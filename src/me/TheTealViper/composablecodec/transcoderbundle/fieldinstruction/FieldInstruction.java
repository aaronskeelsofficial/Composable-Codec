package me.TheTealViper.composablecodec.transcoderbundle.fieldinstruction;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Packaged instance of getter/setter of child object from parent object. Keep in mind the FieldInstruction itself
 * is not keyed locally, it is keyed within the FieldTranscoder. Technically, we could probably move the functions
 * here into FieldTranscoder directly, but that doesn't help teach each role of what's happening. This is purely
 * to function as the bundling of getter/setter into one, associated package.
 *
 * @param <ParentObjectType> The type of the parent object for whom we are inserting to/extracting from the child object
 * @param <ChildObjectType> The type of the child object being inserted to/extracted from the parent object
 */
public class FieldInstruction<ParentObjectType, ChildObjectType> {
	
	/** Extract child from parent lambda  */
	public final Function<ParentObjectType, ChildObjectType> childFromParent;
	
	/** Insert child into parent lambda */
	public final BiConsumer<ParentObjectType, ChildObjectType> childIntoParent;
	
	/**
	 * Instantiates a new field instruction.
	 *
	 * @param getter the getter
	 * @param setter the setter
	 */
	public FieldInstruction(Function<ParentObjectType, ChildObjectType> getter, BiConsumer<ParentObjectType, ChildObjectType> setter) {
		this.childFromParent = getter;
		this.childIntoParent = setter;
	}
}
