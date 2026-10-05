# Composable-Codec
Hytale is a video game that utilizes a fairly automatic and ease-of-integration friendly codec system that uses composition to minimize boilerplate necessary to introduce functionality for data saving/loading/manipulating/extraction. I wanted to try to make my own version from scratch to see how far I'd get. I also wanted to document the evolution of the architecture, literally showing step by step how I iterated to finally arrive wherever I do. Many times, simply explaining a finished system that underwent many iterations loses the clarity of WHY certain decisions were made. I want to preserve the entire chain of thought for myself looking back or others looking to learn by following in my (potentially unprofessional, though real) footsteps. Iterations will either be marked by branches or tags and only the final, fully capable system will exist in the main branch (if I make it that far).

---

# V2
## Mission Statement
With comfort with the builder pattern on some level, it was time to think through the actual architecture. What subtasks are we trying to do? Who/what is going to do them? How do all the subroles tie together? At the end of the day we need the capacity to save/load information as well as inserting into/retrieving from java objects.

## Notes on Architecture
- Codecs: The role of a codec at this point was decided to be translating "external" data <-> "internal" data. I didn't have a hard definition yet of what that looked like.
- Field: The role of a field at this point was decided to be translating "internal" data <-> complex object. A complex object would just be a Java object that holds multiple fields of other Java objects.

## Concluding Thoughts
At this point, I'm really only developing conceptual roles. I'm developing a general flow to the system: external data <-> internal data <-> complex object. The code isn't fully functional as codecs currently do nothing and none of this is composable nor type agnostic, it's not integration friendly as to use any of it looks disgusting and isn't comprehensive, but one step at a time. Conceive, get it functional, then get it pretty. We're in the conception steps.

---

# V1
## Mission Statement
I didn't look too in depth into the Hytale code that inspired me to begin this project in the first place, I just scanned the surface really quick in sample code for other functionality entirely. While scanning, I noticed their codec approach was a compositional builder pattern. The very first thing I wanted to do was construct (from scratch) what I believed a builder pattern might look like. Nothing fancy, just "can I improvise a working builder pattern and what might I think of while working on it".

## Concluding Thoughts
The most basic builder would only allow you to keep modifying a single object, but I wanted mine to allow modifications AND appending/concatenating wherein you can build off your current object, incorporate a new object, and then proceed modifying that. It's a bit of a spin on what I'd guess is a trivial builder, but as the few lines of code show, it's not that complicated. Code works. Moving on.