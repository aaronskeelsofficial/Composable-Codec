# Composable-Codec
Hytale is a video game that utilizes a fairly automatic and ease-of-integration friendly codec system that uses composition to minimize boilerplate necessary to introduce functionality for data saving/loading/manipulating/extraction. I wanted to try to make my own version from scratch to see how far I'd get. I also wanted to document the evolution of the architecture, literally showing step by step how I iterated to finally arrive wherever I do. Many times, simply explaining a finished system that underwent many iterations loses the clarity of WHY certain decisions were made. I want to preserve the entire chain of thought for myself looking back or others looking to learn by following in my (potentially unprofessional, though real) footsteps. Iterations will either be marked by branches or tags and only the final, fully capable system will exist in the main branch (if I make it that far).

---

# V1
## Mission Statement
I didn't look too in depth into the Hytale code that inspired me to begin this project in the first place, I just scanned the surface really quick in sample code for other functionality entirely. While scanning, I noticed their codec approach was a compositional builder pattern. The very first thing I wanted to do was construct (from scratch) what I believed a builder pattern might look like. Nothing fancy, just "can I improvise a working builder pattern and what might I think of while working on it".

## Concluding Thoughts
The most basic builder would only allow you to keep modifying a single object, but I wanted mine to allow modifications AND appending/concatenating wherein you can build off your current object, incorporate a new object, and then proceed modifying that. It's a bit of a spin on what I'd guess is a trivial builder, but as the few lines of code show, it's not that complicated. Code works. Moving on.