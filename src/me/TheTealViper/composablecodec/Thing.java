package me.TheTealViper.composablecodec;

public class Thing {
	private int value;
	private Thing childThing;
	protected Thing() {
		this.value = 0;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public Thing getChildThing() {
		return childThing;
	}
	public void setChildThing(Thing thing) {
		childThing = thing;
	}
	@Override
	public String toString() {
		String s = value + "";
		if (childThing != null)
			s = s + " : " + childThing.toString();
		return s;
	}
}
