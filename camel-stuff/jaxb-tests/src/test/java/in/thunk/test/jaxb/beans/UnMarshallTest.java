package in.thunk.test.jaxb.beans;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Assert;

import org.junit.Test;

public class UnMarshallTest {

	
	@Test
	public void testMarshall() throws JAXBException{
		InputStream xmlInStream = UnMarshallTest.class.getResourceAsStream("Kickstart.xml");
		Assert.assertNotNull(xmlInStream);
		KickstartInstallationType kickstart = unmarshal(KickstartInstallationType.class, xmlInStream);
				
		Assert.assertEquals("http://localhost:8080/installservices/kickstart-agg/v1/initrd", kickstart.getInitrdFileUrl());
		Assert.assertEquals("profiles/kickstart-agg/v1/initrd.gz", kickstart.getInitrdFileLocation());
		Assert.assertEquals("http://localhost:8080/installservices/kickstart-agg/v1/vmlinuz", kickstart.getKernelFileUrl());
		Assert.assertEquals("profiles/kickstart-agg/v1/vmlinuz", kickstart.getKernelLocation());
		Assert.assertEquals("http://localhost:8080/installservices/kickstart-agg/v1/pxeconfig.template?mac=aa:bb:cc:dd:ee:ff", kickstart.getPxeConfigFileUrl());
		Assert.assertEquals("pxelinux.cfg/01-aa-bb-cc-dd-ee-ff", kickstart.getPxeConfigFileLocation());
		System.out.println(kickstart);
		
		xmlInStream = UnMarshallTest.class.getResourceAsStream("Preseed.xml");
		PreseedInstallationType preseed = unmarshal(PreseedInstallationType.class, xmlInStream);
		
		Assert.assertEquals("http://localhost:8080/installservices/preseed-sbe/v1/initrd", preseed.getInitrdFileUrl());
		Assert.assertEquals("profiles/preseed-sbe/v1/initrd.gz", preseed.getInitrdFileLocation());
		Assert.assertEquals("http://localhost:8080/installservices/preseed-sbe/v1/vmlinuz", preseed.getKernelFileUrl());
		Assert.assertEquals("profiles/preseed-sbe/v1/vmlinuz", preseed.getKernelLocation());
		Assert.assertEquals("http://localhost:8080/installservices/preseed-sbe/v1/pxeconfig.template?mac=aa:bb:cc:dd:ee:ff", preseed.getPxeConfigFileUrl());
		Assert.assertEquals("pxelinux.cfg/01-ff-ff-ff-ff-ff-ff", preseed.getPxeConfigFileLocation());
		System.out.println(preseed);
		
	}
		
	public <T> T unmarshal( Class<T> klass, InputStream inputStream )  throws JAXBException {
	    JAXBContext jc = JAXBContext.newInstance(KickstartInstallationType.class, PreseedInstallationType.class);
	    Unmarshaller u = jc.createUnmarshaller();
	    StreamSource source = new StreamSource(inputStream);
	    JAXBElement<T> jaxbElement = (JAXBElement<T>)u.unmarshal(source, klass);
	    return jaxbElement.getValue();
	}
}
