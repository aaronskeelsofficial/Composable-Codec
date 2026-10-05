package me.TheTealViper.composablecodec.ficbundle.codec;

import java.util.HashMap;
import java.util.Map;

public class CodecRegistry {
	private static CodecRegistry globalsingleton = null;
	private final Map<Class<?>, HierarchicalCodec<?>> map;
	private CodecRegistry() {
		map = new HashMap<>();
	}
	
	public static CodecRegistry getGlobalRegistry() {
		if (globalsingleton == null)
			globalsingleton = new CodecRegistry();
		return globalsingleton;
	}
	
	public HierarchicalCodec<?> get(Class<?> c) {
		return map.get(c);
	}
	public void set(Class<?> c, HierarchicalCodec<?> codec) {
		if (map.containsKey(c)) {
			System.out.println("CodecRegistry::set() ERROR: Attempted to add a codec with a class key already in existence.");
			//TODO
			// Get RID of this syso in place of proper hytale logging.
			return;
		}
		map.put(c, codec);
	}
}
