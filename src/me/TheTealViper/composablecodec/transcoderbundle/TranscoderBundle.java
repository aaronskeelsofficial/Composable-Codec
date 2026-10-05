package me.TheTealViper.composablecodec.transcoderbundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.TheTealViper.composablecodec.transcoderbundle.codec.Codec;

/**
 * A TranscoderBundle is meant to represent the entirety of transcoders necessary to construct a java object
 * 
 * The intended workflow is:
 * 		TranscoderBundle<T> bundle = new TranscoderBundle<>(T CLASS, T CONSTRUCTOR)
 * 				.set(...)
 * 				.set(...)
 * 				.buildCodec();
 *
 * @param <JavaObjectType> the generic type
 */
public class TranscoderBundle<JavaObjectType> {
	
	/** What transcoders does this bundle require? */
	private final Map<String, FieldTranscoder<JavaObjectType,?>> constituents; //key, transcoder
	
	/** Java generics erase type information from being workable, so explicitly what type of object does this represent? */
	public final Class<?> javaObjectTypeClass;
	
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
	public TranscoderBundle(Class<?> javaObjectTypeClass, Supplier<JavaObjectType> constructor) {
		this.javaObjectTypeClass = javaObjectTypeClass;
		this.constructor = constructor;
		constituents = new HashMap<>();
		//Register in global registry
		TranscoderBundleRegistry.getGlobalRegistry().set(javaObjectTypeClass, this);
	}
	
	/**
	 * Sets the key-transcoder association.
	 *
	 * @param key the key
	 * @param transcoder the transcoder
	 * @return Modified bundle
	 */
	public TranscoderBundle<JavaObjectType> set(String key, FieldTranscoder<JavaObjectType, ?> transcoder) {
		constituents.put(key, transcoder);
		return this;
	}
	
