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
	public List<List<String>> stressTestList;
	public Map<String,List<String>> stressTestMap;
	
	/** The bundle containing all information necessary to save/load/insert/retrieve data from this object. */
	public static ObjectTranscoder<Player> TRANSCODER = null;
	
	/**
	 * This function creates the transcoder, adds all child parameters to it, registers it to the
	 * global registry, and then tells the transcoder to automatically generate the codec. This is a
	 * function because Codecs must be loaded in the correct dependency order, and if it was processed
	 * statically we can't guarantee order.
	 *
	 * @return the transcoder created
	 */
	public static ObjectTranscoder<?> registerTranscoderBundle() {
		return TRANSCODER = (ObjectTranscoder<Player>) new ObjectTranscoder<>(Player.class, Player::new)
			.set("weapon", new FieldTranscoder<>(
					(x) -> x.weapon,
					(x,y) -> x.weapon = y,
					Weapon.TRANSCODER))
			.set("numbers", new FieldTranscoder<>(
					(x) -> x.numbers,
					(x,y) -> x.numbers = y,
					new ListTranscoder<>(Integer.class)))
			.set("backpack", new FieldTranscoder<>(
					(x) -> x.backpack,
					(x,y) -> x.backpack = y,
					new ListTranscoder<>(Weapon.class)))
			.set("enchantments", new FieldTranscoder<>(
					(x) -> x.enchantments,
					(x,y) -> x.enchantments = y,
					new StringMapTranscoder<>(Enchantment.class)))
			.set("stressTestList", new FieldTranscoder<>(
					(x) -> x.stressTestList,
					(x,y) -> x.stressTestList = y,
					new ListTranscoder<>(new ListTranscoder<>(String.class))))
			.set("stressTestMap", new FieldTranscoder<>(
					(x) -> x.stressTestMap,
					(x,y) -> x.stressTestMap = y,
					new StringMapTranscoder<>(new ListTranscoder<>(String.class))))
			.buildCodec();
	}
}
