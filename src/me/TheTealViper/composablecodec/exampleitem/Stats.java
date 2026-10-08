package me.TheTealViper.composablecodec.exampleitem;

import me.TheTealViper.composablecodec.transcoder.FieldTranscoder;
import me.TheTealViper.composablecodec.transcoder.archetype.ObjectTranscoder;
import me.TheTealViper.composablecodec.transcoder.archetype.PrimitiveTranscoder;

/**
 * Sample class demonstrating how architecture works.
 */
public class Stats {
	public int damage;
	public int durability;
	
	/** The bundle containing all information necessary to save/load/insert/retrieve data from this object. */
	public static ObjectTranscoder<Stats> TRANSCODER;
	
	/**
	 * This function creates the transcoder, adds all child parameters to it, registers it to the
	 * global registry, and then tells the transcoder to automatically generate the codec. This is a
	 * function because Codecs must be loaded in the correct dependency order, and if it was processed
	 * statically we can't guarantee order.
	 *
	 * @return the transcoder created
	 */
	public static ObjectTranscoder<?> registerTranscoderBundle() {
		return TRANSCODER = (ObjectTranscoder<Stats>) new ObjectTranscoder<>(Stats.class, Stats::new)
				.set("damage", new FieldTranscoder<>(
						(x) -> x.damage,
						(x,y) -> x.damage = y,
						PrimitiveTranscoder.INTEGER()))
				.set("durability", new FieldTranscoder<>(
						(x) -> x.durability,
						(x,y) -> x.durability = y,
						PrimitiveTranscoder.INTEGER()))
				.buildCodec();
	}
	
}
