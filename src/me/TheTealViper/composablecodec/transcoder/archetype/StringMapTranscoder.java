package me.TheTealViper.composablecodec.transcoder.archetype;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import me.TheTealViper.composablecodec.transcoder.BaseTranscoder;
import me.TheTealViper.composablecodec.transcoder.codec.Codec;

public class StringMapTranscoder<JavaObjectType> extends BaseTranscoder<Map<String,JavaObjectType>> {
	
	/**
	 * Instantiates a new transcoder bundle.
	 *
	 * @param javaObjectTypeClass the java object type class
	 * @param constructor the constructor lambda
	 */
	public StringMapTranscoder(Type javaObjectTypeRuntime) {
		Supplier<Map<String,JavaObjectType>> constructor = () -> {
			return new HashMap<>();
		};
		super(TypeToken.getParameterized(Map.class, String.class, javaObjectTypeRuntime).getType(), constructor);
		buildCodec();
	}
	
	/**
	 * Here is our default implementation of a lambda that recursively "passes the torch" to generate JSON given any Java object.
	 * Others can manually do this, though that's a headache when most implementations will use this approach anyway.
	 *
	 * @return java to json lambda
	 */
	public Function<Map<String,JavaObjectType>,JsonElement> generateJavaToJson() {
		/*
		 * 1. Loop through keys
		 * 2. For each key, get the child value from the parent
		 * 3. Now convert the child value to json
		 * 4. Add that to parent json object
		 */
		return (javaObject) -> {
			JsonObject json = new JsonObject();
			Type parameterType = ((ParameterizedType) javaObjectTypeRuntime).getActualTypeArguments()[1];
			for (Entry<String,JavaObjectType> entry : javaObject.entrySet()) {
				JsonElement childJson = ObjectTranscoder.getTranscoderOfType(parameterType).codec.javaToJson.apply(entry.getValue());
				json.add(entry.getKey(), childJson);
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
	public Function<JsonElement,Map<String,JavaObjectType>> generateJsonToJava() {
		/*
		 * 1. Loop through keys
		 * 2. For each key, get the child json from the json
		 * 3. Now convert the child json to java object
		 * 4. Add that to parent java object
		 */
		return (json) -> {
			Map<String,JavaObjectType> javaObject = constructor.get();
			JsonObject jsonObj = json.getAsJsonObject();
			Type parameterType = ((ParameterizedType) javaObjectTypeRuntime).getActualTypeArguments()[1];
			for (String key : jsonObj.keySet()) {
				JavaObjectType child = (JavaObjectType) ObjectTranscoder.getTranscoderOfType(parameterType).codec.jsonToJava.apply(jsonObj.get(key));
				javaObject.put(key, child);
			}
			return javaObject;
		};
	}

	@Override
	public BaseTranscoder<Map<String,JavaObjectType>> buildCodec() {
		Codec<Map<String,JavaObjectType>> c = new Codec<Map<String,JavaObjectType>>(
			javaObjectTypeRuntime,
			generateJavaToJson(),
			generateJsonToJava(),
			null,
			null);
		this.codec = c;
		return this;
	}
}
