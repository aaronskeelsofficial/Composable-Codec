package me.TheTealViper.composablecodec.transcoder.archetype;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.TheTealViper.composablecodec.transcoder.BaseTranscoder;
import me.TheTealViper.composablecodec.transcoder.FieldTranscoder;
import me.TheTealViper.composablecodec.transcoder.codec.Codec;
import me.TheTealViper.composablecodec.transcoder.codec.CodecRegistry;

/**
 * An ObjectTranscoder is meant to represent the entirety of transcoders necessary to construct a java object
 * 
 * The intended workflow is:
 * 		ObjectTranscoder<T> bundle = new ObjectTranscoder<>(T CLASS, T CONSTRUCTOR)
 * 				.set(...)
 * 				.set(...)
 * 				.buildCodec();
 *
 * @param <JavaObjectType> the generic type
 */
public class ObjectTranscoder<JavaObjectType> extends BaseTranscoder<JavaObjectType>{
	
	/** How do we make a new instance of our parent java object? */
	public Supplier<JavaObjectType> constructor;
	/** What transcoders does this bundle require to understand/reconstruct its children? */
	public final Map<String, FieldTranscoder<JavaObjectType,?>> constituents; //key, fieldtranscoder
	
	/**
	 * Instantiates a new transcoder bundle.
	 *
	 * @param javaObjectTypeClass the java object type class
	 * @param constructor the constructor lambda
	 */
	public ObjectTranscoder(Type javaObjectTypeRuntime, Supplier<JavaObjectType> constructor) {
		super(javaObjectTypeRuntime);
		this.constructor = constructor;
		this.constituents = new HashMap<>();
	}
	
	/**
	 * Sets the key-transcoder association.
	 *
	 * @param key the key
	 * @param transcoder the fieldtranscoder
	 * @return Modified transcoder
	 */
	public ObjectTranscoder<JavaObjectType> set(String key, FieldTranscoder<JavaObjectType, ?> transcoder) {
		transcoder.key = key;
		constituents.put(key, transcoder);
		return this;
	}
	
	/**
	 * Removes the fieldtranscoder with the key.
	 *
	 * @param key the key
	 * @return Modified transcoder
	 */
	public ObjectTranscoder<JavaObjectType> remove(String key) {
		constituents.remove(key);
		return this;
	}
	
	/**
	 * Gets the fieldtranscoder with the key.
	 *
	 * @param key the key
	 * @return Modified transcoder
	 */
	public FieldTranscoder<JavaObjectType, ?> get(String key) {
		return constituents.get(key);
	}
	
	/**
	 * Gets the keys of all the fieldtranscoders.
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
	@Override
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
				Object child = transcoder.childFromParent.apply(javaObject);
				if (transcoder.childTranscoder != null) {
					JsonElement childJson = transcoder.childTranscoder.codec.javaToJson.apply(child);
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
	@Override
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
				Object child = transcoder.childTranscoder.codec.jsonToJava.apply(childJson);
				transcoder.childIntoParent.accept(javaObject, child);
			}
			return javaObject;
		};
	}

	@Override
	public BaseTranscoder<JavaObjectType> buildCodec() {
		@SuppressWarnings("unchecked")
		Codec<JavaObjectType> c = (Codec<JavaObjectType>) CodecRegistry.getGlobalRegistry().getIfExists(javaObjectTypeRuntime);
		c = c != null ? c : new Codec<JavaObjectType>(
			javaObjectTypeRuntime,
			generateJavaToJson(),
			generateJsonToJava(),
			null,
			null);
		this.codec = c;
		return this;
	}

	public static <T> BaseTranscoder<T> getTranscoderOfType(Type t) {
		return BaseTranscoder.staticGetTranscoderOfType(t);
	}

}
