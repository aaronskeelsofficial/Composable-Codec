package me.TheTealViper.composablecodec;

@SuppressWarnings("unused")
public class Field<PARENTOBJECTTYPE, CHILDDATATYPE> {
	@FunctionalInterface
	public static interface Getter<PARENTOBJECTTYPE, CHILDDATATYPE> {
		CHILDDATATYPE get(PARENTOBJECTTYPE object);
	}
	@FunctionalInterface
	public static interface Setter<PARENTOBJECTTYPE, CHILDDATATYPE> {
		void set(PARENTOBJECTTYPE parent, CHILDDATATYPE data);
	}
	
	private final Codec<CHILDDATATYPE> codec;
	private final Getter<PARENTOBJECTTYPE, CHILDDATATYPE> getter;
	private final Setter<PARENTOBJECTTYPE, CHILDDATATYPE> setter;
	
	public Field(Codec<CHILDDATATYPE> codec, Getter<PARENTOBJECTTYPE, CHILDDATATYPE> getter, Setter<PARENTOBJECTTYPE, CHILDDATATYPE> setter) {
		this.codec = codec;
		this.getter = getter;
		this.setter = setter;
	}
}
