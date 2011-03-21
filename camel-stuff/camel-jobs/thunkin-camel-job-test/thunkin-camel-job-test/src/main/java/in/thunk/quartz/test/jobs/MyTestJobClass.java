package in.thunk.quartz.test.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyTestJobClass implements StatefulJob {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	 int times;
	
	public void execute(JobExecutionContext context)throws JobExecutionException {
		Scheduler scheduler = context.getScheduler();
	
	
		
			if (times < 3) {
				 JobExecutionException exception = new JobExecutionException(times++ + " times exception");
				 exception.setRefireImmediately(true);
				 logger.info("throwing exception");
				 throw exception;
			}
			
			try {
				logger.debug("Executing job in scheduler {}", scheduler.getSchedulerName());
			} catch (SchedulerException e) {
				logger.error("Exception scheduling job", e);
			}
		
	}

}
