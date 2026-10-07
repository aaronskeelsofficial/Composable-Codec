package me.TheTealViper.composablecodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.TheTealViper.composablecodec.exampleitem.Enchantment;
import me.TheTealViper.composablecodec.exampleitem.Player;
import me.TheTealViper.composablecodec.exampleitem.Stats;
import me.TheTealViper.composablecodec.exampleitem.Weapon;
import me.TheTealViper.composablecodec.transcoder.archetype.PrimitiveTranscoder;

/*
 * ARCHITECTURE:
 * FieldTranscoder<ParentObjectType, ChildObjectType> { //Represents the full instructions uniquely to handle getting/setting ONE child parameter of parent object
 * 		String key; //The parent object can have multiple child objects of the same type, this is how we differentiate. The Codec also needs keys for JSON key-value pairs.
 * 		Function<ParentObjectType, ChildObjectType> childFromParent(); //How do we extract the child object given the parent object?
 * 		BiConsumer<ParentObjectType, ChildObjectType> childIntoParent(); //How do we load the child object into the parent object?
 * 		TranscoderBundle<ChildObjectType> childBundle; //How do we recursively "pass the torch" to save/load/parse the child object?
 * }
 * 		//Note: FieldTranscoder has the word "Transcoder" in it, but its form and function is significantly distinct from BaseTranscoder behavior. These are specific to individual fields.
 * 
 * BaseTranscoder<ParentObjectType> { //Informationally represents everything necessary to achieve ParentObjectType <-> JSON. Functionally, acts as an interface for builders/generation.
 * 		Map<String, FieldTranscoder<JavaObjectType,?>> constituents; //What transcoders does this bundle require?
 *		Type javaObjectTypeRuntime; //Java generics erase type information from being workable, so explicitly what type of object does this represent?
 *		Supplier<JavaObjectType> constructor; //How do we make a new instance of our parent java object?
 *		Codec<JavaObjectType> codec; //How do we insert/retrieve a java object into/from another java object
 * }
 * 
 * Transcoder Archetypes: //Acts as an automatically generational template on top of BaseTranscoder to handle Codec behavior
 * 		PrimitiveTranscoder<ObjectType> //These are pre-made templates offering functionality for the most core data types likely found in JSON (int, float, double, string)
 * 		ObjectTranscoder<ObjectType> {} //Assumes the object in question being loaded/saved/manipulated/extracted is a standard Object
 * 		ListTranscoder<ObjectType> {} //Assumes the object in question being loaded/saved/manipulated/extracted is a List of a standard Object.
 * 			//Note: This would likely never be your starting place when making a new Transcoder. ListTranscoders are often implemented as the *child's* transcoder when adding FieldTranscoders.
 * 		StringMapTranscoder<ObjectType> {} //Assumes the object in question being loaded/saved/manipulated/extracted is a String keyed Map of standard Objects.
 * 			//Note: This would likely never be your starting place when making a new Transcoder. StringMapTranscoders are often implemented as the *child's* transcoder when adding FieldTranscoders.
 * 
 * Codec<ParentObjectType> { //How do we convert to/from java object to/from middleman object prior to raw data (JSON, BSON, Binary, etc)
 * 		Map<Class<?>, Codec<?>> children; //Which data types does this codec rely on/build off of? Technically this information is in the TranscoderBundle, but this gives a more direct schema.
 * 		Class<JavaObjectType> javaObjectTypeClass; //The global registry keys are dependent on object type, generics erase this info, what type is this representing?
 * 		//Technically this isn't necessary here as it's in the TranscoderBundle, but it would need to be passed to the Codec temporarily still for the global registry in the constructor.
 * 		Function<JavaObjectType, JsonElement> javaToJson; //How do we convert from java object to json object
 *	 	public final Function<JsonElement, JavaObjectType> jsonToJava; //How do we convert from json object to java object
 *		public final Function<JavaObjectType, ByteBuffer> javaToByteBuffer; //How do we convert from java object to bytebuffer
 *		public final Function<ByteBuffer, JavaObjectType> byteBufferToJava; //How do we convert from bytebuffer to java object
 * }
 * 
 * NOTES:
 * - This architecture does a good job at teaching the separation of roles, their interplay, and actual implementations of ideas
 * - This architecture does NOT currently remove all redundancies and congregate all functionality as condensed as is reasonably possible.
 * - This architecture successfully allows us to handle Java Object and List composition recursively including their saving/loading to/from json
 * - This architecture does NOT currently support arrays or sets (these are basically lists) nor maps (could come in future) in terms of saving/loading/insertion/extraction
 * - This architecture does NOT currently support multi-layered lists (List<List<String>>). In this regard, recursive composition breaks.
 */
