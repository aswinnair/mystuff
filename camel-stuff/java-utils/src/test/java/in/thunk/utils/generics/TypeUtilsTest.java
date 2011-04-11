package in.thunk.utils.generics;

import java.lang.reflect.Type;

public class TypeUtilsTest {
	
	
	public static void main(String[] args) {
		
		Type type = TypeUtils.findMaterializedType(Gen.class, AGenFinalChild.class, 1);
		System.out.println(type);
		
		type = TypeUtils.findMaterializedType(Gen.class, BGenGrandChild.class, 1);
		System.out.println(type);				
	}
	
	
	
	static class Gen<A, B, C> {}	
	
	static class AGenChild<F, K> extends Gen<K, F, Byte> {} ;

	static class AGenGrandChild<FF, K> extends AGenChild<FF, K>{};
	
	static class AGenGreatGrandChild<K, L, FFF> extends AGenGrandChild<FFF, K>{};
	
	static class AGenFinalChild extends AGenGreatGrandChild<Integer, String, Float>{};
	
	static class BGenChild<F, K> extends Gen<K, F, Byte> {} ;

	static class BGenGrandChild<FF, K> extends BGenChild{};
	
}
