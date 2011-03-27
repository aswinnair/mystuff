package in.thunk.test.jaxb.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="preseed")
public class PreseedInstallationType extends InstallationType{

	@Override
	public String toString() {
		return "PreseedInstallationType [getKernelFileUrl()=" + getKernelFileUrl() + ", getKernelLocation()=" + getKernelLocation()
				+ ", getInitrdFileUrl()=" + getInitrdFileUrl() + ", getInitrdFileLocation()=" + getInitrdFileLocation()
				+ ", getPxeConfigFileUrl()=" + getPxeConfigFileUrl() + ", getPxeConfigFileLocation()=" + getPxeConfigFileLocation()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
}
