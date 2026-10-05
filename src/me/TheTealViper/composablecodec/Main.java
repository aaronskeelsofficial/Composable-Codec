package me.TheTealViper.composablecodec;

import java.nio.ByteBuffer;

public class Main {
	
	/*
	 * CHANGELOG:
	 * - Here we separated the codec from the field modification instructions and made a new explicit object
	 * 		for combining them
	 * - We also made a keyed version of this bundle to allow a lookup approach
	 * CURRENT ARCHITECTURE:
	 *      Codec<ChildDataType>(transformation lambdas...)
	 *          = how one value crosses the external-data boundary
	 *          = external data <-> primitive internal data
	 *
	 *      FieldInstruction<ParentObjectType, ChildDataType>(getter, setter)
	 *          = how that value belongs inside the parent object
	 *          = primitive java object  <-> complex java object 
	 *          
	 *      FieldInstructionCodecBundle<ParentObjectType, ChildDataType>(FieldInstruction, Codec)
	 *      	= how to transfer child value to<->from external data &
	 *      		how to insert/retrieve internal data into/from complex object
	 *      
	 *      KeyedFICBundleManager()
	 *      	= associates FICBundle with a key
	 *
	 * NEXT:
	 * - 
	 */
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Codec<Integer> c = new Codec<Integer>(
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
		FieldInstructionCodecBundle<Thing, Integer> bundle = new FieldInstructionCodecBundle<>(f, c);
	}
}
