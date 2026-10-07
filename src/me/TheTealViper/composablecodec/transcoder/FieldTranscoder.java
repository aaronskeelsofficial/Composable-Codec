package me.TheTealViper.composablecodec.transcoder;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Represents the full instructions uniquely to handle getting/setting ONE child parameter of parent object
 *
 * @param <ParentObjectType> The type of the parent object for whom we are inserting to/extracting from the child object
 * @param <ChildObjectType> The type of the child object being inserted to/extracted from the parent object
 */
public class FieldTranscoder<ParentObjectType, ChildObjectType> {
	
	/** The key which allows us to distinguish between parent fields that happen to have the same child object type. */
	public final String key;
	/** Extract child from parent lambda  */
	public final Function<ParentObjectType, ChildObjectType> childFromParent;
	/** Insert child into parent lambda */
	public final BiConsumer<ParentObjectType, ChildObjectType> childIntoParent;
	/**
	 * Due to type erasing, we must store the recursive information to "pass the torch" to children within this object itself.
	 * This also helps us generate dependencies if necessary.
	 */
	public final BaseTranscoder<ChildObjectType> childTranscoder;
	
	/**
	 * Instantiates a new field transcoder.
	 *
	 * @param key the key
	 * @param fieldInstruction the field instruction
	 * @param childBundle the child bundle
	 */
	public FieldTranscoder(
			String key,
			Function<ParentObjectType, ChildObjectType> childFromParent,
			BiConsumer<ParentObjectType, ChildObjectType> childIntoParent,
			BaseTranscoder<ChildObjectType> childTranscoder
	) {
		this.key = key;
		this.childFromParent = childFromParent;
		this.childIntoParent = childIntoParent;
		this.childTranscoder = childTranscoder;
	}
}