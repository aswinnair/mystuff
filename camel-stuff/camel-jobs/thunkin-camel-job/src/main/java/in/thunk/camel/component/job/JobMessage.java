package in.thunk.camel.component.job;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultMessage;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;

public class JobMessage extends DefaultMessage {

    private final JobExecutionContext jobExecutionContext;

    public JobMessage(Exchange exchange, JobExecutionContext jobExecutionContext) {
        this.jobExecutionContext = jobExecutionContext;
        setExchange(exchange);
        // do not set body as it should be null
    }

    public JobExecutionContext getJobExecutionContext() {
        return jobExecutionContext;
    }

    @Override
    protected void populateInitialHeaders(Map<String, Object> map) {
        super.populateInitialHeaders(map);
        if (jobExecutionContext != null) {
            map.put("calendar", jobExecutionContext.getCalendar());
            map.put("fireTime", jobExecutionContext.getFireTime());
            map.put("jobDetail", jobExecutionContext.getJobDetail());
            map.put("jobInstance", jobExecutionContext.getJobInstance());
            map.put("jobRunTime", jobExecutionContext.getJobRunTime());
            map.put("mergedJobDataMap", jobExecutionContext.getMergedJobDataMap());
            map.put("nextFireTime", jobExecutionContext.getNextFireTime());
            map.put("previousFireTime", jobExecutionContext.getPreviousFireTime());
            map.put("refireCount", jobExecutionContext.getRefireCount());
            map.put("result", jobExecutionContext.getResult());
            map.put("scheduledFireTime", jobExecutionContext.getScheduledFireTime());
            map.put("scheduler", jobExecutionContext.getScheduler());
            Trigger trigger = jobExecutionContext.getTrigger();
            map.put("trigger", trigger);
            map.put("triggerName", trigger.getName());
            map.put("triggerGroup", trigger.getGroup());
        }
    }
}
