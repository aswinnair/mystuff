package in.thunk.test.camel.quartz.basic;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.spring.Main;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.log4j.Logger;
import static org.apache.camel.builder.ExpressionBuilder.*;
import static org.apache.camel.builder.ProcessorBuilder.*;

public class BasicRoute extends SpringRouteBuilder {
	Logger logger = Logger.getLogger(BasicRoute.class);
	
	private final Processor logCustom = new Processor() {
					public void process(Exchange exchange) throws Exception {
						logger.debug("coming here " + exchange.getExchangeId());
						System.out.println(exchange);
					}};
	
	AggregationStrategy aggregationStrategy = new AggregationStrategy() {
		
		@Override
		public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
			if (oldExchange != null) {
				oldExchange.getOut().setBody(oldExchange.getOut().getBody(String.class) + " blah " +  newExchange.getOut().getBody(String.class));
			}
			return oldExchange;
		}
	};

	@Override
	public void configure() throws Exception {
		
		from("direct:in").routeId("main")
			.process(logCustom)	
			.setProperty("requestId", constant("1234,12345"))
			.split(body().tokenize(","), aggregationStrategy)
				.process(logCustom)
				.to("seda:test")
				.end()
			.to("quartz://provisionRequest/requestId?dyna=true");
		
		
		from("quartz://provisionRequest/requestId?dyna=true").process(logCustom);
		
		from("seda:test").process(logCustom);
		
	}
	
	public static void main(String[] args) throws Exception {
		Main.main(args);
		ProducerTemplate camelTemplate = Main.getInstance().getCamelTemplate();
		
		
		camelTemplate.sendBody("direct:in", "some text");
	}

}
