package me.TheTealViper.composablecodec;

/*
 * FieldInstruction exists to give instructions to get/set a field of the parent object from internal primitive
 */
@SuppressWarnings("unused")
public class FieldInstruction<PARENTOBJECTTYPE, CHILDDATATYPE> {
	@FunctionalInterface
	public static interface Getter<PARENTOBJECTTYPE, CHILDDATATYPE> {
		CHILDDATATYPE get(PARENTOBJECTTYPE object);
	}
	@FunctionalInterface
	public static interface Setter<PARENTOBJECTTYPE, CHILDDATATYPE> {
		void set(PARENTOBJECTTYPE parent, CHILDDATATYPE data);
	}
	
	//The getter tells us how we interact/retrieve the primitive java object <- complex java object
	private final Getter<PARENTOBJECTTYPE, CHILDDATATYPE> getter;
	//The setter tells us how we interact/insert the primitive java object -> complex java object
	private final Setter<PARENTOBJECTTYPE, CHILDDATATYPE> setter;
	
	public FieldInstruction(Getter<PARENTOBJECTTYPE, CHILDDATATYPE> getter, Setter<PARENTOBJECTTYPE, CHILDDATATYPE> setter) {
		this.getter = getter;
		this.setter = setter;
	}
}
