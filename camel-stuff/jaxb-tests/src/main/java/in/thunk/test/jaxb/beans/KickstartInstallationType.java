package in.thunk.test.jaxb.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="kicstart")
public class KickstartInstallationType extends InstallationType{

	@Override
	public String toString() {
		return "KickstartInstallationType [getKernelFileUrl()=" + getKernelFileUrl() + ", getKernelLocation()=" + getKernelLocation()
				+ ", getInitrdFileUrl()=" + getInitrdFileUrl() + ", getInitrdFileLocation()=" + getInitrdFileLocation()
				+ ", getPxeConfigFileUrl()=" + getPxeConfigFileUrl() + ", getPxeConfigFileLocation()=" + getPxeConfigFileLocation()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}	
	
}
