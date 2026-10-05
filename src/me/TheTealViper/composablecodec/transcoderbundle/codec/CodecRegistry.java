package me.TheTealViper.composablecodec.transcoderbundle.codec;

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
	private final Map<Class<?>, Codec<?>> map;
	
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
	public Codec<?> get(Class<?> c) {
		Codec<?> codec = map.get(c);
		if (codec == null) {
			System.out.println("CodecRegistry::get() ERROR: Desired Codec " + c.getName() + " has not been registered yet, but is attempting to be retrieved.");
			//TODO
			// Get rid of this syso change to log or throw actual exception
		}
		return map.get(c);
	}
	
	/**
	 * Assigns the class-codec relationship.
	 *
	 * @param c Class c
	 * @param codec the codec
	 */
	public void set(Class<?> c, Codec<?> codec) {
		if (map.containsKey(c)) {
			System.out.println("CodecRegistry::set() ERROR: Attempted to add a codec with a class key already in existence.");
			//TODO
			// Get rid of this syso change to log or throw actual exception
			return;
		}
		map.put(c, codec);
	}
	
	/**
	 * Gets all registered classes. This was primarily added for debugging registration order bugs prior to switching away from static approaches.
	 *
	 * @return the registered classes
	 */
	public Set<Class<?>> getRegisteredClasses() {
		return map.keySet();
	}
}
