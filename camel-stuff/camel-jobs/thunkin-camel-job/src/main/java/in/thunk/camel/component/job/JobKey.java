package in.thunk.camel.component.job;

import org.springframework.util.StringUtils;

public class JobKey {

	private String group;
	private String name;
	private String[] segments;
	private String url;
	
	public JobKey(String group, String name) {		
		this(group, name, null);
	}	
	
	public JobKey(String group, String name, String ... segments) {
		super();
		this.group = group;
		this.name = name;
		this.segments = segments;
		this.url = String.format("job://%s/%s/%s", getGroup(), getName(), StringUtils.arrayToDelimitedString(segments, "/"));
	}

	public String getGroup() {
		return group;
	}
		
	public String getName() {
		return name;
	}
		
	public String[] getSegments() {
		return segments;
	}

	public String getURL(){
		return url;		 
	}	

	@Override
	public String toString() {
		return "JobKey [group=" + group + ", name=" + name + "]";
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobKey other = (JobKey) obj;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}	
	
}
