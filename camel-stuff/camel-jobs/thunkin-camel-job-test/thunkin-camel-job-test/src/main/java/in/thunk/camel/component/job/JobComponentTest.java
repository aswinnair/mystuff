package in.thunk.camel.component.job;

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.interceptor.Tracer;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class JobComponentTest extends CamelTestSupport  {

	@Produce(uri = "direct:test") 
	ProducerTemplate producerTemplate;
	
	@Test
	public void testRoute(){
		//Object response = producerTemplate.requestBodyAndHeader("job://group/job/step", "Some text body", "endRoute", "nowhere");
		context.setTracing(true);
		Object response = producerTemplate.requestBodyAndHeader("Some text body", "endRoute", "nowhere");
		System.out.println("Response >> " + response);
	}	
	
		
	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {		
				
			
			@Override
			public void configure() throws Exception {				
				
				getContext().addInterceptStrategy(getTracer());
								
				
				from("direct:test").routeId("test.one")
				 .log( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>my component is being called. huraray!!!!!!!!!!!!!")
				 .to("job://OSP/provisionRequest/collectData");
				
				from("job://OSP/provisionRequest/collectData").routeId("test.two")				 
				 .log("collect data")
				 .to("job://OSP/provisionRequest/bootserverChanges");
				
				from("job://OSP/provisionRequest/bootserverChanges").routeId("test.three")				 
				 .log("start boot changes")
				 .to("job://OSP/provisionRequest/startBootProcess");
				
				from("job://OSP/provisionRequest/startBootProcess").routeId("test.four")				 
				 .log("start boot process")
				 .to("job://OSP/watchProgress");
				
				from("job://OSP/watchProgress").routeId("test.five")				 
				 .log("watching progress")
				 .log(">>>>>>>>>>>>DONE>>>>>>>>>>>>>>>>>>>>>>>>>>");
				 
			}	
		};
	}
	
	private Tracer getTracer() {
		Tracer tracer = new Tracer();
		tracer.setTraceOutExchanges(true);
		tracer.setEnabled(true);		
		tracer.setTraceExceptions(true);
		tracer.setLogLevel(LoggingLevel.INFO);
		return tracer;
	}
}
