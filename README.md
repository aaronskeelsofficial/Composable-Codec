# Composable-Codec
Hytale is a video game that utilizes a fairly automatic and ease-of-integration friendly codec system that uses composition to minimize boilerplate necessary to introduce functionality for data saving/loading/manipulating/extraction. I wanted to try to make my own version from scratch to see how far I'd get. I also wanted to document the evolution of the architecture, literally showing step by step how I iterated to finally arrive wherever I do. Many times, simply explaining a finished system that underwent many iterations loses the clarity of WHY certain decisions were made. I want to preserve the entire chain of thought for myself looking back or others looking to learn by following in my (potentially unprofessional, though real) footsteps. Iterations will either be marked by branches or tags and only the final, fully capable system will exist in the main branch (if I make it that far).

---

# V6
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

# V5
### Mission Statement
The goal of this iteration was functionally - to make Codecs recursively composable - and aesthetically - to rename FieldInstructionCodecBundle to FICBundle because that first name was way too long what the heck.

### Notes on Architecture
The only notable change here is what was a singular "Codec" has now been split into `CodecBase` which holds the fundamental behavior of all Codecs and `HierarchicalCodec` which is the actual implementation which allows for composition.

### Concluding Thoughts
The more I make little changes here to account for this edge case, little changes there to account for this other edge case, the more the code is starting to feel spaghetti. Simultaneously, the more my brain is starting to get scrambled. I have "hacked" together functionality following spur of the moment compulsions/sidequests, but now I should take a step back, re-evaluate the cohesivity of the system as a whole given everything I've changed, and solidify my own understanding of what is actually meant to be happening and if I'm overcomplicating/duplicating things.

---

# V4
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

# V3
### Mission Statement
After the completion of V2, I realized I had made a crucial error given my end goal. V2 sees codecs acting on a single type AND fields acting on a single type. A codec *can* be associated to a single type because loading that type from external data (assuming it's always json or whatever) will never change. A field *can not* be associated to a single type because you need different ways to load an int for example. If your player has player.health and your items have item.quantity, you can't just have a single field that represents "how to take an integer and put it into a complex java object" since you need to distinguish WHAT complex java object you're putting it into. The goal of this pass was to make a field unique to the parent-child relationship such that player.health and item.quantity are uniquely distinguishable. *(A note from very far future Aaron looking back: In the future we will use "key" as our unique identities and our *`Field`* exist attached TO the parent, so technically I have suspicion we never NEEDED *`Field<Parent, Child>`* and could get by with *`Field<Child>`* as was originally conceived)*

### Notes on Architecture
- `Codec<T>`: Converts from External Data <-> Internal Data of Type T
- `Field<P,C>`: Contains instructions to insert/extract C into/from P
 - This allows `Player.health` and `Item.quantity` to be distinguishable
 
### Concluding Thoughts
`Codec`s still aren't functional yet, but this realization about making `Field`s distinguishable was very crucial. We are slightly transitioning from the "conceive" phase into the "make it functional" phase, at least a little bit.

---

# V2
### Mission Statement
With comfort with the builder pattern on some level, it was time to think through the actual architecture. What subtasks are we trying to do? Who/what is going to do them? How do all the subroles tie together? At the end of the day we need the capacity to save/load information as well as inserting into/retrieving from java objects.

### Notes on Architecture
- `Codec`: The role of a codec at this point was decided to be translating "external" data <-> "internal" data. I didn't have a hard definition yet of what that looked like.
- `Field`: The role of a field at this point was decided to be translating "internal" data <-> complex object. A complex object would just be a Java object that holds multiple fields of other Java objects.

### Concluding Thoughts
At this point, I'm really only developing conceptual roles. I'm developing a general flow to the system: external data <-> internal data <-> complex object. The code isn't fully functional as codecs currently do nothing and none of this is composable nor type agnostic, it's not integration friendly as to use any of it looks disgusting and isn't comprehensive, but one step at a time. Conceive, get it functional, then get it pretty. We're in the conception steps.

---

# V1
### Mission Statement
I didn't look too in depth into the Hytale code that inspired me to begin this project in the first place, I just scanned the surface really quick in sample code for other functionality entirely. While scanning, I noticed their codec approach was a compositional builder pattern. The very first thing I wanted to do was construct (from scratch) what I believed a builder pattern might look like. Nothing fancy, just "can I improvise a working builder pattern and what might I think of while working on it".

### Concluding Thoughts
The most basic builder would only allow you to keep modifying a single object, but I wanted mine to allow modifications AND appending/concatenating wherein you can build off your current object, incorporate a new object, and then proceed modifying that. It's a bit of a spin on what I'd guess is a trivial builder, but as the few lines of code show, it's not that complicated. Code works. Moving on.