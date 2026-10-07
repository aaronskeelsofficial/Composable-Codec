package me.TheTealViper.composablecodec.exampleitem;

import java.util.List;

import me.TheTealViper.composablecodec.transcoder.FieldTranscoder;
import me.TheTealViper.composablecodec.transcoder.archetype.ListTranscoder;
import me.TheTealViper.composablecodec.transcoder.archetype.ObjectTranscoder;

/**
 * Sample class demonstrating how architecture works.
 */
public class Weapon{
	public Stats stats;
	public List<Double> killmetadata;
	
	/** The bundle containing all information necessary to save/load/insert/retrieve data from this object. */
	public static ObjectTranscoder<Weapon> BUNDLE;
	
	/**
	 * This function creates the transcoder bundle, adds all child parameters to it, registers it to the
	 * global registry, and then tells the transcoder bundle to automatically generate the codec. This is a
	 * function because Codecs must be loaded in the correct dependency order, and if it was processed
	 * statically we can't guarantee order.
	 *
	 * @return the transcoder bundle created
	 */
	public static ObjectTranscoder<?> registerTranscoderBundle() {
		return BUNDLE = (ObjectTranscoder<Weapon>) new ObjectTranscoder<>(Weapon.class, Weapon::new)
				.set("stats", new FieldTranscoder<>("stats",
						(x) -> x.stats,
						(x,y) -> x.stats = y,
						Stats.BUNDLE))
				.set("killmetadata", new FieldTranscoder<>("killmetadata",
						(x) -> x.killmetadata,
						(x,y) -> x.killmetadata = y,
						new ListTranscoder<>(Double.class)))
				.buildCodec();
	}
	
}
