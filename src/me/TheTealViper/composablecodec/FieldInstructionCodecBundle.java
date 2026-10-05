package me.TheTealViper.composablecodec;

public class FieldInstructionCodecBundle<PARENTOBJECTTYPE, CHILDDATATYPE> {
	public final FieldInstruction<PARENTOBJECTTYPE, CHILDDATATYPE> fieldInstruction;
	public final Codec<CHILDDATATYPE> codec;
	public FieldInstructionCodecBundle(FieldInstruction<PARENTOBJECTTYPE, CHILDDATATYPE> fieldInstruction, Codec<CHILDDATATYPE> codec) {
		this.fieldInstruction = fieldInstruction;
		this.codec = codec;
	}
}
