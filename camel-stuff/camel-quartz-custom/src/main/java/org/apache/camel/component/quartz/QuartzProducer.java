package org.apache.camel.component.quartz;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.impl.DefaultProducer;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;

public class QuartzProducer extends DefaultProducer {

	DynaQuartzEndpoint quartzEndpoint;
	Expression producerExpression;
	
	public QuartzProducer(DynaQuartzEndpoint quartzEndpoint, Expression producerExpression) {
		super(quartzEndpoint);
		this.quartzEndpoint = quartzEndpoint;
		this.producerExpression = producerExpression;
	}

	public void process(Exchange exchange) throws Exception {
		
		String jobName = producerExpression.evaluate(exchange, String.class);
		
		JobDetail jobDetail = quartzEndpoint.getJobDetail(jobName);
				
		quartzEndpoint.addTrigger(quartzEndpoint.createTrigger(jobName), jobDetail);
	}
	
}
