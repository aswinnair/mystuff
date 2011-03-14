package in.thunk.test.camel.http.basic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

public class CamelMessageUtils {

	public enum FilterType{Include, Exclude}
	
	public static Map<String, Object> filterInMessages(String filterPattern, Exchange exchange, FilterType filterType){
		if (exchange == null || filterPattern == null) {
			return null;
		}		
		return filterMessage(filterPattern, exchange.getIn(), filterType);
	}
	
	public static Map<String, Object> filterOutMessages(String filterPattern, Exchange exchange, FilterType filterType){
		if (exchange == null || filterPattern == null) {
			return null;
		}		
		return filterMessage(filterPattern, exchange.getOut(), filterType);
	}
	
	public static Map<String, Object> filterMessage(String filterPattern, Message message, FilterType filterType){		
		Map<String, Object> headers = message.getHeaders();
		return filterMessage(filterPattern, headers, filterType);
	}
	
	public static Map<String, Object> filterMessage(String filterPattern, Map<String, Object> headers, FilterType filterType){
		
		if (filterType == null) {
			filterType = FilterType.Include;
		}
		
		Pattern hPattern = Pattern.compile(filterPattern);		
		
		Map<String, Object> filteredHeaders = new HashMap<String, Object>();
		
		for (Entry<String, Object> s: headers.entrySet()) {
			if (hPattern.matcher(s.getKey()).matches()){
				if (FilterType.Include.equals(filterType)) { 
					filteredHeaders.put(s.getKey(), s.getValue());
				}
			} else if (FilterType.Exclude.equals(filterType)){
				filteredHeaders.put(s.getKey(), s.getValue());
			}
		}		
		
		return filteredHeaders;
	}
	
}
