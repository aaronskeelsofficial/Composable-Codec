package me.TheTealViper.composablecodec;

import java.nio.ByteBuffer;

/*
 * Codec exists to give instructions to translate external <-> internal primitive
 */
@SuppressWarnings("unused")
public class Codec<T> {
    @FunctionalInterface
    public interface JsonEncoder<T> {
        String encode(T object);
    }
    @FunctionalInterface
    public interface JsonDecoder<T> {
        T decode(String json);
    }
    @FunctionalInterface
    public interface BinaryEncoder<T> {
        ByteBuffer encode(T object);
    }
    @FunctionalInterface
    public interface BinaryDecoder<T> {
        T decode(ByteBuffer data);
    }

	private final JsonEncoder<T> jsonEncoder;
    private final JsonDecoder<T> jsonDecoder;
    private final BinaryEncoder<T> binaryEncoder;
    private final BinaryDecoder<T> binaryDecoder;
    public Codec(
        JsonEncoder<T> jsonEncoder,
        JsonDecoder<T> jsonDecoder,
        BinaryEncoder<T> binaryEncoder,
        BinaryDecoder<T> binaryDecoder
    ) {
        this.jsonEncoder = jsonEncoder;
        this.jsonDecoder = jsonDecoder;
        this.binaryEncoder = binaryEncoder;
        this.binaryDecoder = binaryDecoder;
    }
}