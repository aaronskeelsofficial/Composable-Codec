package me.TheTealViper.composablecodec.exampleitem;

import me.TheTealViper.composablecodec.transcoderbundle.FieldTranscoder;
import me.TheTealViper.composablecodec.transcoderbundle.TranscoderBundle;
import me.TheTealViper.composablecodec.transcoderbundle.fieldinstruction.FieldInstruction;

/**
 * Sample class demonstrating how architecture works.
 */
public class Stats {
	public int damage;
	public int durability;
	
	/** The bundle containing all information necessary to save/load/insert/retrieve data from this object. */
	public static TranscoderBundle<Stats> BUNDLE;
	
	/**
	 * This function creates the transcoder bundle, adds all child parameters to it, registers it to the
	 * global registry, and then tells the transcoder bundle to automatically generate the codec. This is a
	 * function because Codecs must be loaded in the correct dependency order, and if it was processed
	 * statically we can't guarantee order.
	 *
	 * @return the transcoder bundle created
	 */
	public static TranscoderBundle<?> registerTranscoderBundle() {
		return BUNDLE = new TranscoderBundle<>(Stats.class, Stats::new)
				.set("damage", new FieldTranscoder<>("damage",
						new FieldInstruction<>(
								(x) -> x.damage,
								(x,y) -> x.damage = y),
						TranscoderBundle.INTEGER()))
				.set("durability", new FieldTranscoder<>("durability",
						new FieldInstruction<>(
								(x) -> x.durability,
								(x,y) -> x.durability = y),
						TranscoderBundle.INTEGER()))
				.buildCodec();
	}
	
}
