package me.TheTealViper.composablecodec.transcoder.archetype;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import me.TheTealViper.composablecodec.transcoder.BaseTranscoder;
import me.TheTealViper.composablecodec.transcoder.codec.Codec;

public class ListTranscoder<JavaObjectType> extends BaseTranscoder<List<JavaObjectType>> {
	
	/**
	 * Instantiates a new transcoder representing a list type of data.
	 *
	 * @param javaObjectTypeClass the java object type class
	 * @param constructor the constructor lambda
	 */
	public ListTranscoder(Type javaObjectTypeRuntime) {
		Supplier<List<JavaObjectType>> constructor = () -> {
			return new ArrayList<>();
		};
		super(TypeToken.getParameterized(List.class, javaObjectTypeRuntime).getType(), constructor);
		buildCodec();
	}
	
	/**
	 * Here is our default implementation of a lambda that recursively "passes the torch" to generate JSON given any Java object.
	 * Others can manually do this, though that's a headache when most implementations will use this approach anyway.
	 *
	 * @return java to json lambda
	 */
	@Override
	public Function<List<JavaObjectType>,JsonElement> generateJavaToJson() {
		return (javaObject) -> {
			JsonArray jsonArr = new JsonArray();
			Type parameterType = ((ParameterizedType) javaObjectTypeRuntime).getActualTypeArguments()[0];
			for (JavaObjectType elem : javaObject) {
					JsonElement childJson = ObjectTranscoder.getTranscoderOfType(parameterType).codec.javaToJson.apply(elem);
					jsonArr.add(childJson);
			}
			return jsonArr;
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
	public Function<JsonElement,List<JavaObjectType>> generateJsonToJava() {
		return (json) -> {
			List<JavaObjectType> javaObject = constructor.get();
			JsonArray jsonArr = json.getAsJsonArray();
			Type parameterType = ((ParameterizedType) javaObjectTypeRuntime).getActualTypeArguments()[0];
			for (JsonElement childJson : jsonArr) {
				JavaObjectType child = (JavaObjectType) ObjectTranscoder.getTranscoderOfType(parameterType).codec.jsonToJava.apply(childJson);
				javaObject.add(child);
			}
			return javaObject;
		};
	}

	@Override
	public BaseTranscoder<List<JavaObjectType>> buildCodec() {
		Codec<List<JavaObjectType>> c = new Codec<List<JavaObjectType>>(
			javaObjectTypeRuntime,
			generateJavaToJson(),
			generateJsonToJava(),
			null,
			null);
		this.codec = c;
		return this;
	}
}
