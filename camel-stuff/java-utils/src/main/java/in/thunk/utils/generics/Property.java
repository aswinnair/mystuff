package in.thunk.utils.generics;

import java.util.Map;

public abstract class Property<E> {

	Class<E> type;
	private Property(){
		
	}

	private Class<E> getType() {
		if (type == null) {
			type = TypeUtils.findMaterializedType(Property.class, getClass(), 0);
		}
		return type;
	}
	
	public E get(Map<Property<E>, Object> context){
		Object object = context.get(this);
		return getType().cast(object);
	}

	
	public static Property<String> STRING = new StringProperty();
	public static Property<Integer> INTEGER = new IntProperty();
	
	public static class StringProperty extends Property<String> {}
	
	public static class IntProperty extends Property<Integer> {}
	
	public static class FloatProperty extends Property<String> {}
	
}
