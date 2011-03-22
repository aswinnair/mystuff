package in.thunk.camel.component.job;

public class JobKey {

	private String group;
	private String name;
	
	
	public JobKey(String group, String name) {
		super();
		this.group = group;
		this.name = name;
	}

	public String getGroup() {
		return group;
	}
		
	public String getName() {
		return name;
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
