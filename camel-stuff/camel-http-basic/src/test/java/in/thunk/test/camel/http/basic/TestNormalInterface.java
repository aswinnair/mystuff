package in.thunk.test.camel.http.basic;

import in.thunk.test.camel.http.basic.bean.generated.ResourceType;
import in.thunk.test.camel.http.basic.bean.generated.ResourcesType;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**"
 * A simple example showing how to use regular interface and wiring camle on the backend.
 * @author aswin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestNormalInterface {

	@Autowired
	ImageManagementService imageManagementService;
	

	@Test
	public void callLocalRoute() throws URISyntaxException{
		
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("hostName", "aswin-test-instance");
		map.put("ip", "44.44.44.77");
		map.put("sp-mac", "aa:bb:cc:dd:ee:ff");
		map.put("net0-mac", "ff:ee:dd:cc:bb:aa");	
		map.put("os", "ubuntu");
		map.put("osProfile", "sbe/v5");
		map.put("os", "ubuntu");

		ResourcesType resources = imageManagementService.getResources(map);
		
		Assert.assertNotNull(resources);
		Assert.assertTrue(resources.getResource().size() > 1);
		for (ResourceType resource: resources.getResource()){
			Assert.assertNotNull(resource.getUrl());
			System.out.println(resource.getUrl());
		}		
	}
		
	
	
}
