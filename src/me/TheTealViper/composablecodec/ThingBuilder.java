package me.TheTealViper.composablecodec;

public class ThingBuilder {
	private Thing headThing, currentThing;
	
	public static ThingBuilder create() {
		ThingBuilder builder = new ThingBuilder();
		builder.headThing = builder.currentThing = new Thing();
		return builder;
	}
	private ThingBuilder() {
	}
	
	public ThingBuilder add(int i) {
		currentThing.setValue(currentThing.getValue()+i);
		return this;
	}
	public ThingBuilder append() {
		Thing thing = new Thing();
		currentThing.setChildThing(thing);
		currentThing = thing;
		return this;
	}
	public Thing build() {
		return headThing;
	}
}
