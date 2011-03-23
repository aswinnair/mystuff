/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package in.thunk.camel.component.job;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.CamelContext;
import org.apache.camel.StartupListener;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.IntrospectionSupport;
import org.apache.camel.util.ObjectHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import com.google.common.collect.*;

/**
 * A <a href="http://camel.apache.org/quartz.html">Quartz Component</a>
 * <p/>
 * For a brief tutorial on setting cron expression see <a href=
 * "http://www.opensymphony.com/quartz/wikidocs/CronTriggers%20Tutorial.html"
 * >Quartz cron tutorial</a>.
 * 
 * @version $Revision:520964 $
 */
public class JobComponent extends DefaultComponent implements StartupListener {
	private static final transient Log LOG = LogFactory.getLog(JobComponent.class);
	private static final AtomicInteger JOBS = new AtomicInteger();
	private static Scheduler scheduler;
	private final List<JobToAdd> jobsToAdd = new ArrayList<JobToAdd>();
	private SchedulerFactory factory;
	private Properties properties;
	private String propertiesFile;
	private int startDelayedSeconds;
	private boolean autoStartScheduler = true;
	Multimap<JobKey, JobEndpoint> endpoints = HashMultimap.create(); 
	
	

	private final class JobToAdd {
		private final JobDetail job;
		private final Trigger trigger;

		private JobToAdd(JobDetail job, Trigger trigger) {
			this.job = job;
			this.trigger = trigger;
		}

		public JobDetail getJob() {
			return job;
		}

		public Trigger getTrigger() {
			return trigger;
		}
	}

	public JobComponent() {
	}

	public JobComponent(final CamelContext context) {
		super(context);		
	}

	@Override
	protected JobEndpoint createEndpoint(final String uri, final String remaining, final Map<String, Object> parameters) throws Exception {

		// lets split the remaining into a group/name
		URI u = new URI(uri);
		String jobString = ObjectHelper.after(u.getPath(), "/");
		String group = u.getHost();

		String[] fragments = jobString.split("/");
		String name = fragments[0];
		String stepName = fragments.length > 1 ? fragments[1] : null;
		
		
		String expression = getAndRemoveParameter(parameters, "jobId", String.class);
		String cron = getAndRemoveParameter(parameters, "cron", String.class);
		Boolean fireNow = getAndRemoveParameter(parameters, "fireNow", Boolean.class, Boolean.FALSE);

		if (ObjectHelper.isEmpty(group) || ObjectHelper.isEmpty(name)) {
			throw new IllegalArgumentException("Cannot create a Job Endopoint without Group and Job Names");
		}

		Map<String, Object> triggerParameters = IntrospectionSupport.extractProperties(parameters, "trigger.");
		Map<String, Object> jobParameters = IntrospectionSupport.extractProperties(parameters, "job.");

		JobKey key = new JobKey(group, name, stepName);

		Collection<JobEndpoint> tasksEndpoints = endpoints.get(key);
		
		for (JobEndpoint jobEndpoint : tasksEndpoints) {
			if (uri.equals(jobEndpoint.getEndpointUri())){
				
				if (!jobParameters.isEmpty()) {
					if (jobEndpoint.isJobParamSet()) {
						throw new IllegalArgumentException("Job parameters can be set only once for a specific job. Failing at uri " + uri);
					}
					setProperties(jobEndpoint.getJobDetail(), jobParameters);
					jobEndpoint.setJobParamSet(true);
				}				
				return jobEndpoint;
			}
		}
		
		JobEndpoint answer = new JobEndpoint(key, expression, this);
		tasksEndpoints.add(answer);

		answer.setStateful(true); //set it true by default, and the route definition can override
		setProperties(answer.getJobDetail(), jobParameters);
		
		if (! triggerParameters.isEmpty()) { 			// setting regular Quartz consumer with trigger and stuff

			Trigger trigger = null;
	
			// if we're starting up and not running in Quartz clustered mode
			// then check for a name conflict.
			if (!isClustered()) {
				// check to see if this trigger already exists
				trigger = getScheduler().getTrigger(name, group);
				if (trigger != null) {
					String msg = "A Quartz job already exists with the name/group: " + name + "/" + group;
					throw new IllegalArgumentException(msg);
				}
			}
	
			// create the trigger either cron or simple
			if (ObjectHelper.isNotEmpty(cron)) {
				trigger = createCronTrigger(cron);
			} else {
				trigger = new SimpleTrigger();
				if (fireNow) {
					String intervalString = (String) triggerParameters.get("repeatInterval");
					if (intervalString != null) {
						long interval = Long.valueOf(intervalString);
						trigger.setStartTime(new Date(System.currentTimeMillis() - interval));
					}
				}
			}
	
			setProperties(trigger, triggerParameters);
			trigger.setName(name);
			trigger.setGroup(group);
	
			answer.addTrigger(trigger);
		}

		return answer;

	}

