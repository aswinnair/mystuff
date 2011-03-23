package in.thunk.camel.component.job;

import org.apache.camel.Exchange;

public interface JobIDGenerator {

	public String generateID(Exchange exchange);
}
