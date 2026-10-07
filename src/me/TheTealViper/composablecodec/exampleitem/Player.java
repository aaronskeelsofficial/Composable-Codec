package me.TheTealViper.composablecodec.exampleitem;

import java.util.List;
import java.util.Map;

import me.TheTealViper.composablecodec.transcoder.FieldTranscoder;
import me.TheTealViper.composablecodec.transcoder.archetype.ListTranscoder;
import me.TheTealViper.composablecodec.transcoder.archetype.ObjectTranscoder;
import me.TheTealViper.composablecodec.transcoder.archetype.StringMapTranscoder;

/**
 * Sample class demonstrating how architecture works.
 */
public class Player{
	public Weapon weapon;
	public List<Integer> numbers;
	public List<Weapon> backpack;
	public Map<String,Enchantment> enchantments;
	
	/** The bundle containing all information necessary to save/load/insert/retrieve data from this object. */
	public static ObjectTranscoder<Player> BUNDLE = null;
	
	/**
	 * This function creates the transcoder bundle, adds all child parameters to it, registers it to the
	 * global registry, and then tells the transcoder bundle to automatically generate the codec. This is a
	 * function because Codecs must be loaded in the correct dependency order, and if it was processed
	 * statically we can't guarantee order.
	 *
	 * @return the transcoder bundle created
	 */
	public static ObjectTranscoder<?> registerTranscoderBundle() {
		return BUNDLE = (ObjectTranscoder<Player>) new ObjectTranscoder<>(Player.class, Player::new)
			.set("weapon",
					new FieldTranscoder<>("weapon",
						(x) -> x.weapon,
						(x,y) -> x.weapon = y,
						Weapon.BUNDLE))
			.set("numbers",
					new FieldTranscoder<>("numbers",
						(x) -> x.numbers,
						(x,y) -> x.numbers = y,
						new ListTranscoder<>(Integer.class)))
			.set("backpack", 
					new FieldTranscoder<>("backpack",
						(x) -> x.backpack,
						(x,y) -> x.backpack = y,
						new ListTranscoder<>(Weapon.class)))
			.set("enchantments", 
					new FieldTranscoder<>("enchantments",
						(x) -> x.enchantments,
						(x,y) -> x.enchantments = y,
						new StringMapTranscoder<>(Enchantment.class)))
			.buildCodec();
	}
}
