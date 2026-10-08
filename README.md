# Composable-Codec
Hytale is a video game that utilizes a fairly automatic and ease-of-integration friendly codec system that uses composition to minimize boilerplate necessary to introduce functionality for data saving/loading/manipulating/extraction. I wanted to try to make my own version from scratch to see how far I'd get. I also wanted to document the evolution of the architecture, literally showing step by step how I iterated to finally arrive wherever I do. Many times, simply explaining a finished system that underwent many iterations loses the clarity of WHY certain decisions were made. I want to preserve the entire chain of thought for myself looking back or others looking to learn by following in my (potentially unprofessional, though real) footsteps. Iterations will either be marked by branches or tags and only the final, fully capable system will exist in the main branch (if I make it that far).

---

# V9 - Infinite Recursion
### Mission Statement
At V8, we had Object composability, and we hacked on `List<Object>` functionality as well, though the List functionality wasn't truly recursively composable (we could not do `List<List<String>>` for example). The point of V9 was to alter the guts of the code to support true recursive composability through and through. Now Objects, Lists, and Maps can be stacked onto one another in as deep a nesting as one wishes. `List<Map<String, List<List<CustomObject>>>>` is now possible if you're crazy enough to stack it that far.

### Notes on Architecture
 - The change necessary to make this work is that the role of BaseTranscoder was simplified such that ObjectTranscoder, ListTranscoder, and StringMapTranscoder aren't expected to behave so... universally similar. They each are their own class, and they should act like it more boldly. ListTranscoder and StringMapTranscoder now hold references to the child type transcoder within them
 - To be able to retrieve this child type transcoder without devs needing a disgusting constructor layout, we needed to re-implement the transcoder registry which now lives in BaseTranscoder.
 - Now when we, for example, do `new ObjectTranscoder()...set(..., new FieldTranscoder(..., new ListTranscoder<>(new ListTranscoder<>(Integer.class))))`, that will process from the inside scopes outward. It will first check the `ObjectTranscoder<Integer>` exists, then it will *create* the `ListTranscoder<Integer>` referencing the `ObjectTranscoder<Integer>`, and then it will *create* the `ListTranscoder<List<Integer>>` referencing the just created `ListTranscoder<Integer>`.
 - FieldTranscoders no longer need a key passed in their constructor as this cleans up the ObjectTranscoder syntax such that when a FieldTranscoder is added to an ObjectTranscoder, it will pass *its* key on automatically. Now there is no need to duplicate information passed in APIs interacted with by most users. This does place "phantom burden" on those who don't know the nuance, even though it's documented in the javadocs, where if they use the constructor without the key they will NEED to manually set it at some point.

### Concluding Thoughts
At this point in the project, it is actually pretty feature complete in accordance to the initial encompassing goal of the whole thing. Codecs can load/save/manipulate/extract information regarding a parent type compositionally and recursively. There is adequate support for an ecosystem welcoming mods who interact cross-mod. Pros said, while working on the project new information became apparently I wouldn't have even conceived of in the beginning. "Closing" thoughts regarding ways I was wrong, mistaken, or could improve that I have on this project are:
 - I only implemented JSON functionality as I assumed "Binary" implementations actually meant "BSON" implementations wherein BSON is just JSON but not plaintext. So I figured if JSON worked BSON would work trivially. What I came to realize further in is my "Binary" implementation is NOT explicitly BSON. BSON is good for saved data where the memory footprint of "keys" isn't that big a deal, but for network packages you really don't want to have to send information as key-value pairs as this severely bloats bandwidth. So I'd need to think through a binary approach. My ignorant brain says "Oh you can just append all information with size information bingo bango it'll be easy" but I'm sure actual implementations won't agree with that simplicity and growable things like Lists of custom Objects with multiple fields would begin to be a pain in the neck.
 - I did not know I would need a JSON parsing middleman library in the beginning, and I sought out GSON halfway through as a means to connect the dots to the workflow process. But what I didn't realize in that chaos is that GSON... cannn basically do everything this entire project represents. GSON can parse a JSON string into a recursively composed java object through Java Reflection utils perfectly. It not only can do it, but it probably does it better. After researching, I see that using reflection for such a task is quite GC heavy with lots of bloating intermediate memory traces. Hopefully that justifies not just using that instead lol.
 - Upon finalizing the true recursive composability in V9 and exploring what the next range to explore would be, I realized I have no support for polymorphism. If I want moddable support, that would mean my game would provide a class or inferface of Block for example. Then the mod would create a new class extending Block. So now my server holds data like `List<Block>` perhaps, and one of those blocks may be a fully custom block type. now, when I save it, I can't just save it as a "Block" or it doesn't know the custom information added by the new custom class. So Hytale and GSON both do something along the lines of adding a "TYPE" tag to the JSON object, and that TYPE string maps to a codec for the true custom object. In theory this doesn't seem too complicated to implement but the ignorant endeavors always start that way huh. I do wonder why they use "type" and "TYPE" to represent the type instead of a more custom delimited tag that is less likely to be a commonly used english word that a REAL key might take.

