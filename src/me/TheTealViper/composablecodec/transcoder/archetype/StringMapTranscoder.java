package me.TheTealViper.composablecodec.transcoder.archetype;

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
import me.TheTealViper.composablecodec.transcoder.codec.CodecRegistry;

/**
 * A StringMapTranscoder is meant to represent the entirety of transcoders necessary to construct a String keyed Map of java object
 * 
 * The intended workflow is NOT:
 * 		StringMapTranscoder<T> bundle = new StringMapTranscoder<>(T CLASS, T CONSTRUCTOR)
 * 				.set(...)
 * 				.set(...)
 * 				.buildCodec();
 * The intended workflow IS:
 * 		ObjectTranscoder<T> bundle = new ObjectTranscoder<>(T CLASS, T CONSTRUCTOR)
 * 				.set(..., new FieldTranscoder<>(..., new StringMapTranscoder<>(Integer.class))))
 * 				.buildCodec();
 * StringMapTranscoder are NOT meant to ever be the outermost layer of a codec, they are meant to represent an implementation that builds off an ObjectTranscoder for a type
 *
 * @param <JavaObjectType> the generic type
 */
public class StringMapTranscoder<JavaObjectType> extends BaseTranscoder<Map<String,JavaObjectType>> {
	
	/** How do we make a new instance of our parent java object? */
	public Supplier<Map<String,JavaObjectType>> constructor;
	/** How do we work with each element of the list's data? */
	public BaseTranscoder<JavaObjectType> elemTranscoder;
	
	/**
	 * Instantiates a new transcoder bundle.
	 *
	 * @param javaObjectTypeClass the java object type class
	 * @param constructor the constructor lambda
	 */
	public StringMapTranscoder(Type javaObjectTypeRuntime) {
		this(staticGetTranscoderOfType(javaObjectTypeRuntime));
	}
	public StringMapTranscoder(BaseTranscoder<JavaObjectType> elemTranscoder) {
		Supplier<Map<String,JavaObjectType>> constructor = () -> {
			return new HashMap<>();
		};
		super(TypeToken.getParameterized(Map.class, String.class, elemTranscoder.javaObjectTypeRuntime).getType());
		this.constructor = constructor;
		this.elemTranscoder = elemTranscoder;
		buildCodec();
	}
	
	/**
	 * Here is our default implementation of a lambda that recursively "passes the torch" to generate JSON given any Java object.
	 * Others can manually do this, though that's a headache when most implementations will use this approach anyway.
	 *
	 * @return java to json lambda
	 */
	@Override
	public Function<Map<String,JavaObjectType>,JsonElement> generateJavaToJson() {
		/*
		 * 1. Loop through keys
		 * 2. For each key, get the child value from the parent
		 * 3. Now convert the child value to json
		 * 4. Add that to parent json object
		 */
		return (javaObject) -> {
			JsonObject json = new JsonObject();
			for (Entry<String,JavaObjectType> entry : javaObject.entrySet()) {
				JsonElement childJson = elemTranscoder.codec.javaToJson.apply(entry.getValue());
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
	@Override
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
			for (String key : jsonObj.keySet()) {
				JavaObjectType child = elemTranscoder.codec.jsonToJava.apply(jsonObj.get(key));
				javaObject.put(key, child);
			}
			return javaObject;
		};
	}

	@Override
	public BaseTranscoder<Map<String,JavaObjectType>> buildCodec() {
		@SuppressWarnings("unchecked")
		Codec<Map<String,JavaObjectType>> c = (Codec<Map<String,JavaObjectType>>) CodecRegistry.getGlobalRegistry().getIfExists(javaObjectTypeRuntime);
		c = c != null ? c : new Codec<Map<String,JavaObjectType>>(
			javaObjectTypeRuntime,
			generateJavaToJson(),
			generateJsonToJava(),
			null,
			null);
		this.codec = c;
		return this;
	}
	
	public static <T> BaseTranscoder<Map<String,T>> getTranscoderOfType(Type t) {
		return BaseTranscoder.staticGetTranscoderOfType(TypeToken.getParameterized(Map.class, String.class, t).getType());
	}
}
