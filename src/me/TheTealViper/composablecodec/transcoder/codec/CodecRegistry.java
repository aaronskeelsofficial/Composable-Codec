package me.TheTealViper.composablecodec.transcoder.codec;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class provides a global singleton that registers all instantiated Codecs. This is useful if third party addons need to load/save objects from other addons.
 */
public class CodecRegistry {
	
	/** The globalsingleton. */
	private static CodecRegistry globalsingleton = null;
	
	/** The map. */
	private final Map<Type, Codec<?>> map;
	
	/**
	 * Instantiates a new codec registry.
	 */
	private CodecRegistry() {
		map = new HashMap<>();
	}
	
	/**
	 * Gets the global registry.
	 *
	 * @return the global registry
	 */
	public static CodecRegistry getGlobalRegistry() {
		if (globalsingleton == null)
			globalsingleton = new CodecRegistry();
		return globalsingleton;
	}
	
	/**
	 * Gets the codec associated with class.
	 *
	 * @param c Class c
	 * @return the codec
	 */
	public Codec<?> get(Type t) {
		Codec<?> codec = map.get(t);
		if (codec == null) {
			System.out.println("CodecRegistry::get() ERROR: Desired Codec " + t.toString() + " has not been registered yet, but is attempting to be retrieved.");
			//TODO
			// Get rid of this syso change to log or throw actual exception
		}
		return map.get(t);
	}
	
	/**
	 * Assigns the class-codec relationship.
	 *
	 * @param t Type t
	 * @param codec the codec
	 */
	public void set(Type t, Codec<?> codec) {
		if (map.containsKey(t)) {
			System.out.println("CodecRegistry::set() ERROR: Attempted to add a codec with a type key already in existence '" + t.toString() + "'.");
			//TODO
			// Get rid of this syso change to log or throw actual exception
			return;
		}
		map.put(t, codec);
	}
	
	/**
	 * Gets all registered types. This was primarily added for debugging registration order bugs prior to switching away from static approaches.
	 *
	 * @return the registered types
	 */
	public Set<Type> getRegisteredTypes() {
		return map.keySet();
	}
}
