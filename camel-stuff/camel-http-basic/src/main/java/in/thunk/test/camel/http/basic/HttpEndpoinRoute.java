package in.thunk.test.camel.http.basic;

import static in.thunk.test.camel.http.basic.ExpressionHelper.counterExpression;
import static in.thunk.test.camel.http.basic.ExpressionHelper.logProcessor;

import in.thunk.test.camel.http.basic.CamelMessageUtils.FilterType;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class HttpEndpoinRoute  extends SpringRouteBuilder {
		
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String TEMPLATE = "template";
	private static final String OSP_ID = "ospId";
	
	@Override
	public void configure() throws Exception {

		from("jetty:http://0.0.0.0:9090/imgmgnt/osprofiles/sbe/v5/install-conf")
		 .choice()
		 	.when(header(OSP_ID).isNull())
			 	.setHeader(OSP_ID, counterExpression()) 
			 	.removeHeaders("CamelHttp*", "CamelHttpUrl")
			 	.process(createRecord) //create a new entry in temp hash			 	
			 	.to("string-template:templates/Test.tm").removeHeaders("*")
			 	.setHeader(CONTENT_TYPE, constant("text/xml"))
		 	.otherwise()
		 		.process(syncRecord) //retrieve details stored earlier
		 		.process(logProcessor(ExpressionBuilder.headersExpression(), "headers"))
		 		.choice()
			 		.when(header(TEMPLATE).contains("ubuntu"))
			 			.to("string-template:templates/pxeconfig-ubuntu.tm")
			 		.when(header(TEMPLATE).contains("fedora"))
			 			.to("string-template:templates/pxeconfig-fedora.tm")			 			
			 		.when(header(TEMPLATE).contains("preseed"))
			 			.to("string-template:templates/preseed.tm")
			 		.when(header(TEMPLATE).contains("kickstart"))
			 			.to("string-template:templates/kickstart.tm")			 			
			 		.otherwise()			 			
			 			.throwException(new Exception("Unknown request"))
			 		.end()	
			 	.removeHeader("*")	
			 	.setHeader("Content-type", constant("text"))
		    .end()
		 //.process(logProcessor(body(), "bodyTemplate"))
		 ;
		
		
		from("jetty:http://0.0.0.0:9090/imgmgnt/static")
		//.process(logProcessor(ExpressionBuilder.headersExpression(), "Headers from Static request >>"))
		.setBody(constant("STATIC RESOURCE SUCH AS INITRD or VMLINUZ")).setHeader("Content-type", constant("text"));
	}

	
		
	
	
	private WeakHashMap<String, Map<String, Object>> cache = new WeakHashMap<String, Map<String, Object>>();
	
	Processor createRecord = new Processor() {
		
		public void process(Exchange exchange) throws Exception {
			String header = exchange.getIn().getHeader(OSP_ID, String.class);
			
			if (header != null) {
				cache.put(header, exchange.getIn().getHeaders());				
			}
		}
	};

	Processor syncRecord = new Processor() {
		
		public void process(Exchange exchange) throws Exception {
			String header = exchange.getIn().getHeader(OSP_ID, String.class);
			
			if (header != null) {				
				Map<String, Object> templateParams = cache.get(header);				
				if (cache != null) {					
					exchange.getIn().getHeaders().putAll(templateParams);					
				}
			}
		}
	};
	
}