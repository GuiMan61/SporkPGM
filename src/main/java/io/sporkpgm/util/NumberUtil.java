/*
 * Copyright 2013 Maxim Salikhov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sporkpgm.util;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class NumberUtil {

	private static char[] allowed;

	public static Integer parseInteger(String s) {
		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException ex) {
			Log.severe("Could not parse string '" + s + "' to integer!");
			return null;
		}
	}

	public static int parseInteger(String s, int def) {
		Integer result = parseInteger(s);
		return result != null ? result : def;
	}

	public static Double parseDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch(NumberFormatException ex) {
			Log.severe("Could not parse string '" + s + "' to double!");
			return null;
		}
	}

	public static double parseDouble(String s, double def) {
		Double result = parseDouble(s);
		return result != null ? result : def;
	}

	public static Float parseFloat(String s) {
		try {
			return Float.parseFloat(s);
		} catch(NumberFormatException ex) {
			Log.severe("Could not parse string '" + s + "' to float!");
			return null;
		}
	}

	public static float parseFloat(String s, float def) {
		Float result = parseFloat(s);
		return result != null ? result : def;
	}

	public static Long parseLong(String s) {
		try {
			return Long.parseLong(s);
		} catch(NumberFormatException ex) {
			Log.severe("Could not parse string '" + s + "' to float!");
			return null;
		}
	}

	public static long parseLong(String s, long def) {
		Long result = parseLong(s);
		return result != null ? result : def;
	}

	public static int getRandom(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

	public static boolean randomBoolean() {
		return new Random().nextBoolean();
	}

	public static double getLowest(List<Double> doubles) {
		double lowest = doubles.get(0);
		for(int i = 1; i < doubles.size(); i++) {
			double value = doubles.get(i);
			if(lowest > value) {
				lowest = value;
			}
		}
		return lowest;
	}

	public static List<Object> getLowest(Map<Object, Double> map) {
		Object[] array = map.keySet().toArray();

		List<Object> objects = Lists.newArrayList(new Object[]{array[0]});
		double lowest = map.get(objects.get(0));
		for(int i = 1; i < map.size(); i++) {
			Object key = array[i];
			double value = map.get(key);
			if(lowest > value) {
				objects = Lists.newArrayList(new Object[]{key});
				lowest = value;
			} else if(lowest == value) {
				objects.add(key);
			}
		}

		return objects;
	}

}
