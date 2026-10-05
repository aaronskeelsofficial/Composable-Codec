package me.TheTealViper.composablecodec;

public class FICBundle<PARENTOBJECTTYPE, CHILDDATATYPE> {
	public final FieldInstruction<PARENTOBJECTTYPE, CHILDDATATYPE> fieldInstruction;
	public final HierarchicalCodec<CHILDDATATYPE> codec;
	public FICBundle(FieldInstruction<PARENTOBJECTTYPE, CHILDDATATYPE> fieldInstruction, HierarchicalCodec<CHILDDATATYPE> codec) {
		this.fieldInstruction = fieldInstruction;
		this.codec = codec;
	}
}