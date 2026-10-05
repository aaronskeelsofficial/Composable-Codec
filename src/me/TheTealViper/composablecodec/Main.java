package me.TheTealViper.composablecodec;

import java.nio.ByteBuffer;

public class Main {
	
	/*
	 * CHANGELOG:
	 * - Before, every java primitive would have a codec, though we couldn't compose codecs into
	 * 		representing a more advanced object like Codec<Itemstack> consisting of
	 * 		codec<Integer> count
	 * 		codec<String> itemname
	 * 		codec<Double> durability
	 * 		etc...
	 * - In short, we want ONE codec to represent a parent object type, and that codec can reference
	 * 		children codecs.
	 * - We do NOT want the parent object type to hold a list of primitive codecs.
	 * 		We want recursion, not composition for codecs in particular.
	 * 		Codec = 1-to-1 relationship with every parent object
	 *
	 * CURRENT ARCHITECTURE:
	 *      CodecBase<ChildDataType>(transformation lambdas...)
	 *          = how one value crosses the external-data boundary
	 *          = external data <-> primitive internal data
	 *
	 *      HierarchicalCodec<ChildDataType>(transformation lambdas) extends CodecBase
	 *      	= evolves off Codec to allow for recursive composition
	 *      	= the default implementation of codec for our project
	 *      	= each parent object is to have a 1-to-1 relationship with their representative codec
	 *
	 *      FieldInstruction<ParentObjectType, ChildDataType>(getter, setter)
	 *          = how that value belongs inside the parent object
	 *          = primitive java object  <-> complex java object
	 *          = currently each parent object has a sort of "List" many-to-one relationship with their child property FieldInstructions
	 *
	 *      FICBundle<ParentObjectType, ChildDataType>(FieldInstruction, HierarchicalCodec)
	 *      	= how to transfer child value to<->from external data &
	 *      		how to insert/retrieve internal data into/from complex object
	 *
	 *      KeyedFICBundleManager()
	 *      	= associates FICBundle with a key
	 *
	 *      ParentObject()
	 *      	- FICBundle<ParentObject>
	 *
	 * NEXT:
	 * - Right now we have composable, hierarchical, recursive Codecs, though our field instructions can't be composed. This significant
	 * 		difference in ideology feels odd. We should make the systems symmetrical.
	 */
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		HierarchicalCodec<Integer> c = new HierarchicalCodec<Integer>(
				object -> "",
				data -> 0,
				data -> ByteBuffer.allocate(0),
				data -> 0);
		FieldInstruction.Getter<Thing, Integer> g = new FieldInstruction.Getter<Thing, Integer>() {
			@Override
			public Integer get(Thing thing) {
				return thing.value;
			}
		};
		FieldInstruction.Setter<Thing, Integer> s = new FieldInstruction.Setter<Thing, Integer>() {
			@Override
			public void set(Thing thing, Integer value) {
				thing.value = value;
			}
		};
		FieldInstruction<Thing, Integer> f = new FieldInstruction<Thing, Integer>(g, s);
		FICBundle<Thing, Integer> bundle = new FICBundle<>(f, c);
	}
}
