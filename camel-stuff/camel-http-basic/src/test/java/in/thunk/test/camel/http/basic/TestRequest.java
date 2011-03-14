package in.thunk.test.camel.http.basic;

import in.thunk.test.camel.http.basic.bean.generated.ResourcesType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.http.HttpMethods;
import org.apache.camel.util.URISupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestRequest {

	@Autowired
	ProducerTemplate template;
	

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
		String paramString = URISupport.createQueryString(map);
		
		Object response = template.requestBody("direct:imgmgmtAndProcess", paramString);
		ResourcesType resources = (ResourcesType) response;
		
		Assert.assertNotNull(resources);
	}
		
	
	//send request to a remote HTTP server, using POST and getting teh response.
	@Test
	public void sendRemoteHttpRequest() throws URISyntaxException, IOException{
		String endpointUri = "http://localhost:9090/imgmgnt/osprofiles/sbe/v5/install-conf";
		
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("hostName", "aswin-test-instance");
		map.put("ip", "44.44.44.77");
		map.put("sp-mac", "aa:bb:cc:dd:ee:ff");
		map.put("eth0-mac", "ff:ee:dd:cc:bb:aa");
		map.put("os-profile", "sbe-ubuntu1010-aggnode-1");
		String paramString = URISupport.createQueryString(prefixify("tparams.", map));
				
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.HTTP_METHOD, HttpMethods.POST);
		headers.put(Exchange.CONTENT_TYPE, "application/x-www-form-urlencoded");
		headers.put(Exchange.ACCEPT_CONTENT_TYPE, "text/xml");
	
		Object respXMLInStream = template.requestBodyAndHeaders(endpointUri, paramString, headers);
		
		Assert.assertNotNull(respXMLInStream);
		System.out.println(String.format("Result is[[ %s ]] " , FileCopyUtils.copyToString(new InputStreamReader((InputStream) respXMLInStream))));
	}


	private Map<Object, Object> prefixify(String string, Map<Object, Object> map) {
		Map<Object, Object> newMap = new HashMap<Object, Object>();
		for(Entry<Object, Object> s : map.entrySet()){
			newMap.put(string + s.getKey().toString(), s.getValue());
		}
		return newMap;
	}
	
}
