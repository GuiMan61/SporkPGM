package io.sporkpgm.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassUtils {

	public static Object invokeMethod(Object object, Class<?> clazz, String name, Class[] params, Object[] values) {
		try {
			return invokeMethodThrows(object, clazz, name, params, values);
		} catch(Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public static Object invokeMethodThrows(Object object, Class<?> clazz, String name, Class[] params, Object[] values) throws Exception {
		Method method = methodThrows(clazz, name, params);
		return method.invoke(object, values);
	}

	public static Method method(Class<?> clazz, String name, Class[] params) {
		try {
			return methodThrows(clazz, name, params);
		} catch(Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public static Method methodThrows(Class<?> clazz, String name, Class[] params) throws Exception {
		Method method = clazz.getDeclaredMethod(name, params);
		method.setAccessible(true);
		return method;
	}

	public static void setField(Object object, Class<?> clazz, String name, Object value) {
		try {
			setFieldThrows(object, clazz, name, value);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void setFieldThrows(Object object, Class<?> clazz, String name, Object value) throws Exception {
		fieldThrows(clazz, name).set(object, value);
	}

	public static Object getField(Object object, Class<?> clazz, String name) {
		try {
			return getFieldThrows(object, clazz, name);
		} catch(Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public static Object getFieldThrows(Object object, Class<?> clazz, String name) throws Exception {
		return fieldThrows(clazz, name).get(object);
	}

	public static Field field(Class<?> clazz, String name) {
		try {
			return fieldThrows(clazz, name);
		} catch(Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public static Field fieldThrows(Class<?> clazz, String name) throws Exception {
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		return field;
	}

	public static Object construct(Class<?> clazz, Class[] params, Object[] values) {
		try {
			return constructThrows(clazz, params, values);
		} catch(Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public static Object constructThrows(Class<?> clazz, Class[] params, Object[] values) throws Exception {
		Constructor constructor = clazz.getDeclaredConstructor(params);
		constructor.setAccessible(true);
		return constructor.newInstance(values);
	}

	public static String build(Class<?> clazz, Object object) {
		try {
			return buildThrows(clazz, object);
		} catch(IllegalAccessException illegal) {
			illegal.printStackTrace();
			return clazz.getSimpleName() + "{class=" + clazz.getName() + "}";
		}
	}

	private static String buildThrows(Class<?> clazz, Object object) throws IllegalAccessException {
		StringBuilder string = new StringBuilder();
		string.append(clazz.getSimpleName()).append("{");

		boolean previous = false;
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			field.setAccessible(true);
			if(previous) {
				string.append(",");
			}

			string.append(field.getName()).append("=").append("[").append(field.get(object) != null ? field.get(object) : "null").append("]");
			previous = true;
		}

		if(previous) {
			string.append(",");
		}

		string.append("class").append("=").append(clazz.getName());

		string.append("}");
		return string.toString();
	}

	/*
	public static ParsedClass parse(String toString) {
		Map<String, String> map = new HashMap<>();
		int start = toString.indexOf('{') + 1;
		int end = toString.length() - 2;
		String fields = toString.substring(start, end);

		String[] split = new String[]{fields};
		if(fields.contains(",")) {
			split = fields.split(",");
		}

		for(String data : split) {
			String[] fieldValue = data.split("=");
			String field = fieldValue[0];

			List<String> values = Lists.newArrayList(fieldValue);
			values.remove(0);

			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < values.size(); i++) {
				if(i > 0) {
					builder.append("=");
				}

				String value = values.get(i);
				builder.append(value.substring(1, value.length() - 2));
			}

			String value = builder.toString();
			map.put(field, value);
		}

		return new ParsedClass(map);
	}
	*/

	public static Class<?> getClass(String string) {
		try {
			return Class.forName(string);
		} catch(ClassNotFoundException ex) { /* nothing */ }
		return null;
	}

	public static Class<?> getNMS(String string) {
		return getClass("net.minecraft.server." + getVersion() + (string.startsWith(".") ? string : "." + string));
	}

	public static Class<?> getCraft(String string) {
		return getClass("org.bukkit.craftbukkit." + getVersion() + (string.startsWith(".") ? string : "." + string));
	}

	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", "," + "" + "").split(",")[3];
	}

}
