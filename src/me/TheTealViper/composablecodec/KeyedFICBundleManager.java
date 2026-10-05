package me.TheTealViper.composablecodec;

import java.util.HashMap;
import java.util.Map;

public class KeyedFICBundleManager {
	private final Map<String, FieldInstructionCodecBundle<?,?>> db;
	public KeyedFICBundleManager() {
		db = new HashMap<>();
	}
	public void setFICBundle(String key, FieldInstructionCodecBundle<?,?> FICBundle) {
		db.put(key, FICBundle);
	}
	public FieldInstructionCodecBundle<?,?> getFICBundle(String key) {
		return db.get(key);
	}
}
