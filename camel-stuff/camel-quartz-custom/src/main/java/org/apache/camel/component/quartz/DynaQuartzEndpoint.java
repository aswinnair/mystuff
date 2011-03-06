package org.apache.camel.component.quartz;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Producer;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

public class DynaQuartzEndpoint extends QuartzEndpoint {

	String group;	
	String jobNameProperty;
	Map<String, JobDetail> jobDetails = new HashMap<String, JobDetail>();
	
	public DynaQuartzEndpoint(String endpointUri, QuartzComponent component, String group, String jobNameProperty) {
		super(endpointUri, component);
		this.group = group;
		this.jobNameProperty = jobNameProperty;
	}

    public Producer createProducer() throws Exception {
    	return new QuartzProducer(this, createExpression(jobNameProperty));
    }
	
	public JobDetail getJobDetail(String name){
		JobDetail temp = jobDetails.get(name);
		if (temp == null) {
			temp = new JobDetail(name, group, CamelJob.class);
			jobDetails.put(name, temp);
		}		
		return temp;
	}
	
	
	
    public Exchange createExchange(final JobExecutionContext jobExecutionContext) {
        Exchange exchange = createExchange();
        exchange.setIn(new QuartzMessage(exchange, jobExecutionContext));
        jobExecutionContext.getJobDetail().getName();
        return exchange;
    }
	
	Expression createExpression(final String name){
		return new Expression() {			
			public <T> T evaluate(Exchange exchange, Class<T> type) {				
				return type.cast(exchange.getProperty(name));
			}
		};
	}

	public SimpleTrigger createTrigger(String jobName) {
		SimpleTrigger simpleTrigger = new SimpleTrigger(group, jobName);
		return simpleTrigger;
	}
	
	
    public synchronized void consumerStarted(final QuartzConsumer consumer) throws SchedulerException {
        getLoadBalancer().addProcessor(consumer.getProcessor());
     
    }

    public synchronized void consumerStopped(final QuartzConsumer consumer) throws SchedulerException {
        getLoadBalancer().removeProcessor(consumer.getProcessor());
    }

    @Override
    protected String createEndpointUri() {
        return "quartz://" + getGroup() + "/" + getTrigger().getName();
    }

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}    
}
