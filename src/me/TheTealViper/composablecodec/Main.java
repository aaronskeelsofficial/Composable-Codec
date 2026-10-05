package me.TheTealViper.composablecodec;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.TheTealViper.composablecodec.exampleitem.Player;
import me.TheTealViper.composablecodec.exampleitem.Stats;
import me.TheTealViper.composablecodec.exampleitem.Weapon;

/*
 * ARCHITECTURE:
 * TranscoderBundle<ParentObjectType> { //Represents the full instructions to handle getting/setting EVERY child parameter of parent object, and how to save/load parent object.
 * 		Map<String, FieldTranscoder<JavaObjectType,?>> constituents; //What transcoders does this bundle require?
 * 		Class<?> javaObjectTypeClass //Java generics erase type information from being workable, so explicitly what type of object does this represent?
 * 		Supplier<JavaObjectType> constructor; //How do we make a new instance of our parent java object?
 * 		Codec<JavaObjectType> codec; //How do we save/load java objects to/from json objects (NOT raw json string)?
 * 		TranscoderBundle<JavaObjectType> buildCodec(); //We need to delay building the parent codec until all FieldTranscoders have been added to the bundle
 * }
 * 
 * FieldTranscoder<ParentObjectType, ChildObjectType> { //Represents the full instructions uniquely to handle getting/setting ONE child parameter of parent object
 * 		String key; //The parent object can have multiple child objects of the same type, this is how we differentiate. The Codec also needs keys for JSON key-value pairs.
 * 		FieldInstruction<ParentObjectType, ChildObjectType> fieldInstruction; //How to we insert/retrieve child objects into/from parent objects?
 * 		TranscoderBundle<ChildObjectType> childBundle; //How do we recursively "pass the torch" to save/load/parse the child object?
 * }
 * 
 * FieldInstruction<ParentObjectType, ChildObjectType> { //Packaged instance of getter/setter of child object from parent object
 * 		Function<ParentObjectType, ChildObjectType> childFromParent(); //How do we extract the child object given the parent object?
 * 		BiConsumer<ParentObjectType, ChildObjectType> childIntoParent(); //How do we load the child object into the parent object?
 * 		//Keep in mind the FieldInstruction itself is not keyed locally, it is keyed within the FieldTranscoder.
 * 		//Technically, we could probably move the functions here into FieldTranscoder directly, but that doesn't help teach each role of what's happening.
 * }
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
 * - This architecture successfully allows us to handle Java Object to Java Object composition recursively including it's saving/loading to/from json
 * - This architecture does NOT currently support arrays/lists, maps, or sets in terms of saving/loading/insertion/extraction
 */
public class Main {
	
	public static void main(String[] args) {
		//Setup. Note: order matters here. Parent codecs depending on child codecs must be made AFTER children they depend on.
		Stats.registerTranscoderBundle();
		Weapon.registerTranscoderBundle();
		Player.registerTranscoderBundle();
		
		//Build sample object within java
		Stats s = new Stats();
		s.damage = 5;
		s.durability = 10;
		Weapon w = new Weapon();
		w.stats = s;
		Player p = new Player();
		p.weapon = w;
		
		//Test recursive entire chain of parent java object -> child objects... -> json object -> json string
		JsonObject json = (JsonObject) Player.BUNDLE.codec.javaToJson.apply(p);
		System.out.println("[json]: " + json);
		String st = json.toString();
		System.out.println("JSON: " + st);
		
		//Test recursive entire chain of json string -> json object -> child objects... -> parent java object
		JsonObject json2 = JsonParser.parseString(st).getAsJsonObject();
		System.out.println("[json2]: " + json2);
		Player p2 = Player.BUNDLE.codec.jsonToJava.apply(json2);
		System.out.println(p2.weapon.stats.damage + "," + p2.weapon.stats.durability);
	}
}
