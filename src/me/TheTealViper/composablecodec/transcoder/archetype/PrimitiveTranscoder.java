package me.TheTealViper.composablecodec.transcoder.archetype;

import me.TheTealViper.composablecodec.transcoder.codec.Codec;

/**
 * A PrimitiveTranscoder is a prebuilt core transcoder for primitive types
 */
public class PrimitiveTranscoder {
	
	public static void registerPrimitiveTranscoders() {
		INTEGER();
		FLOAT();
		DOUBLE();
		STRING();
	}
	
	private static ObjectTranscoder<Integer> INTEGER = null;
	/**
	 * Integer.
	 *
	 * @return the transcoder bundle
	 */
	public static ObjectTranscoder<Integer> INTEGER() {
		if (INTEGER == null) {
			ObjectTranscoder<Integer> bundle = new ObjectTranscoder<Integer>(Integer.class, null);
			bundle.codec = Codec.INTEGER;
			INTEGER = bundle;
		}
		return INTEGER;
	}
	
	/** The float. */
	private static ObjectTranscoder<Float> FLOAT = null;
	
	/**
	 * Float.
	 *
	 * @return the transcoder bundle
	 */
	public static ObjectTranscoder<Float> FLOAT() {
		if (FLOAT == null) {
			ObjectTranscoder<Float> bundle = new ObjectTranscoder<Float>(Float.class, null);
			bundle.codec = Codec.FLOAT;
			FLOAT = bundle;
		}
		return FLOAT;
	}
	
	/** The double. */
	private static ObjectTranscoder<Double> DOUBLE = null;
	
	/**
	 * Double.
	 *
	 * @return the transcoder bundle
	 */
	public static ObjectTranscoder<Double> DOUBLE() {
		if (DOUBLE == null) {
			ObjectTranscoder<Double> bundle = new ObjectTranscoder<Double>(Double.class, null);
			bundle.codec = Codec.DOUBLE;
			DOUBLE = bundle;
		}
		return DOUBLE;
	}
	
	/** The string. */
	private static ObjectTranscoder<String> STRING = null;
	
	/**
	 * String.
	 *
	 * @return the transcoder bundle
	 */
	public static ObjectTranscoder<String> STRING() {
		if (STRING == null) {
			ObjectTranscoder<String> bundle = new ObjectTranscoder<String>(String.class, null);
			bundle.codec = Codec.STRING;
			STRING = bundle;
		}
		return STRING;
	}
}