public class Main {
	
	public static void main(String[] args) {
		//Setup. Note: order matters here. Parent codecs depending on child codecs must be made AFTER children they depend on.
		PrimitiveTranscoder.registerPrimitiveTranscoders();
		Enchantment.registerTranscoderBundle();
		Stats.registerTranscoderBundle();
		Weapon.registerTranscoderBundle();
		Player.registerTranscoderBundle();
		
		//Build sample object manually within java
		Player p = new Player();
		((Runnable) () -> {
			Stats s = new Stats();
			s.damage = 5;
			s.durability = 10;
			Weapon w = new Weapon();
			w.stats = s;
			List<Double> m = new ArrayList<>(List.of(0.6,0.7,420d,.69d));
			w.killmetadata = m;
			p.weapon = w;
		}).run();
		List<Integer> n = new ArrayList<>(List.of(6,7,420,69));
		p.numbers = n;
		//Configure backpack
		List<Weapon> b = new ArrayList<>();
		((Runnable) () -> {
			Stats s = new Stats();
			s.damage = 1;
			s.durability = 2;
			Weapon w = new Weapon();
			w.stats = s;
			List<Double> m = new ArrayList<>(List.of(1d,2d));
			w.killmetadata = m;
			b.add(w);
		}).run();
		((Runnable) () -> {
			Stats s = new Stats();
			s.damage = 3;
			s.durability = 4;
			Weapon w = new Weapon();
			w.stats = s;
			List<Double> m = new ArrayList<>(List.of(1d,2d,3d,4d));
			w.killmetadata = m;
			b.add(w);
		}).run();
		((Runnable) () -> {
			Stats s = new Stats();
			s.damage = 5;
			s.durability = 6;
			Weapon w = new Weapon();
			w.stats = s;
			List<Double> m = new ArrayList<>(List.of(1d,2d,3d,4d,5d,6d));
			w.killmetadata = m;
			b.add(w);
		}).run();
		p.backpack = b;
		Map<String,Enchantment> e = new HashMap<>();
		((Runnable) () -> {
			Enchantment en = new Enchantment();
			en.name = "damagebuff";
			en.level = 1;
			e.put(en.name, en);
		}).run();
		((Runnable) () -> {
			Enchantment en = new Enchantment();
			en.name = "superjump";
			en.level = 2;
			e.put(en.name, en);
		}).run();
		p.enchantments = e;
		
		//Test recursive entire chain of parent java object -> child objects... -> json object -> json string
		JsonObject json = (JsonObject) Player.BUNDLE.codec.javaToJson.apply(p);
		System.out.println("[json]: " + json);
		
		//Test recursive entire chain of json string -> json object -> child objects... -> parent java object
		String st = json.toString();
		JsonObject json2 = JsonParser.parseString(st).getAsJsonObject();
		System.out.println("[json2]: " + json2);
		Player p2 = Player.BUNDLE.codec.jsonToJava.apply(json2);
		System.out.println(p2.weapon.stats.damage + "," + p2.weapon.stats.durability);
		System.out.println(p2.backpack.get(2).killmetadata);
		System.out.println(p2.enchantments.get("superjump").level);
	}
}
