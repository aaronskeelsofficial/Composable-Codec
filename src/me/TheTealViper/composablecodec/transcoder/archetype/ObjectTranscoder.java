package me.TheTealViper.composablecodec.transcoder.archetype;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.TheTealViper.composablecodec.transcoder.BaseTranscoder;
import me.TheTealViper.composablecodec.transcoder.FieldTranscoder;
import me.TheTealViper.composablecodec.transcoder.codec.Codec;

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
	private static final Map<Type, ObjectTranscoder<?>> globalRegistryMap = new HashMap<>();
	@SuppressWarnings("unchecked")
	public static <T> ObjectTranscoder<T> getTranscoderOfType(Type T) {
		return (ObjectTranscoder<T>) globalRegistryMap.get(T);
	}
	
	/**
	 * Instantiates a new transcoder bundle.
	 *
	 * @param javaObjectTypeClass the java object type class
	 * @param constructor the constructor lambda
	 */
	public ObjectTranscoder(Type javaObjectTypeRuntime, Supplier<JavaObjectType> constructor) {
		super(javaObjectTypeRuntime, constructor);
		//Register in global registry
		globalRegistryMap.put(javaObjectTypeRuntime, this);
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
		Codec<JavaObjectType> c = new Codec<JavaObjectType>(
				javaObjectTypeRuntime,
				generateJavaToJson(),
				generateJsonToJava(),
				null,
				null);
			this.codec = c;
			return this;
		}
	
	

}
