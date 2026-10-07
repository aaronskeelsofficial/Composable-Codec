package me.TheTealViper.composablecodec.transcoder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonElement;

import me.TheTealViper.composablecodec.transcoder.codec.Codec;

public abstract class BaseTranscoder<JavaObjectType> {

	/** What transcoders does this bundle require? */
	public final Map<String, FieldTranscoder<JavaObjectType,?>> constituents; //key, transcoder
	
	/** Java generics erase type information from being workable, so explicitly what type of object does this represent? */
	public final Type javaObjectTypeRuntime;
	
	/** How do we make a new instance of our parent java object? */
	public Supplier<JavaObjectType> constructor;
	
	/** How do we insert/retrieve a java object into/from another java object */
	public Codec<JavaObjectType> codec;
	
	/**
	 * Instantiates a new transcoder bundle.
	 *
	 * @param javaObjectTypeClass the java object type class
	 * @param constructor the constructor lambda
	 */
	public BaseTranscoder(Type javaObjectTypeRuntime, Supplier<JavaObjectType> constructor) {
		this.javaObjectTypeRuntime = javaObjectTypeRuntime;
		this.constructor = constructor;
		constituents = new HashMap<>();
		//Register in global registry
		//TODO
	}
	
	/**
	 * Sets the key-transcoder association.
	 *
	 * @param key the key
	 * @param transcoder the transcoder
	 * @return Modified bundle
	 */
	public BaseTranscoder<JavaObjectType> set(String key, FieldTranscoder<JavaObjectType, ?> transcoder) {
		constituents.put(key, transcoder);
		return this;
	}
	
	/**
	 * Removes the transcoder with the key.
	 *
	 * @param key the key
	 * @return Modified bundle
	 */
	public BaseTranscoder<JavaObjectType> remove(String key) {
		constituents.remove(key);
		return this;
	}
	
	/**
	 * Gets the transcoder with the key.
	 *
	 * @param key the key
	 * @return Modified bundle
	 */
	public FieldTranscoder<JavaObjectType, ?> get(String key) {
		return constituents.get(key);
	}
	
	/**
	 * Gets the keys.
	 *
	 * @return the keys
	 */
	public Set<String> getKeys() {
		return constituents.keySet();
	}
	
	/**
	 * Here is our default implementation of a lambda that recursively "passes the torch" to generate JSON given any Java object.
	 * Others can manually do this, though that's a headache when most implementations will use this approach anyway.
	 *
	 * @return java to json lambda
	 */
	public abstract Function<?,JsonElement> generateJavaToJson();
	
	/**
	 * Here is our default implementation of a lambda that recursively "passes the torch" to load object values given
	 * any json object (middleman object NOT raw json string). Others can manually do this, though that's a headache
	 * when most implementations will use this approach anyway.
	 *
	 * @return json to java lambda
	 */
	public abstract Function<JsonElement,?> generateJsonToJava();
	
	/**
	 * This function builds the codec. It is necessary for codec creation to be a function (not static) because we MUST wait for the child codecs
	 * to have been registered prior to attempting to build this codec since it is dependent on them.
	 *
	 * @return the transcoder bundle
	 */
	public abstract BaseTranscoder<JavaObjectType> buildCodec();
	
}
