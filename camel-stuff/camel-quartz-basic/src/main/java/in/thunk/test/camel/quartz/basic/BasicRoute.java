package in.thunk.test.camel.quartz.basic;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.Main;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.log4j.Logger;

public class BasicRoute extends SpringRouteBuilder {
	Logger logger = Logger.getLogger(BasicRoute.class);
	
	private final Processor logCustom = new Processor() {
					public void process(Exchange exchange) throws Exception {
						logger.debug("coming here " + exchange.getExchangeId());
					}};

	@Override
	public void configure() throws Exception {
		
					
		from("direct:in").routeId("main")
			.process(logCustom)	
			.setProperty("requestId", constant("1234"))
			.to("quartz://provisionRequest/requestId?dyna=true");
		
		
		from("quartz://provisionRequest/requestId?dyna=true").process(logCustom).throwException(new Exception("testException"));
		
		
	}
	
	public static void main(String[] args) throws Exception {
		Main.main(args);
		ProducerTemplate camelTemplate = Main.getInstance().getCamelTemplate();
		
		
		camelTemplate.sendBody("direct:in", "some text");
	}

}
