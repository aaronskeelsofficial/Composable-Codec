package me.TheTealViper.composablecodec.transcoder.codec;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/*
 * IMPORTANT NOTE ON CODEC ARCHETYPES (Set/List/Array/etc)
 * Custom code is running in the following classes manually checking the type parameters and forking their behavior accordingly:
 *  - TranscoderBundleRegistry: Pulling a List<Integer> will actually return Integer
 *  - TranscoderBundle: The jsonToJava and javaToJson generated handle custom archetypes explicitly
 */

/**
 * This class answers how we convert to/from java object to/from middleman object prior to raw data (JSON, BSON, Binary, etc)
 *
 * @param <JavaObjectType> The type of java object this Codec handles
 */
public class Codec<JavaObjectType> {
	
	/** What codecs in theory would this one depend on? */
	private final Map<Type, Codec<?>> children;
	
	/** The global registry keys are dependent on object type, generics erase this info, what type is this representing? */
	public final Type javaObjectTypeRuntime;
	
	/** How do we convert from java object to json object */
	public final Function<JavaObjectType, JsonElement> javaToJson;
	
	/** How do we convert from json object to java object */
	public final Function<JsonElement, JavaObjectType> jsonToJava;
	
	/** How do we convert from java object to bytebuffer */
	public final Function<JavaObjectType, ByteBuffer> javaToByteBuffer;
	
	/** How do we convert from bytebuffer to java object */
	public final Function<ByteBuffer, JavaObjectType> byteBufferToJava;
		
	/**
	 * Instantiates a new codec and registers it in the global registry.
	 *
	 * @param javaObjectTypeClass This is necessary as the generic type undergoes type erasure and then we have no information on what type object we are handling.
	 * @param javaToJson The java to json converter
	 * @param jsonToJava The json to java converter
	 * @param javaToByteBuffer The java to byte buffer converter
	 * @param byteBufferToJava The byte buffer to java converter
	 */
	public Codec(
			Type javaObjectTypeRuntime,
			Function<JavaObjectType, JsonElement> javaToJson,
			Function<JsonElement, JavaObjectType> jsonToJava,
			Function<JavaObjectType, ByteBuffer> javaToByteBuffer,
			Function<ByteBuffer, JavaObjectType> byteBufferToJava
    ) {
		this.javaObjectTypeRuntime = javaObjectTypeRuntime;
        this.javaToJson = javaToJson;
        this.jsonToJava = jsonToJava;
        this.javaToByteBuffer = javaToByteBuffer;
        this.byteBufferToJava = byteBufferToJava;
        children = new HashMap<>();
        //Register in global registry
        CodecRegistry.getGlobalRegistry().set(javaObjectTypeRuntime, this);
    }
	
	/**
	 * Adds a child codec dependency (purely informational).
	 *
	 * @param codec Child codec
	 * @return Modified codec
	 */
	public Codec<JavaObjectType> addChild(Codec<?> codec) {
		children.put(codec.javaObjectTypeRuntime, codec);
		return this;
	}
	
	/**
	 * Removes a child codec dependency (purely informational).
	 *
	 * @param codec Child codec
	 * @return Modified codec
	 */
	public Codec<JavaObjectType> removeChild(Codec<?> codec) {
		children.values().remove(codec);
		return this;
	}
	
	/**
	 * This function will remove the original child codec if it exists, and then add the replacement codec
	 *
	 * @param original Original child codec
	 * @param replacement Replacement child codec
	 * @return Modified codec
	 */
	public Codec<JavaObjectType> replaceOrInsertChild(Codec<?> original, Codec<?> replacement) {
		children.values().remove(original);
		children.put(replacement.javaObjectTypeRuntime, replacement);
		return this;
	}
	
	
	
	
	
	
	/** The Constant INTEGER. */
	public static final Codec<Integer> INTEGER = new Codec<>(
			Integer.class,
			(javaObject) -> new JsonPrimitive(javaObject),
			(jsonObject) -> jsonObject.getAsInt(),
			(javaObject) -> ByteBuffer.allocate(Integer.SIZE/8).put(javaObject.byteValue()),
			(byteBuffer) -> byteBuffer.getInt());
	
	/** The Constant FLOAT. */
	public static final Codec<Float> FLOAT = new Codec<>(
			Float.class,
			(javaObject) -> new JsonPrimitive(javaObject),
			(jsonObject) -> jsonObject.getAsFloat(),
			(javaObject) -> ByteBuffer.allocate(Float.SIZE/8).put(javaObject.byteValue()),
			(byteBuffer) -> byteBuffer.getFloat());
	
	/** The Constant DOUBLE. */
	public static final Codec<Double> DOUBLE = new Codec<>(
			Double.class,
			(javaObject) -> new JsonPrimitive(javaObject),
			(jsonObject) -> jsonObject.getAsDouble(),
			(javaObject) -> ByteBuffer.allocate(Double.SIZE/8).put(javaObject.byteValue()),
			(byteBuffer) -> byteBuffer.getDouble());
	
	/** The Constant STRING. */
	public static final Codec<String> STRING = new Codec<>(
			String.class,
			(javaObject) -> new JsonPrimitive(javaObject),
			(jsonObject) -> jsonObject.getAsString(),
			(javaObject) -> ByteBuffer.allocate(javaObject.getBytes().length).put(javaObject.getBytes()),
			(byteBuffer) -> StandardCharsets.UTF_8.decode(byteBuffer).toString());
}
