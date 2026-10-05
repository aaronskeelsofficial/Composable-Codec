package me.TheTealViper.composablecodec.ficbundle.fieldinstruction;

import java.util.HashMap;
import java.util.Map;

import me.TheTealViper.composablecodec.ficbundle.FICBundle;

/*
 * A FIC Bundle Group is meant to be a composable builder housing all FIC Bundles for a parent object
 */
public class FICBundleGroup<PARENTOBJECTTYPE> {
	private final Map<String, FICBundle<PARENTOBJECTTYPE,?>> children;
	public FICBundleGroup() {
		children = new HashMap<>();
	}
	public FICBundleGroup<PARENTOBJECTTYPE> set(String key, FICBundle<PARENTOBJECTTYPE, ?> bundle) {
		children.put(key, bundle);
		return this;
	}
	public FICBundleGroup<PARENTOBJECTTYPE> remove(String key) {
		children.remove(key);
		return this;
	}
	public FICBundle<PARENTOBJECTTYPE, ?> get(String key) {
		return children.get(key);
	}

}
