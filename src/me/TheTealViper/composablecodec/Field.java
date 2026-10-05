package me.TheTealViper.composablecodec;

public class Field<T> {
	@FunctionalInterface
	static interface Getter<T> {
		T get(Thing thing);
	}
	@FunctionalInterface
	static interface Setter<T> {
		void set(Thing thing, T value);
	}
	
	public Codec<T> codec;
	public Getter<T> getter;
	public Setter<T> setter;
	
	public Field(Codec<T> codec, Getter<T> getter, Setter<T> setter) {
		this.codec = codec;
		this.getter = getter;
		this.setter = setter;
	}
}
