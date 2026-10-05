package me.TheTealViper.composablecodec.transcoderbundle;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a global singleton that registers all instantiated bundles. This is useful if third party addons need to load/save/insert/extract objects from other addons.
 */
public class TranscoderBundleRegistry {
	
	/** The globalsingleton. */
	private static TranscoderBundleRegistry globalsingleton = null;
	
	/** The map. */
	private final Map<Class<?>, TranscoderBundle<?>> map;
	
	/**
	 * Instantiates a new transcoder bundle registry.
	 */
	private TranscoderBundleRegistry() {
		map = new HashMap<>();
	}
	
	/**
	 * Gets the global registry.
	 *
	 * @return the global registry
	 */
	public static TranscoderBundleRegistry getGlobalRegistry() {
		if (globalsingleton == null)
			globalsingleton = new TranscoderBundleRegistry();
		return globalsingleton;
	}
	
	/**
	 * Gets the bundle associated with a class.
	 *
	 * @param <T> the generic type
	 * @param c the c
	 * @return the transcoder bundle
	 */
	@SuppressWarnings("unchecked")
	public <T> TranscoderBundle<T> get(Class<T> c) {
		TranscoderBundle<?> codec = map.get(c);
		if (codec == null) {
			System.out.println("TranscoderBundleRegistry::get() ERROR: Desired TranscoderBundle " + c.getName() + " has not been registered yet, but is attempting to be retrieved.");
			//TODO
			// Get rid of this syso change to log or throw error
		}
		return (TranscoderBundle<T>) map.get(c);
	}
	
	/**
	 * Assigns the class-codec relationship.
	 *
	 * @param c the c
	 * @param bundle the bundle
	 */
	public void set(Class<?> c, TranscoderBundle<?> bundle) {
		if (map.containsKey(c)) {
			System.out.println("TranscoderBundleRegistry::set() ERROR: Attempted to add a codec with a class key already in existence.");
			//TODO
			// Get rid of this syso change to log or throw error
			return;
		}
		map.put(c, bundle);
	}
}
