package cek.ruins;

import java.util.concurrent.ConcurrentHashMap;

public class ApplicationMap {
	private static ConcurrentHashMap<String, Object> applicationMap = new ConcurrentHashMap<String, Object>(10, 0.9f, 5);

	public static Object get(String key) {
		return applicationMap.get(key);
	}

	public static void set(String key, Object value) {
		ApplicationMap.applicationMap.put(key, value);
	}


}
