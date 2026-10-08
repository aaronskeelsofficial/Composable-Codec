package me.TheTealViper.composablecodec.transcoder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonElement;

import me.TheTealViper.composablecodec.transcoder.codec.Codec;

public abstract class BaseTranscoder<JavaObjectType> {
	
	private static final Map<Type, BaseTranscoder<?>> globalRegistryMap = new HashMap<>();
	@SuppressWarnings("unchecked")
	public static <T> BaseTranscoder<T> staticGetTranscoderOfType(Type T) {
		return (BaseTranscoder<T>) globalRegistryMap.get(T);
	}
	
	/** Java generics erase type information from being workable, so explicitly what type of object does this represent? */
	public final Type javaObjectTypeRuntime;
	/** How do we insert/retrieve a java object into/from another java object */
	public Codec<JavaObjectType> codec;
	
	/**
	 * Instantiates a new transcoder bundle.
	 *
	 * @param javaObjectTypeClass the java object type class
	 * @param constructor the constructor lambda
	 */
	public BaseTranscoder(Type javaObjectTypeRuntime) {
		this.javaObjectTypeRuntime = javaObjectTypeRuntime;
		//Register in global registry
		globalRegistryMap.put(javaObjectTypeRuntime, this);
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
