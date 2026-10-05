package me.TheTealViper.composablecodec;

import java.util.ArrayList;
import java.util.List;

import me.TheTealViper.composablecodec.core.CodecBase;

public class HierarchicalCodec<PARENTOBJECTTYPE> extends CodecBase<PARENTOBJECTTYPE> {
	private final List<CodecBase<?>> children;
	public HierarchicalCodec(JsonEncoder<PARENTOBJECTTYPE> jsonEncoder, JsonDecoder<PARENTOBJECTTYPE> jsonDecoder,
			BinaryEncoder<PARENTOBJECTTYPE> binaryEncoder, BinaryDecoder<PARENTOBJECTTYPE> binaryDecoder) {
		super(jsonEncoder, jsonDecoder, binaryEncoder, binaryDecoder);
		children = new ArrayList<>();
	}
	public HierarchicalCodec<PARENTOBJECTTYPE> add(HierarchicalCodec<?> codec) {
		children.add(codec);
		return this;
	}
	public HierarchicalCodec<PARENTOBJECTTYPE> remove(HierarchicalCodec<?> codec) {
		children.remove(codec);
		return this;
	}
	public HierarchicalCodec<PARENTOBJECTTYPE> replaceOrInsert(HierarchicalCodec<?> originalCodec, HierarchicalCodec<?> replacementCodec) {
		children.remove(originalCodec);
		children.add(replacementCodec);
		return this;
	}
}
