package in.thunk.quartz.test;
import in.thunk.quartz.test.jobs.MyTestJobClass;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzTest {
	static int counter;
	
    public static void main(String[] args) throws InterruptedException {
    	Logger logger = LoggerFactory.getLogger(QuartzTest.class);
        try {
            // Grab the Scheduler instance from the Factory 
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();
        	
         	
         // Schedule the job with the trigger
            JobDetail job = scheduler.getJobDetail("job1", "group1");
            if (job == null) {
            	job = new JobDetail("job1", "group1", MyTestJobClass.class);
                job.setDurability(true);
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("jobKey", "JobKeyValue");
    			job.setJobDataMap(jobDataMap);
    			scheduler.scheduleJob(job, createTrigger(newTriggerName()));
    			
            } else {
            	logger.info("Using existing JobDetail with name:{} and group:{}", job.getName(), job.getGroup());
            	for (int i = 0; i < 5; i++) {
            		scheduler.scheduleJob(createTrigger(newTriggerName(), job));
                	logger.info("Created trigger {} {} {}", new Object[]{Integer.valueOf(i), job.getName(), job.getGroup()});
				}
            	//scheduler.triggerJob(job.getName(), job.getGroup(), triggerMap);
            }
            
            
         Thread.sleep(100000);
         scheduler.shutdown(true);

        } catch (SchedulerException se) {
           logger.error("Error scheduling", se);
        }
    }

	private static String newTriggerName() {
		return "trigger" + counter++;
	}
    
    private static Trigger createTrigger(String name) {
    	return createTrigger(name, null);
    }
    
	private static Trigger createTrigger(String name, JobDetail jobDetail) {
		// Define a Trigger that will fire "now"
		Trigger trigger = new SimpleTrigger(name, "group1", new Date());
		trigger.setVolatility(true);
		JobDataMap triggerMap = new JobDataMap();
		triggerMap.put("triggerKey", "TriggerKeyValue");
		trigger.setJobDataMap(triggerMap);
		if (null != jobDetail) {
			trigger.setJobName(jobDetail.getName());
			trigger.setJobGroup(trigger.getGroup());
		}		
		return trigger;
	}
}