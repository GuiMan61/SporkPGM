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

	public static <T> T getRandom(List<T> list) {
		if(list.size() == 0) {
			return null;
		}

		int min = 0;
		int max = list.size() - 1;
		return list.get(NumberUtil.getRandom(min, max));
	}

}
