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

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JobProducer
 */
public class JobProducer extends DefaultProducer {
    private static Logger logger = LoggerFactory.getLogger(JobProducer.class);

    public JobProducer(JobEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public JobEndpoint getEndpoint(){
    	return (JobEndpoint) super.getEndpoint();
    }
    
    public void process(Exchange exchange) throws Exception {
       logger.info("Save state to persister");
       
       if (null == exchange.getProperty(JobConstants.JOB_ID)){
    	   //schedule a job and return
    	   JobDetail jobDetail = createJobDetail(exchange);
    	   getEndpoint().addTrigger(new SimpleTrigger(), jobDetail);    	   
    	   return;
       }
       
       //this is just a step and we are already in a Quartz job context, sync the data using the syncer and process inline
       
       getEndpoint().getJobDataPersister().deHydrate(exchange, exchange.getProperty(JobConstants.JOB_ID, String.class));       
       getEndpoint().getConsumer().getProcessor().process(exchange);
        
    }

	private JobDetail createJobDetail(Exchange exchange) {
		JobKey jobkey = getEndpoint().getJobKey();
		String jobId = getEndpoint().getIdGenerator().generateID(exchange);
		
		JobDetail jobDetail = new JobDetail(String.format("%s_%s",jobkey.getName(), jobId) , jobkey.getGroup(), StatefulCamelJob.class);
		jobDetail.getJobDataMap().put(JobConstants.JOB_ID, jobId);
		return jobDetail;
	}

}
