package com.mobilous.ext.plugin.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The helper class that allows you to define a map object containing different data types.
 *
 * Please do not modify this file.
 *
 * @author yanto
 */
public class HeterogeneousMap {
	// Collection
	private Map<String, Object> map;

	// Constructor
	public HeterogeneousMap() { 
		map = new HashMap<String, Object>();
	}

	// Put string entity 
	public void put(String key, String value) {
		map.put(key, value);
	}

	// Put non-string entity
	public <T> void put(String key, T value, Class<T> type) {
		map.put(key, value);		
	}

	// Get string entity
	public String get(String key){
		try {
			return map.get(key).toString();
		} catch(Exception e) {}
		return null;
	}

	// Get non-string entity 
	public <T> T get(String key, Class<T> type) {
		try {
			return type.cast(map.get(key));
		} catch(Exception e) {}
		return null;
	}

	// Key set
	public Set<String> keySet() {
		return map.keySet();
	}
}