	protected CronTrigger createCronTrigger(String path) throws ParseException {
		// replace + back to space so it's a cron expression
		path = path.replaceAll("\\+", " ");
		CronTrigger cron = new CronTrigger();
		cron.setCronExpression(path);
		return cron;
	}

	public void onCamelContextStarted(CamelContext camelContext, boolean alreadyStarted) throws Exception {
		if (scheduler != null) {
			// register current camel context to scheduler so we can look it up
			// when jobs is being triggered
			scheduler.getContext().put(JobConstants.QUARTZ_CAMEL_CONTEXT + "-" + getCamelContext().getName(), getCamelContext());
		}

		// if not configure to auto start then don't start it
		if (!isAutoStartScheduler()) {
			LOG.info("QuartzComponent configured to not auto start Quartz scheduler.");
			return;
		}

		// only start scheduler when CamelContext have finished starting
		startScheduler();
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		if (scheduler == null) {
			scheduler = getScheduler();
		}
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();

		if (scheduler != null) {
			int number = JOBS.get();
			if (number > 0) {
				LOG.info("Cannot shutdown Quartz scheduler: " + scheduler.getSchedulerName() + " as there are still " + number
						+ " jobs registered.");
			} else {
				// no more jobs then shutdown the scheduler
				LOG.info("There are no more jobs registered, so shutting down Quartz scheduler: " + scheduler.getSchedulerName());
				scheduler.shutdown();
				scheduler = null;
			}
		}
	}

	public void addJob(JobDetail job, Trigger trigger) throws SchedulerException {
		if (scheduler == null) {
			// add job to internal list because we will defer adding to the
			// scheduler when camel context has been fully started
			jobsToAdd.add(new JobToAdd(job, trigger));
		} else {
			// add job directly to scheduler
			doAddJob(job, trigger);
		}
	}