---

# V8 - Maps and Lists
### Mission Statement
V7 put the project in a place where it actually works properly, though not fully. We could encode and decode explicitly key-value pairs into object-to-object relationships. This didn't account for Array/List behavior, nor keyed map behavior. All of JSON is key-value so a Map implementation seems interesting, but the difference in explicitly supporting a Map is that when decoding from JSON -> Java Object, we *don't need to know every key we're looking for*. Object "map" is like "we know we are looking for this key and it is this type" whereas Map "map" is "we don't know what keys we're looking for, but every key we come across will be this type". Similar, but not the same.

### Notes on Architecture
 - This modification required a group up overhaul a bit. Running through the entirety of the code base, all instances of `Class<?>` which we used to represent the type being talked about were replaced with `Type`. This is because Class only holds information regarding the base type of the object. This means `List<String>` can only be represented as a List. Type allows what is called parameterized typing which means we can store the fact that it's a List holding the generic parameter type of String.
 - Another change of this iteration was the renaming of classes to better represent their role. "TranscoderBundle" became "TranscoderBase" among the other aptly named archetypes of specific implementations. One lingering point of confusing is the preservation of "FieldTranscoder" having completely unique role and function to all other classes with the name transcoder in them. I just can't think of another good name.
 - In this architecture, all transcoders are "BaseTranscoder" at heart. Other transcoder archetype classes act more like buidlers/factories templating premade generation patterns of a BaseTranscoder.
 - In this architecture, the "global transcoder registry" had to be reworked. While typing information fundamentally changed and archetypes were introduced, it seemed simpler to just nuke the transcoder registry entirely. The new approach is that *a* registry lives in the ObjectTranscoder. It's not meant to be all-encompassing though. It only registers transcoders initialized through the ObjectTranscoder class. Perhaps a global registry could be re-introduced for BaseTranscoder in the future. A big reason it was removed was that I don't want external users ever needing to interact with ParameterizedTokens. I want them to be able to pass simple "Weapon.class" arguments, and they get handled as parameterized types on the backend. If I add a reliance on a global registry with accurate parameterized typing and encourage that as the approach to obtain other mods' transcoders, then devs would now need to learn more. Don't want that.
 
### Concluding Thoughts
I didn't add Set or Array support officially, but they are effectively just Lists from a data storage standpoint so if someone wants those, they can easily work within the List workflow. I also don't currently allow multi-layered List recursion `List<List<String>>` for example. I'm not sure how important that would be to implement, but my infrastructure assumes a List is a single layer deep and is a List of an Object. Overall the project has come far enough that I would say it is complete. Stretch goals could be allowing multi-layered Lists, explicitly supporting Set/Array rather than expecting hacky List workarounds, and introducing external systems that do things like: Make transcodable objects build off an interface, intelligently queue and register transcoders/codecs in order of dependency, have an accessible global lookup for any transcoder needed, etc. Project is complete though. I have researched Hytale's approach a bit more in depth now, having made my own system, and I see our overlaps and where they did things differently. I have learned, and that was the goal from the beginning. Mission success.

---

# V7 - Finalize First Demo
### Mission Statement
The goal here was to no longer limit my simulations to java->json and instead think of json->java as well. This was achieved. V7 is meant to be my first, semi-formalized checkpoint where an intended goal has been met, and whether or not the project extends depends on the creation of a new goal. As such a checkpoint, javadocs have been semi-meticulously typed.

### Notes on Architecture
General Notes
 - Firstly, code layout and class names were changed for clarity sake (hopefully).
 - Secondly, it has yet to be acknowledged in these changelogs but we have working global registries now. The idea behind this is if we are working within Hytale which encourages mod makers to integrate into their ecosystem and communicate with other mods, these registries will allow third party mods to work on data from other mods. Codecs auto-register into the global registry upon instantiation, TranscoderBundles upon finalizing their "build".
 - Thirdly, and most importantly, the fundamental chain of type conversions has been modified to: java object <-fieldinstruction-> java object <-codec-> json object <-gson-> json string. Previous attempts kept hitting a confusing wall because going from json string to java object felt like a HUGE task (because I was trying to parse them by hand). Using gson as a middleman between a java workable json object and raw json string was exactly the piece of the puzzle I needed to tie everything together *functionally*.
 - Codecs explicitly hold information on their dependencies. This isn't used as of now, but perhaps it could be used at some point regarding scheduling/delaying the loading of things. This is by FAR a stretch goal and by no means the purpose of this project itself. This information just allows for that possibility.
 - TranscoderBundles have a unique creation pattern wherein you call a constructor to create one and use a builder pattern to append all information necessary inside of it (fields and child codecs), you never hand-define its own codec, and then you "build" it. This build process will automatically generate the codec for the parent type for you utilizing the fieldinstruction information you've already added, as well as auto-register it in the global registry for others to reference.
 
