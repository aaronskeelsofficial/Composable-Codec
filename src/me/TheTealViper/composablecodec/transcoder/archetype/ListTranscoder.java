package me.TheTealViper.composablecodec.transcoder.archetype;

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
import me.TheTealViper.composablecodec.transcoder.codec.CodecRegistry;

/**
 * A ListTranscoder is meant to represent the entirety of transcoders necessary to construct a List of java object
 * 
 * The intended workflow is NOT:
 * 		ListTranscoder<T> bundle = new ListTranscoder<>(T CLASS, T CONSTRUCTOR)
 * 				.set(...)
 * 				.set(...)
 * 				.buildCodec();
 * The intended workflow IS:
 * 		ObjectTranscoder<T> bundle = new ObjectTranscoder<>(T CLASS, T CONSTRUCTOR)
 * 				.set(..., new FieldTranscoder<>(..., new ListTranscoder<>(Integer.class))))
 * 				.buildCodec();
 * ListTranscoders are NOT meant to ever be the outermost layer of a codec, they are meant to represent an implementation that builds off an ObjectTranscoder for a type
 *
 * @param <JavaObjectType> the generic type
 */
public class ListTranscoder<JavaObjectType> extends BaseTranscoder<List<JavaObjectType>> {
	
	/** How do we make a new instance of our parent java object? */
	public Supplier<List<JavaObjectType>> constructor;
	/** How do we work with each element of the list's data? */
	public BaseTranscoder<JavaObjectType> elemTranscoder;
	
	/**
	 * Instantiates a new transcoder representing a list type of data.
	 *
	 * @param javaObjectTypeClass the java object type class
	 * @param constructor the constructor lambda
	 */
	public ListTranscoder(Type elemTypeRuntime) {
		this(staticGetTranscoderOfType(elemTypeRuntime));
	}
	
	public ListTranscoder(BaseTranscoder<JavaObjectType> elemTranscoder) {
		Supplier<List<JavaObjectType>> constructor = () -> {
			return new ArrayList<>();
		};
		super(TypeToken.getParameterized(List.class, elemTranscoder.javaObjectTypeRuntime).getType());
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
	public Function<List<JavaObjectType>,JsonElement> generateJavaToJson() {
		return (javaObject) -> {
			JsonArray jsonArr = new JsonArray();
			for (JavaObjectType elem : javaObject) {
					JsonElement childJson = elemTranscoder.codec.javaToJson.apply(elem);
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
	@Override
	public Function<JsonElement,List<JavaObjectType>> generateJsonToJava() {
		return (json) -> {
			List<JavaObjectType> javaObject = constructor.get();
			JsonArray jsonArr = json.getAsJsonArray();
			for (JsonElement childJson : jsonArr) {
				JavaObjectType child = elemTranscoder.codec.jsonToJava.apply(childJson);
				javaObject.add(child);
			}
			return javaObject;
		};
	}

	@Override
	public BaseTranscoder<List<JavaObjectType>> buildCodec() {
		@SuppressWarnings("unchecked")
		Codec<List<JavaObjectType>> c = (Codec<List<JavaObjectType>>) CodecRegistry.getGlobalRegistry().getIfExists(javaObjectTypeRuntime);
		c = c != null ? c : new Codec<List<JavaObjectType>>(
			javaObjectTypeRuntime,
			generateJavaToJson(),
			generateJsonToJava(),
			null,
			null);
		this.codec = c;
		return this;
	}
	
	public static <T> BaseTranscoder<List<T>> getTranscoderOfType(Type t) {
		return BaseTranscoder.staticGetTranscoderOfType(TypeToken.getParameterized(List.class, t).getType());
	}
}
