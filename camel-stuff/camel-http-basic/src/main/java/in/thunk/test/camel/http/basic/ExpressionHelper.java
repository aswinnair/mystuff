package in.thunk.test.camel.http.basic;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Processor;
import org.apache.camel.builder.ValueBuilder;
import org.apache.log4j.Logger;

public class ExpressionHelper {
	
	static Logger logger = Logger.getLogger(ExpressionHelper.class);
	
	
	public static Expression counterExpression(){

		return new Expression(){
			long counter = 1;
			@SuppressWarnings("unchecked")
			public <T> T evaluate(Exchange exchange, Class<T> type) {
				
				if (Long.class.isAssignableFrom(type)) {
					return type.cast(counter);
				}

				if (type.isAssignableFrom(String.class)){
					return type.cast(Long.toString(counter++));
				}
				
				if (type == null) {
					return (T) Long.valueOf(counter++);
				}
				
				throw new RuntimeException("Cannot convert long to " + type);
			}};
	}
	
	public static Processor logProcessor(final Expression expression, final String key) {
		return new Processor() {			
			public void process(Exchange exchange) throws Exception {
				Object result = expression.evaluate(exchange, Object.class);

				if (result instanceof Map) {
					Map<Object, Object> map = (Map<Object, Object>) result;
					for (Entry<Object, Object> entry : map.entrySet()) {
						logger.info(String.format("%s:Map[%s] = %s", key, entry.getKey(), entry.getValue()));
					}
				} else if (result instanceof Collection<?>) {
					Collection<?> coll = (Collection<?>) result;
					for (Object a: coll) {
						logger.info(String.format("%s:Coll= %s", key, a.toString()));
					}
				} else {
					logger.info(String.format("%s:Unknown= %s", key, result));
				} 				
			}
		};		
	}

	
	
}