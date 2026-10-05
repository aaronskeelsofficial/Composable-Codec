package me.TheTealViper.composablecodec;

import java.util.HashMap;
import java.util.Map;

public class KeyedFICBundleManager {
	private final Map<String, FICBundle<?,?>> db;
	public KeyedFICBundleManager() {
		db = new HashMap<>();
	}
	public void setFICBundle(String key, FICBundle<?,?> FICBundle) {
		db.put(key, FICBundle);
	}
	public FICBundle<?,?> getFICBundle(String key) {
		return db.get(key);
	}
}
