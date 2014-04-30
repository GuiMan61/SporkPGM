package io.sporkpgm.util;

import java.lang.reflect.Array;
import java.util.List;

public class OtherUtil {

	public static <T> T[] toArray(Class<T> clazz, List<T> list) {
		T[] array = (T[]) Array.newInstance(clazz, list.size());
		for(int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

}
