package me.TheTealViper.composablecodec;

import java.nio.ByteBuffer;

public class Main {
	
	/*
	 * The ideas of the architecture here are as follows
	 * - The codec handles TRANSLATING *INDIVIDUAL EXTERNAL* DATA from one type to another,
	 * 		namely as an example JSON -> Java Integer or BSON -> Java String
	 * - Again, this is for individual values, *not cohesive complex objects*
	 * - Now, once we have individual values, when we group them we can construct complex Java objects
	 * - Fields tell us how we go from individual JAVA data objects -> complex Java data objects
	 * - This example of field only allows us to use "Thing" object type. The next phase will address this.
	 */
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Codec<Integer> c = new Codec<Integer>(
				object -> "",
				data -> 0,
				data -> ByteBuffer.allocate(0),
				data -> 0);
		Field.Getter<Integer> g = new Field.Getter<Integer>() {
			@Override
			public Integer get(Thing thing) {
				return thing.value;
			}
		};
		Field.Setter<Integer> s = new Field.Setter<Integer>() {
			@Override
			public void set(Thing thing, Integer value) {
				thing.value = value;
			}
		};
		Field<Integer> f = new Field<Integer>(c, g, s);
	}
}
