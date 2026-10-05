package me.TheTealViper.composablecodec;

import java.nio.ByteBuffer;

import me.TheTealViper.composablecodec.ficbundle.FICBundle;
import me.TheTealViper.composablecodec.ficbundle.codec.HierarchicalCodec;
import me.TheTealViper.composablecodec.ficbundle.fieldinstruction.FieldInstruction;
import me.TheTealViper.composablecodec.ficbundle.fieldinstruction.FieldInstruction.Getter;
import me.TheTealViper.composablecodec.ficbundle.fieldinstruction.FieldInstruction.Setter;

public class Main {
	
	/*
	 * CONCEPTUAL RECAP:
	 *  - We start from scratch. We will introduce two things we know for sure:
	 *  	1. We need a way to read/write data to JSON/binary
	 *  	2. We need a way to read/write java object parameters
	 *  - We address #1 by introducing Codecs. Their job is via lambdas to transform external data <-> internal data
	 *  - We address #2 by introducing FieldInstructions. Their job is to give instructions as to how to set/get a java object's field via function lambdas.
	 *  - Let's say the object of our current consideration is the following
	 *  	Weapon {
	 *  		int damage;
	 *  	}
	 *  - So a codec will take a type, and have associated getters/setters for json and binary for that type
	 *  - A FieldInstruction will take the parent type and the property type, and have associated getters/setters for their relationship
	 *  - Now we have a working first stage prototype
	 *  ---------------------------------------------------
	 *  - Now let's say the object of our consideration changes to
	 *  	Weapon {
	 *  		int damage;
	 *  		int durability;
	 *  	}
	 *  - If we pass just a parent type and child parameter type, how will the functions know if we are talking about damage or durability?
	 *  - We must modify our approach to distinguish these cases. We will associate a String "key" to know if we intend to set damage or durability.
	 *  	- Note: This key is technically a RANDOM string allowed to be completely detached from the actual variable name, though it should be related for organization
	 *  - So now, we know in some sense our FieldInstructions must have a key distinguishing which child property they belong to
	 *  - Now we have a working second stage prototype
	 *  ---------------------------------------------------
	 *  - Now let's say our entire consideration changes. Currently we are thinking on the Java end, and flowing our way DOWN to JSON.
	 *  - But what happens if we start with JSON and flow into Java? Let's say we have the following JSON representation of a weapon:
	 *  	{
	 *  		"damage": 5,
	 *  		"durability": 10,
	 *  	}
	 *  - Let's imagine being told to turn this JSON into a weapon object. We hit our first key-value pair and... what do we do? In this example it's obvious
	 *  	that the value type is an integer, but that is not always the case. 5.0 vs 5.0 can be a float or a double. Or more complex than that, we can store
	 *  	entire other objects within a value field. Imagine the java object:
	 *  	Player {
	 *  		Weapon {
	 *  			int damage;
	 *  			int durability;
	 *  		}
	 *  	}
	 *  - Now imagine the JSON representation:
	 *  	{
	 *  		"weapon": {
	 *  			"damage": 5,
	 *  			"durability": 10,
	 *  		},
	 *  	}
	 *  - Decoding this new JSON, our first key is "weapon" and the value type is... what? How would we know? Also, when saving from Java -> JSON, how did we know
	 *  	what key to use in the first place? Do we just use the class name as the key?
	 *  - Here is what will come of those questions:
	 *  	1. You should NOT use the class name because damage and durability are both Integers so that would yield the same key for both
	 *  	2. You should *probably* use something logical like the name of the child parameter name in the parent object
	 *  - The next useful thought is this, if the FieldInstruction requires a key due to type overlap in child properties,
	 *  	and Codecs require a key for external -> internal procedures, why *not just make them the same key*?
	 *  - This brings us to a decent conceptual evolution stopping point
	 *  -----------------------------------------------------
	 *  - Now with where the last part left off, assuming FieldInstruction and Codec both use an identical key, we have a fork in the road as to what our architecture does:
	 *  	1. FieldInstruction is the key authority, housing it data structure wise. Codec's associated type can be used to lookup the parent object's FieldInstruction
	 *  		for that type which will yield the key. This prevents duplicate data and desync issues, though arbitrarily chooses an anchor for the data.
	 *  	2. FieldInstruction will house the key... AND Codec will also house the same key. This doesn't pick favorites arbitrarily and keeps use of data organized and clear.
	 *  		The threat of this approach is the duplication of data and likelihood/possibility of desync between the two sources.
	 *  	3. We make a new object that bundles the FieldInstruction and Codec into a singular object, and this object holds the key. This avoids arbitrary favorites and
	 *  		data duplication, at the expense (how expensive is up to you) of class bloat.
	 *  - This project will be going with approach #3, though know that all are valid with no downsides so severe they absolutely shouldn't be used. #2 is pretty risky though.
	 *  -----------------------------------------------------
	 *  - Now let's switch gears a bit and again consider the java object
	 *  	Player {
	 *  		Weapon {
	 *  			int damage;
	 *  			int durability;
	 *  		}
	 *  	}
	 *  - For our Codec, we want to pass in only a single type and have the Codec handle translating that type into JSON/binary
	 *  - For the above object, we could have our Player codec manually handle every step of storing itself. Perhaps the Player codec does something akin to
	 *  	1. save the position
	 *  	2. save the weapon aka
	 *  		1. save the damage
	 *  		2. save the durability
	 *  - But now let's say we'll also have an inventory storage codec at some point. That storage will likely need to be able to save weapons. Should EVERY object that
	 *  	can hold weapons need to copy-paste the exact same weapon saving code into their codec?
	 *  - The answer you said was hopefully, no. Please no. We can say save the player, and when we encounter the weapon, we'll pass the saving burden off on Codec<Weapon>.
	 *  - This chain of command passing the baton through each child type allows us to define once and pass the torch where applicable.
	 *  - The question now is, where do all these torchbearers live?
	 *  	1. We could have one centralized location, a Map<Type, Codec<Type>>, where we give the exact type we need and get the way to load it.
	 *  	2. We could have a composed/builder structure where when we make a new parent object, we say:
	 *  		Parent Codec ---contains---> Child Codec Reference ---contains---> Child Codec Reference ...
	 *  	- Approach #1 gives us a comprehensive list of ALL codecs that exist
	 *  	- Approach #2 gives us an analyzable schema/relationship structure for all codec dependencies
	 *  	- I'm not sure which we will necessarily need in our project, but both seem like important questions to answer, so I will opt to have BOTH in this project.
	 *  		- Note: This doesn't imply a global FieldInstruction registry since they aren't tied to only a type, they are tied to a specific parent object and
	 *  			would never need to be referenced globally.
	 *  - This is a decent conclusion to this concept evolution.
	 *  -----------------------------------------------------
	 *  - Lastly for this phase/recap, the last architectural decision requires a pain in the you know what change
	 *  - Currently, all our type passing is done via Generic types. To understand the issue called "type erasure", we need to know how generics work.
	 *  	- In Java, generics work by downcasting to the most generic type within the "Generic" code itself (typically this means "Object" unless you specify <T extends xyz>)
	 *  	- All the code on the OUTSIDE of the generic class interacting with it, will get parenthesis type casting inserted into their usage. So the Generic code all becomes
	 *  		literal "Object" type oriented, and then when it's passed off the code on the outside gets String s = (String) o where o is the generic object.
	 *  - With an understanding of how Java works, and hopefully clarity on why it's called "type erasure", the code within the generic class literally just sees an Object type.
	 *  - Now the direct pain in the side it causes us. If we want to have a global Codec registry, we need to associate the type the codec works on with itself
	 *  - We could have devs create the codec and register it themselves, though that's just adding busywork. If we create a codec, one would likely expect it to also be registered
	 *  	So therefore the Codec's constructor should probably automatically handle registration... but the Codec class itself has no clue what type of object it's working on...
	 *  	because of *type erasure* woohoo. So we CAN NOT DO something like PARENTOBJECTTYPE.class since PARENTOBJECTTYPE isn't a dynamic type at runtime... it's an Object.
	 *  - The best case solution is to force a disgusting constructor where we (seemingly) randomly toss in duplicate data information of the class type as an input
	 *  - Codec<PARENTDATATYPE>(getters..., setters...) becomes Codec<PARENTDATATYPE>(PARENTDATATYPE.class, getters..., setters...)
	 *  - It's gross, yes. It's stupid, yes. Rust is better, yes. But this is what we have to work with so suck it up buttercup.
	 */
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		HierarchicalCodec<Integer> c = new HierarchicalCodec<>(
				Integer.class,
				object -> "",
				data -> 0,
				data -> ByteBuffer.allocate(0),
				data -> 0);
		FieldInstruction.Getter<Thing, Integer> g = new Getter<>() {
			@Override
			public Integer get(Thing thing) {
				return thing.value;
			}
		};
		FieldInstruction.Setter<Thing, Integer> s = new Setter<>() {
			@Override
			public void set(Thing thing, Integer value) {
				thing.value = value;
			}
		};
		FieldInstruction<Thing, Integer> f = new FieldInstruction<>(g, s);
		FICBundle<Thing, Integer> bundle = new FICBundle<>("value", f, c);
	}
}
