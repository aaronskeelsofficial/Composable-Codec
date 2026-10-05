package me.TheTealViper.composablecodec.ficbundle;

import me.TheTealViper.composablecodec.ficbundle.codec.HierarchicalCodec;
import me.TheTealViper.composablecodec.ficbundle.fieldinstruction.FieldInstruction;

public class FICBundle<PARENTOBJECTTYPE, CHILDDATATYPE> {
	public final String key;
	public final FieldInstruction<PARENTOBJECTTYPE, CHILDDATATYPE> fieldInstruction;
	public final HierarchicalCodec<CHILDDATATYPE> codec;
	public FICBundle(String key, FieldInstruction<PARENTOBJECTTYPE, CHILDDATATYPE> fieldInstruction, HierarchicalCodec<CHILDDATATYPE> codec) {
		this.key = key;
		this.fieldInstruction = fieldInstruction;
		this.codec = codec;
	}
}