Layout Notes
 - `Player`
    - `TranscoderBundle<Player>`
    - `Weapon`
       - `TranscoderBundle<Weapon>`
       - `Stats`
          - `TranscoderBundle<Stats>`
          - damage
          - durability
 - `TranscoderBundle<P>`
    - key -> `FieldTranscoder<P,C>`
       - key
       - `FieldInstruction`
       - `childCodec`
    - javaObjectTypeClass
    - constructor
    - codec (this is built automatically during the "build" phase)

### Concluding Thoughts
This architecture technically is tech-demo feature complete. It can successfully take a java object composed of java objects and translate it into json string. It can then take that json string and return it back into a workable java object with recursively parsed child types. Ways to expand functionality is to introduce codecs for types beyond what I'd call an "ObjectCodec". I'd allow for "ArrayCodec/ListCodec/SetCodec" and "MapCodec" as well. The first could handle saved data such as inventory contents, the second perhaps a friend list of some kind or some form of uniquely named saved configurations. It is also apparent that the FieldInstruction information could be moved up a tier into the FieldTranscoder object for conciseness, though this would lose the appeal of transparent separation of concerns from a teaching perspective. The key also doesn't need to be duplicated across TranscoderBundle AND FieldTranscoder. Those are all possible areas of improvement.

---

# V6 - Regroup/Rewrite Java To Json
### Mission Statement
After continuously making spur of the moment fixes for edge cases just to see if functionality conceptually was feasible, that tech debt began to add up. V6 is about rewinding and redeveloping the architecture from the ground up conceptually, making sure all those changes still make sense with regards to the big picture. The goal is to avoid duplicate code, subsystems stepping on the toes of other subsystems, and optimized cohesivity between all working parts. A fully informational architecture reasoning/breakdown header can be found within Main.java with way more information than is present here.

### Notes on Architecture
 - `HierarchicalCodec<T>`: Converts from External Data <-> Internal Data of Type T, composed of children codecs for "passing the torch" when coming across child types
 - `FieldInstruction<P,C>`: Contains instructions to insert/extract C into/from P
 - `FICBundle<P,C>`: Makes a permanent association between the FieldInstructions for a parent-child and the Codec (one-to-one) for the child type.
    - This creates, conceptually, a single object that can handle the full chain: External Data <-> Internal Data <-> Parent Object
 - `FICBundleGroup<P,C>`: Conceptually, the idea is each class would have ONE manager that houses all the FieldInstructions for its fields (which in turn house the necessary children codecs), the codec for itself, and they would all be tied to a "key" which ideally represents the variable name.
    - Its name says "BundleGroup" and is disgustingly redundant I am sorry. The "bundle" means the FieldInstruction and Codec are together, and the "group" is because there are multiple FICBundles per parent object since there is one per variable. This naming will need to become clearer.

### Concluding Thoughts
*Commentary by Aaron from the future: This iteration did exactly what I set out to do in it... reinforce that the architecture works* **by simulating taking an existing Java object and imagining turning it into Json** *. I thought through the information I'd need, I thought through the chain of events and the recursive dependencies, and it all made sense. What I failed to simulate at this point was pretty big... it was going the other direction. This current architecture, coded the way it is, in Java specifically, will have some faults when attempting to go JSON -> Java.*

---

# V5 - Codec Composition
### Mission Statement
The goal of this iteration was functionally - to make Codecs recursively composable - and aesthetically - to rename FieldInstructionCodecBundle to FICBundle because that first name was way too long what the heck.

### Notes on Architecture
The only notable change here is what was a singular "Codec" has now been split into `CodecBase` which holds the fundamental behavior of all Codecs and `HierarchicalCodec` which is the actual implementation which allows for composition.

### Concluding Thoughts
The more I make little changes here to account for this edge case, little changes there to account for this other edge case, the more the code is starting to feel spaghetti. Simultaneously, the more my brain is starting to get scrambled. I have "hacked" together functionality following spur of the moment compulsions/sidequests, but now I should take a step back, re-evaluate the cohesivity of the system as a whole given everything I've changed, and solidify my own understanding of what is actually meant to be happening and if I'm overcomplicating/duplicating things.

---

