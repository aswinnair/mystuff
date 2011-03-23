package in.thunk.camel.component.job;

import org.apache.camel.Exchange;

public interface DataSyncer {

	/**
	 * Save the exchange data of interest to the backing persistence store against the passed in id.
	 * This id itself is derived from the user defined expression  
	 * @param exchange
	 * @param jobIDExpression
	 */
	public void deHydrate(Exchange exchange, String id);

	/**
	 * From the backing persistence store, fetch the data and populate the Exchange appropriately. 
	 * The id used for dehydrating the job is also passed in
	 * @param exchange
	 * @param job
	 */
	public void hydrate(Exchange exchange, String job);
}
