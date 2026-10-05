package me.TheTealViper.composablecodec.ficbundle.codec;

import java.util.ArrayList;
import java.util.List;

import me.TheTealViper.composablecodec.core.CodecBase;
import me.TheTealViper.composablecodec.core.CodecBase.BinaryDecoder;
import me.TheTealViper.composablecodec.core.CodecBase.BinaryEncoder;
import me.TheTealViper.composablecodec.core.CodecBase.JsonDecoder;
import me.TheTealViper.composablecodec.core.CodecBase.JsonEncoder;

/*
 * A hierarchical codec is a codec that is composed of other codecs
 */
public class HierarchicalCodec<OBJECTTYPE> extends CodecBase<OBJECTTYPE> {
	private final List<CodecBase<?>> children;
	private final Class<OBJECTTYPE> OBJECTTYPECLASS;
	public HierarchicalCodec(Class<OBJECTTYPE> OBJECTTYPECLASS, JsonEncoder<OBJECTTYPE> jsonEncoder, JsonDecoder<OBJECTTYPE> jsonDecoder,
			BinaryEncoder<OBJECTTYPE> binaryEncoder, BinaryDecoder<OBJECTTYPE> binaryDecoder) {
		super(jsonEncoder, jsonDecoder, binaryEncoder, binaryDecoder);
		this.OBJECTTYPECLASS = OBJECTTYPECLASS;
		children = new ArrayList<>();
		CodecRegistry.getGlobalRegistry().set(this.OBJECTTYPECLASS, this);
	}
	public HierarchicalCodec<OBJECTTYPE> add(HierarchicalCodec<?> codec) {
		children.add(codec);
		return this;
	}
	public HierarchicalCodec<OBJECTTYPE> remove(HierarchicalCodec<?> codec) {
		children.remove(codec);
		return this;
	}
	public HierarchicalCodec<OBJECTTYPE> replaceOrInsert(HierarchicalCodec<?> original, HierarchicalCodec<?> replacement) {
		children.remove(original);
		children.add(replacement);
		return this;
	}
}