# V4 - Keys and Consolidation
### Mission Statement
Conception of intended subsystems has taken place, a bit of refinement regarding functionality (changing architecture to account for "edge" case which was actually extremely common and necessary) has also taken place, and V4 dabbles in making it pretty. If my end goal is to have one place I reference to save/load/modify/retrieve objects, I need to merge Codecs and (the newly renamed) FieldInstructions into one place. Simultaneously, another "edge" case was noticed. If I have `Player.health` and `Player.mana` and BOTH are integers *within a Player*, I can't currently distinguish those two `Field<Player, Integer>`. So my overall solution for addressing both these motivations were to combine Codecs and FieldInstructions into a FieldInstructionCodecBundle, and then associate that with the parent object behind a String key representing the variable name (or technically whatever you please). This is done with a KeyedFICBundleManager

### Notes on Architecture:
 - `Codec<T>`: Converts from External Data <-> Internal Data of Type T
 - `FieldInstruction<P,C>`: Contains instructions to insert/extract C into/from P
 - `FieldInstructionCodecBundle<P,C>`: Makes a permanent association between the FieldInstructions for a parent-child and the Codec (one-to-one) for the child type.
    - This creates, conceptually, a single object that can handle the full chain: External Data <-> Internal Data <-> Parent Object
 - `KeyedFICBundleManager<P,C>`: Conceptually, the idea is each class would have ONE manager that houses all the FieldInstructions for its fields (which in turn house the necessary children codecs), the codec for itself, and they would all be tied to a "key" which ideally represents the variable name.
 
 The idea is:
 - Player
    - KeyedFICBundleManager
       - key -> FieldInstructionCodecBundle
          - FieldInstruction (modify/extract java object data)
          - Codec (convert to/from external data types)
 
### Concluding Thoughts
Codecs still aren't functional nor composable via a builder, though the overall system is coming together bit by bit. We'll get there.

---

# V3 - Field Parent/Child Relation
### Mission Statement
After the completion of V2, I realized I had made a crucial error given my end goal. V2 sees codecs acting on a single type AND fields acting on a single type. A codec *can* be associated to a single type because loading that type from external data (assuming it's always json or whatever) will never change. A field *can not* be associated to a single type because you need different ways to load an int for example. If your player has player.health and your items have item.quantity, you can't just have a single field that represents "how to take an integer and put it into a complex java object" since you need to distinguish WHAT complex java object you're putting it into. The goal of this pass was to make a field unique to the parent-child relationship such that player.health and item.quantity are uniquely distinguishable. *(A note from very far future Aaron looking back: In the future we will use "key" as our unique identities and our *`Field`* exist attached TO the parent, so technically I have suspicion we never NEEDED *`Field<Parent, Child>`* and could get by with *`Field<Child>`* as was originally conceived)*

### Notes on Architecture
- `Codec<T>`: Converts from External Data <-> Internal Data of Type T
- `Field<P,C>`: Contains instructions to insert/extract C into/from P
 - This allows `Player.health` and `Item.quantity` to be distinguishable
 
### Concluding Thoughts
`Codec`s still aren't functional yet, but this realization about making `Field`s distinguishable was very crucial. We are slightly transitioning from the "conceive" phase into the "make it functional" phase, at least a little bit.

---

# V2 - Role Conception
### Mission Statement
With comfort with the builder pattern on some level, it was time to think through the actual architecture. What subtasks are we trying to do? Who/what is going to do them? How do all the subroles tie together? At the end of the day we need the capacity to save/load information as well as inserting into/retrieving from java objects.

### Notes on Architecture
- `Codec`: The role of a codec at this point was decided to be translating "external" data <-> "internal" data. I didn't have a hard definition yet of what that looked like.
- `Field`: The role of a field at this point was decided to be translating "internal" data <-> complex object. A complex object would just be a Java object that holds multiple fields of other Java objects.

### Concluding Thoughts
At this point, I'm really only developing conceptual roles. I'm developing a general flow to the system: external data <-> internal data <-> complex object. The code isn't fully functional as codecs currently do nothing and none of this is composable nor type agnostic, it's not integration friendly as to use any of it looks disgusting and isn't comprehensive, but one step at a time. Conceive, get it functional, then get it pretty. We're in the conception steps.

---

# V1 - Builder Pattern Sample
### Mission Statement
I didn't look too in depth into the Hytale code that inspired me to begin this project in the first place, I just scanned the surface really quick in sample code for other functionality entirely. While scanning, I noticed their codec approach was a compositional builder pattern. The very first thing I wanted to do was construct (from scratch) what I believed a builder pattern might look like. Nothing fancy, just "can I improvise a working builder pattern and what might I think of while working on it".

### Concluding Thoughts
The most basic builder would only allow you to keep modifying a single object, but I wanted mine to allow modifications AND appending/concatenating wherein you can build off your current object, incorporate a new object, and then proceed modifying that. It's a bit of a spin on what I'd guess is a trivial builder, but as the few lines of code show, it's not that complicated. Code works. Moving on.
