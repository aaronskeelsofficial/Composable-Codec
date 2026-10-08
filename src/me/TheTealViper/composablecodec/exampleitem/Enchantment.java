package me.TheTealViper.composablecodec.exampleitem;

import me.TheTealViper.composablecodec.transcoder.FieldTranscoder;
import me.TheTealViper.composablecodec.transcoder.archetype.ObjectTranscoder;
import me.TheTealViper.composablecodec.transcoder.archetype.PrimitiveTranscoder;

/**
 * Sample class demonstrating how architecture works.
 */
public class Enchantment {
	public String name;
	public int level;
	
	/** The bundle containing all information necessary to save/load/insert/retrieve data from this object. */
	public static ObjectTranscoder<Enchantment> BUNDLE;
	
	/**
	 * This function creates the transcoder bundle, adds all child parameters to it, registers it to the
	 * global registry, and then tells the transcoder bundle to automatically generate the codec. This is a
	 * function because Codecs must be loaded in the correct dependency order, and if it was processed
	 * statically we can't guarantee order.
	 *
	 * @return the transcoder bundle created
	 */
	public static ObjectTranscoder<?> registerTranscoderBundle() {
		return BUNDLE = (ObjectTranscoder<Enchantment>) new ObjectTranscoder<>(Enchantment.class, Enchantment::new)
				.set("name", new FieldTranscoder<>(
						(x) -> x.name,
						(x,y) -> x.name = y,
						PrimitiveTranscoder.STRING()))
				.set("level", new FieldTranscoder<>(
						(x) -> x.level,
						(x,y) -> x.level = y,
						PrimitiveTranscoder.INTEGER()))
				.buildCodec();
	}
}
