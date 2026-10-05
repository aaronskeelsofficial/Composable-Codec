package me.TheTealViper.composablecodec;

import java.nio.ByteBuffer;

public class Main {
	
	/*
	 * PREVIOUS NOTES:
	 * - Codec<T> handles translating an individual Java value of type T
	 *   across an external-data boundary.
	 * - We are using JSON and binary as examples of external representations.
	 * - A codec therefore answers:
	 *      "How do I turn a T into external data?"
	 *      "How do I turn that external data back into a T?"
	 *
	 * FIELDS:
	 * - Field<ParentObjectType, ChildType> connects an individual value
	 *   to the larger object that contains it.
	 * - It specifies:
	 *      1. the Codec<ChildType>
	 *      2. how to get ChildType from ParentObjectType
	 *      3. how to put ChildType into ParentObjectType
	 *
	 * - Example:
	 *      Field<Thing, Integer>
	 *   describes Thing.value.
	 *
	 * CURRENT ARCHITECTURE:
	 *      Codec<ChildType>
	 *          = how one value crosses the external-data boundary
	 *
	 *      Field<ParentObjectType, ChildType>
	 *          = how that value belongs inside the parent object
	 *
	 * NEXT:
	 * - We currently have a way to store how external data -> java primitive,
	 * 		and we have a way to interact/put java primitive -> java object,
	 * 		but what happens if our complex java object has multiple properties of the same type?
	 * 		What if Thing has thing.healthValue and thing.xpValue? How do we differentiate?
	 * 		Next we will make the instructions for java primitive -> java object keyed
	 */
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Codec<Integer> c = new Codec<Integer>(
				object -> "",
				data -> 0,
				data -> ByteBuffer.allocate(0),
				data -> 0);
		Field.Getter<Thing, Integer> g = new Field.Getter<Thing, Integer>() {
			@Override
			public Integer get(Thing thing) {
				return thing.value;
			}
		};
		Field.Setter<Thing, Integer> s = new Field.Setter<Thing, Integer>() {
			@Override
			public void set(Thing thing, Integer value) {
				thing.value = value;
			}
		};
		Field<Thing, Integer> f = new Field<Thing, Integer>(c, g, s);
	}
}
