package in.thunk.test.camel.http.basic;

import static in.thunk.test.camel.http.basic.ExpressionHelper.logProcessor;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.bean.BeanInvocation;
import org.apache.camel.component.http.HttpMethods;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.camel.util.URISupport;
import org.springframework.stereotype.Component;

@Component
public class TestingRoute extends SpringRouteBuilder {
	JaxbDataFormat jaxb = new JaxbDataFormat("in.thunk.test.camel.http.basic.bean.generated");
	
	@Override
	public void configure() throws Exception {
		
		from("direct:imgmgmt")
			.process(logProcessor(body(), "What body >>"))
			.process(mapToQueryParms).end() //if arg is map,convert it to string params
			.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
			.setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
			.setHeader(Exchange.ACCEPT_CONTENT_TYPE, constant("text/xml"))
			.process(logProcessor(body(), " TO POST>>>>"))
		.to("http://localhost:9090/imgmgnt/osprofiles/sbe/v5/install-conf")
		//.convertBodyTo(String.class).process(logProcessor(body(), " RESULT >>>>"))
		.unmarshal(jaxb)
		;  
		
		
		
		//A route that does the whole processing, including requesting for the URLs from the response.
		from("direct:imgmgmtAndProcess")
		.choice().when(body().isInstanceOf(Map.class)).process(mapToQueryParms).end() //if arg is map,convert it to string params
			.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
			.setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
			.setHeader(Exchange.ACCEPT_CONTENT_TYPE, constant("text/xml"))
		.process(logProcessor(body(), " TO POST>>>>"))
		.to("http://localhost:9090/imgmgnt/osprofiles/sbe/v5/install-conf")
			.convertBodyTo(String.class).process(logProcessor(body(), " RESULT >>>>"))
		.unmarshal(jaxb)	
		.split(simple("${body.resource}"))  //lets test if we can request those links, so start by split each resource and then get them
			.process(logProcessor(simple("${body.url}"), " URL to Call >> "))
			.setHeader(Exchange.HTTP_URI, simple("${body.url}")).setBody(constant(""))
			.to("http://localhost:9090/imgmgnt").transform().body(String.class)
			.process(logProcessor(body(), "Result of call >> "))
			.end()
	;  
		
		
		//test xpath stuff, not used here
		from("direct:xpath").transform().body(String.class)
		.process(logProcessor(body(), "httpCallResult"))
		.transform(xpath("/resources/path").resultType(List.class))		
		.split(body())
		.transform(xpath("concat('-s ', //src, '  -d ', //dest)").stringResult());
	}

	
	Processor mapToQueryParms = new Processor() {
		
		public void process(Exchange exchange) throws Exception {
			Object body = exchange.getIn().getBody();			
			
			if (body instanceof BeanInvocation) {
				BeanInvocation beanInvoke = (BeanInvocation) body;
				Object arg = beanInvoke.getArgs().length == 1 ? beanInvoke.getArgs()[0] : null;
				body = arg != null ? arg : body;
			}
			
			if (body instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> map = (Map<Object, Object>) body;
				exchange.getIn().setBody(URISupport.createQueryString(map));	
			}
			
			// else nothing			
		}
	};
}
