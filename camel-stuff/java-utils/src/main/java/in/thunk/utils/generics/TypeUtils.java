package in.thunk.utils.generics;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class TypeUtils {
	
	@SuppressWarnings("unchecked")
	public static <T> T toT(Type t){
		
		return (T) t;
	}
	
	/**
	 * 
	 * @param rootType  The root type that has defined a Type variable 
	 * @param child  The child of the root type from which to start the search
	 * @param tParamIndex The index of the type variable in the root type for which we are trying to find the most specific materialized type
	 * @return The most specific materialized type of the Type parameter at index tParamIndex of the rootType 
	 * @throws If any of the subclass are not properly parameterized
	 */
	public static <T> Class<T> findMaterializedType(Class<?> rootType, Class<?> child, int tParamIndex) {
		TypeIndex matTypeIndex = _findType(rootType, child, tParamIndex);
		
		if (matTypeIndex.isTypeVariable() || matTypeIndex.isWildCard()) {
			throw new IllegalArgumentException("Cannot convert to type");
		}
						
		return (Class<T>) matTypeIndex.type;
	}
	
	

	public static TypeIndex _findType(Class<?> rootType, Class<?> child, int tParamIndex) {
			
		if (child.equals(Object.class)){
			throw new IllegalArgumentException("Child is not in the heirarchy of rootType speified");
		}		
				
		if (!rootType.equals(child.getSuperclass())) {
			
			TypeIndex typeIdx = _findType(rootType, child.getSuperclass(), tParamIndex);			
						
			if (typeIdx.isTypeVariable()) { //find the subclass type variable that matches it				
				TypeIndex varByIndex = findTypeByIndex(child.getGenericSuperclass(), typeIdx);
				return varByIndex.isTypeVariable() ? findMatchingTypeVariable(child, varByIndex.asTypeVariable()) : varByIndex;
			} else  {
				return typeIdx;
			}
			
		} else {			
			ParameterizedType parameterizedSuperType = asParametrizedType(child.getGenericSuperclass());						
			 Type typeParameterType = getTypeParameterType(parameterizedSuperType, tParamIndex);
			 if (typeParameterType instanceof TypeVariable<?>) {
				 return findMatchingTypeVariable(child, (TypeVariable<?>) typeParameterType);
			 } else {
				return new TypeIndex(typeParameterType);
			 }			 
		}	
	}


	private static class TypeIndex{
		private Type type;
		private int index;
		public TypeIndex(Type type){
			this(type, -1);
		}
		public boolean isWildCard() {
			// TODO Auto-generated method stub
			return false;
		}
		public boolean isTypeVariable(){
			return (type instanceof TypeVariable<?>) ;
		}
		
		public TypeVariable<?> asTypeVariable() {			
			return (TypeVariable<?>) type;
		}
		public TypeIndex(Type type, int index) {
			this.type = type;
			this.index = (type instanceof TypeVariable<?>) ? index : -1;	
		}
		@Override
		public String toString() {
			return "TypeIndex [type=" + type + ", index=" + index + "]";
		}			
	}
	
	private static ParameterizedType asParametrizedType(Type superType) {
		if (superType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) superType;
			return parameterizedType;
		} else {
			throw new IllegalArgumentException("Cannot extract type parameter out of non paramterized class :)");
		}
	}

	private static TypeIndex findMatchingTypeVariable(Class<?> clazz , TypeVariable<?> superTVar){
		TypeVariable<?>[] typeParams = clazz.getTypeParameters();
		for (int i = 0; i < typeParams.length; i++) {
			TypeVariable<?> tVar = typeParams[i];			
			if (tVar.getName().equals(superTVar.getName())) {
				return new TypeIndex(tVar, i);
			}
		}				
		throw new IllegalArgumentException(String.format("Did not find matching typeargument for supertypearg %s in class (%s)", superTVar, clazz));
	}
	
	
	private static TypeIndex findTypeByIndex(Type type, TypeIndex typeStruct){
		ParameterizedType paramType = asParametrizedType(type);			
		Type[] tArgs = paramType.getActualTypeArguments();
		if (tArgs.length <= typeStruct.index){
			throw new IllegalArgumentException(String.format("Type parameters index %d is invalid for type (%s)", typeStruct.index, type));
		}
		return new TypeIndex(tArgs[typeStruct.index], typeStruct.index);	
	}
	
	private static Type getTypeParameterType(ParameterizedType paramedSuperType, int typeParamIndex) {
		Type[] tArgs = paramedSuperType.getActualTypeArguments();
		
		if (tArgs.length <= typeParamIndex) {
			throw new IllegalArgumentException(String.format("No type parameter at index %d for (%s)", typeParamIndex, paramedSuperType));
		}
		return tArgs[typeParamIndex];		
	}

}