	private void doAddJob(JobDetail job, Trigger trigger) throws SchedulerException {
		JOBS.incrementAndGet();

		Trigger existingTrigger = getScheduler().getTrigger(trigger.getName(), trigger.getGroup());
		if (existingTrigger == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Adding job using trigger: " + trigger.getGroup() + "/" + trigger.getName());
			}
			getScheduler().scheduleJob(job, trigger);
		} else if (hasTriggerChanged(existingTrigger, trigger)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Trigger: " + trigger.getGroup() + "/" + trigger.getName() + " already exists and will be updated by Quartz.");
			}
			scheduler.addJob(job, true);
			trigger.setJobName(job.getName());
			scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Trigger: " + trigger.getGroup() + "/" + trigger.getName()
						+ " already exists and will be resumed automatically by Quartz.");
			}
			if (!isClustered()) {
				scheduler.resumeTrigger(trigger.getName(), trigger.getGroup());
			}
		}
	}

	private boolean hasTriggerChanged(Trigger oldTrigger, Trigger newTrigger) {
		if (oldTrigger instanceof CronTrigger && oldTrigger.equals(newTrigger)) {
			CronTrigger oldCron = (CronTrigger) oldTrigger;
			CronTrigger newCron = (CronTrigger) newTrigger;
			return !oldCron.getCronExpression().equals(newCron.getCronExpression());
		} else {
			return !newTrigger.equals(oldTrigger);
		}
	}

	public void pauseJob(Trigger trigger) throws SchedulerException {
		JOBS.decrementAndGet();

		if (isClustered()) {
			// do not pause jobs which are clustered, as we want the jobs to
			// continue running on the other nodes
			if (LOG.isDebugEnabled()) {
				LOG.debug("Cannot pause job using trigger: " + trigger.getGroup() + "/" + trigger.getName()
						+ " as the JobStore is clustered.");
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Pausing job using trigger: " + trigger.getGroup() + "/" + trigger.getName());
			}
			getScheduler().pauseTrigger(trigger.getName(), trigger.getGroup());
			getScheduler().pauseJob(trigger.getName(), trigger.getGroup());
		}
	}

	public void deleteJob(String name, String group) throws SchedulerException {
		if (isClustered()) {
			// do not pause jobs which are clustered, as we want the jobs to
			// continue running on the other nodes
			if (LOG.isDebugEnabled()) {
				LOG.debug("Cannot delete job using trigger: " + group + "/" + name + " as the JobStore is clustered.");
			}
		} else {
			Trigger trigger = getScheduler().getTrigger(name, group);
			if (trigger != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Deleting job using trigger: " + group + "/" + name);
				}
				getScheduler().unscheduleJob(name, group);
			}
		}
	}

	/**
	 * To force shutdown the quartz scheduler
	 * 
	 * @throws SchedulerException
	 *             can be thrown if error shutting down
	 */
	public void shutdownScheduler() throws SchedulerException {
		if (scheduler != null) {
			LOG.info("Forcing shutdown of Quartz scheduler: " + scheduler.getSchedulerName());
			scheduler.shutdown();
			scheduler = null;
		}
	}

	/**
	 * Is the quartz scheduler clustered?
	 */
	public boolean isClustered() throws SchedulerException {
		try {
			return getScheduler().getMetaData().isJobStoreClustered();
		} catch (NoSuchMethodError e) {
			LOG.debug("Job clustering is only supported since Quartz 1.7, isClustered returning false");
			return false;
		}
	}

	/**
	 * To force starting the quartz scheduler
	 * 
	 * @throws SchedulerException
	 *             can be thrown if error starting
	 */
	public void startScheduler() throws SchedulerException {
		for (JobToAdd add : jobsToAdd) {
			doAddJob(add.getJob(), add.getTrigger());
		}
		jobsToAdd.clear();

		if (!getScheduler().isStarted()) {
			if (getStartDelayedSeconds() > 0) {
				LOG.info("Starting Quartz scheduler: " + getScheduler().getSchedulerName() + " delayed: " + getStartDelayedSeconds()
						+ " seconds.");
				try {
					getScheduler().startDelayed(getStartDelayedSeconds());
				} catch (NoSuchMethodError e) {
					LOG.warn("Your version of Quartz is too old to support delayed startup! " + "Starting Quartz scheduler immediately : "
							+ getScheduler().getSchedulerName());
					getScheduler().start();
				}
			} else {
				LOG.info("Starting Quartz scheduler: " + getScheduler().getSchedulerName());
				getScheduler().start();
			}
		}
	}

	// Properties
	// -------------------------------------------------------------------------

	public SchedulerFactory getFactory() throws SchedulerException {
		if (factory == null) {
			factory = createSchedulerFactory();
		}
		return factory;
	}

	public void setFactory(final SchedulerFactory factory) {
		this.factory = factory;
	}

	public synchronized Scheduler getScheduler() throws SchedulerException {
		if (scheduler == null) {
			scheduler = createScheduler();
		}
		return scheduler;
	}

	public void setScheduler(final Scheduler scheduler) {
		JobComponent.scheduler = scheduler;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getPropertiesFile() {
		return propertiesFile;
	}

	public void setPropertiesFile(String propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

	public int getStartDelayedSeconds() {
		return startDelayedSeconds;
	}

	public void setStartDelayedSeconds(int startDelayedSeconds) {
		this.startDelayedSeconds = startDelayedSeconds;
	}

	public boolean isAutoStartScheduler() {
		return autoStartScheduler;
	}

	public void setAutoStartScheduler(boolean autoStartScheduler) {
		this.autoStartScheduler = autoStartScheduler;
	}

	// Implementation methods
	// -------------------------------------------------------------------------

	protected Properties loadProperties() throws SchedulerException {
		Properties answer = getProperties();
		if (answer == null && getPropertiesFile() != null) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Loading Quartz properties file from classpath: " + getPropertiesFile());
			}
			InputStream is = getCamelContext().getClassResolver().loadResourceAsStream(getPropertiesFile());
			if (is == null) {
				throw new SchedulerException("Quartz properties file not found in classpath: " + getPropertiesFile());
			}
			answer = new Properties();
			try {
				answer.load(is);
			} catch (IOException e) {
				throw new SchedulerException("Error loading Quartz properties file from classpath: " + getPropertiesFile(), e);
			}
		}
		return answer;
	}

	protected SchedulerFactory createSchedulerFactory() throws SchedulerException {
		Properties prop = loadProperties();
		if (prop != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Creating SchedulerFactory with properties: " + prop);
			}
			return new StdSchedulerFactory(prop);
		} else {
			return new StdSchedulerFactory();
		}
	}

	protected Scheduler createScheduler() throws SchedulerException {
		Scheduler scheduler = getFactory().getScheduler();
		// register current camel context to scheduler so we can look it up when
		// jobs is being triggered
		scheduler.getContext().put(JobConstants.QUARTZ_CAMEL_CONTEXT + "-" + getCamelContext().getName(), getCamelContext());
		return scheduler;
	}
}