	/**
	 * Removes the transcoder with the key.
	 *
	 * @param key the key
	 * @return Modified bundle
	 */
	public TranscoderBundle<JavaObjectType> remove(String key) {
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
	@SuppressWarnings("unchecked")
	public Function<JavaObjectType,JsonElement> generateJavaToJson() {
		/*
		 * 1. Loop through keys
		 * 2. For each key, get the child value from the parent
		 * 3. Now convert the child value to json
		 * 4. Add that to parent json object
		 */
		return (javaObject) -> {
			JsonObject json = new JsonObject();
			for (String key : constituents.keySet()) {
				FieldTranscoder<JavaObjectType,Object> transcoder = (FieldTranscoder<JavaObjectType, Object>) get(key);
				Object child = transcoder.fieldInstruction.childFromParent.apply(javaObject);
				if (transcoder.childBundle != null) {
					JsonElement childJson = transcoder.childBundle.codec.javaToJson.apply(child);
					json.add(key, childJson);
				}
			}
			return json;
		};
	}
	
	/**
	 * Here is our default implementation of a lambda that recursively "passes the torch" to load object values given
	 * any json object (middleman object NOT raw json string). Others can manually do this, though that's a headache
	 * when most implementations will use this approach anyway.
	 *
	 * @return json to java lambda
	 */
	@SuppressWarnings("unchecked")
	public Function<JsonElement,JavaObjectType> generateJsonToJava() {
		/*
		 * 1. Loop through keys
		 * 2. For each key, get the child json from the json
		 * 3. Now convert the child json to java object
		 * 4. Add that to parent java object
		 */
		return (json) -> {
			JavaObjectType javaObject = constructor.get();
			for (String key : constituents.keySet()) {
				FieldTranscoder<JavaObjectType,Object> transcoder = (FieldTranscoder<JavaObjectType, Object>) get(key);
				JsonElement childJson = ((JsonObject) json).get(key);
				Object child = transcoder.childBundle.codec.jsonToJava.apply(childJson);
				transcoder.fieldInstruction.childIntoParent.accept(javaObject, child);
			}
			return javaObject;
		};
	}
	
	/**
	 * This function builds the codec. It is necessary for codec creation to be a function (not static) because we MUST wait for the child codecs
	 * to have been registered prior to attempting to build this codec since it is dependent on them.
	 *
	 * @return the transcoder bundle
	 */
	@SuppressWarnings("unchecked")
	public TranscoderBundle<JavaObjectType> buildCodec() {
		Codec<JavaObjectType> c = new Codec<JavaObjectType>((Class<JavaObjectType>) javaObjectTypeClass,
			generateJavaToJson(),
			generateJsonToJava(),
			null,
			null);
		this.codec = c;
		return this;
	}
	
	
	
	
	
	
	
	
	
	
//	public <C> void encodeFieldJson(JsonObject json, Object parent, String key) {
//		@SuppressWarnings("unchecked")
//		FieldTranscoder<Object, C> bundle = (FieldTranscoder<Object, C>) get(key);
//	    C child = bundle.fieldInstruction.childFromParent.get(parent);
//	    JsonElement encoded = bundle.codec.encodeJson(child);
//	    json.add(key, encoded);
//	}
//	public JsonEncoder<PARENTOBJECTTYPE> generateJsonEncoder() {
//		return (object) -> {
//			JsonObject json = new JsonObject();
//        	for (String key : getKeys()) {
//        		encodeFieldJson(json, object, key);
//        	}
//        	return json;
//		};
//	}
//	public <C> void decodeFieldJson(PARENTOBJECTTYPE object, JsonObject json, String key) {
//		@SuppressWarnings("unchecked")
//		FieldTranscoder<PARENTOBJECTTYPE, C> bundle = (FieldTranscoder<PARENTOBJECTTYPE, C>) get(key);
//		C child = bundle.codec.decodeJson(json);
//		bundle.fieldInstruction.childIntoParent.set(object, child);
//	}
//	public JsonDecoder<PARENTOBJECTTYPE> generateJsonDecoder() {
//		return (json) -> {
//			PARENTOBJECTTYPE object = constructor.get();
//			for (String key : getKeys()) {
//				FieldTranscoder<PARENTOBJECTTYPE, ?> bundle = get(key);
//				bundle.codec.decodeJson(json);
//				get(key).fieldInstruction.setter.
//			}
//			return null;
//		};
/** The integer. */
//	}
	private static TranscoderBundle<Integer> INTEGER = null;
	
	/**
	 * Integer.
	 *
	 * @return the transcoder bundle
	 */
	public static TranscoderBundle<Integer> INTEGER() {
		if (INTEGER == null) {
			TranscoderBundle<Integer> bundle = new TranscoderBundle<Integer>(Integer.class, null);
			bundle.codec = Codec.INTEGER;
			INTEGER = bundle;
		}
		return INTEGER;
	}
	
	/** The float. */
	private static TranscoderBundle<Float> FLOAT = null;
	
	/**
	 * Float.
	 *
	 * @return the transcoder bundle
	 */
	public static TranscoderBundle<Float> FLOAT() {
		if (FLOAT == null) {
			TranscoderBundle<Float> bundle = new TranscoderBundle<Float>(Float.class, null);
			bundle.codec = Codec.FLOAT;
			FLOAT = bundle;
		}
		return FLOAT;
	}
	
	/** The double. */
	private static TranscoderBundle<Double> DOUBLE = null;
	
	/**
	 * Double.
	 *
	 * @return the transcoder bundle
	 */
	public static TranscoderBundle<Double> DOUBLE() {
		if (DOUBLE == null) {
			TranscoderBundle<Double> bundle = new TranscoderBundle<Double>(Double.class, null);
			bundle.codec = Codec.DOUBLE;
			DOUBLE = bundle;
		}
		return DOUBLE;
	}
	
	/** The string. */
	private static TranscoderBundle<String> STRING = null;
	
	/**
	 * String.
	 *
	 * @return the transcoder bundle
	 */
	public static TranscoderBundle<String> STRING() {
		if (STRING == null) {
			TranscoderBundle<String> bundle = new TranscoderBundle<String>(String.class, null);
			bundle.codec = Codec.STRING;
			STRING = bundle;
		}
		return STRING;
	}

}
