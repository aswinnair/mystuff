package in.thunk.test.camel.http.basic;

import java.util.Map;

import in.thunk.test.camel.http.basic.bean.generated.ResourcesType;

public interface ImageManagementService {

	public ResourcesType getResources(Map<Object, Object> requestParams);
}
