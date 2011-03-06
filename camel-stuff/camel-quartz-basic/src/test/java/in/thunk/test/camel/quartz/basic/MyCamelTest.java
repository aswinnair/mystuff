package in.thunk.test.camel.quartz.basic;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration
public class MyCamelTest extends AbstractJUnit4SpringContextTests{

    @Autowired
    protected ProducerTemplate producer;

    @Test
    public void testMocksAreValid() throws Exception {
		
		producer.sendBody("direct:in", "some text");
		
		Thread.sleep(1000 * 1000);
    }
}
