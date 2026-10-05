package me.TheTealViper.composablecodec.transcoderbundle;

import me.TheTealViper.composablecodec.transcoderbundle.fieldinstruction.FieldInstruction;

/**
 * Represents the full instructions uniquely to handle getting/setting ONE child parameter of parent object
 *
 * @param <ParentObjectType> The type of the parent object for whom we are inserting to/extracting from the child object
 * @param <ChildObjectType> The type of the child object being inserted to/extracted from the parent object
 */
public class FieldTranscoder<ParentObjectType, ChildObjectType> {
	
	/** The key which allows us to distinguish between parent fields that happen to have the same child object type. */
	public final String key;
	public final FieldInstruction<ParentObjectType, ChildObjectType> fieldInstruction;
	/**
	 * Due to type erasing, we must store the recursive information to "pass the torch" to children within this object itself.
	 * This also helps us generate dependencies if necessary.
	 */
	public final TranscoderBundle<ChildObjectType> childBundle;
	
	/**
	 * Instantiates a new field transcoder.
	 *
	 * @param key the key
	 * @param fieldInstruction the field instruction
	 * @param childBundle the child bundle
	 */
	public FieldTranscoder(String key, FieldInstruction<ParentObjectType, ChildObjectType> fieldInstruction, TranscoderBundle<ChildObjectType> childBundle) {
		this.key = key;
		this.fieldInstruction = fieldInstruction;
		this.childBundle = childBundle;
	}
}