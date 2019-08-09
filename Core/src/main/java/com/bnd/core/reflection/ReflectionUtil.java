package com.bnd.core.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author © Peter Banda
 * @since 2008
 */
public class ReflectionUtil {

	public static GenericReflectionProvider createGenericReflectionProvider(Class<? extends ReflectionProvider> reflectionProviderClazz) {
		return new GenericReflectionProviderImpl(reflectionProviderClazz);
	}

	/**
	 * Creates a new instance of given class
	 * 
	 * @param <O> The handled Class type
	 * @param clazz The handled Class
	 * @return A fresh new instance of given Class
	 */
	public static <O> O createNewInstance(Class<O> clazz) {
		String newInstanceProblemMessage = "The class " + clazz.getName() + " is expected to have public default (null) constructor.";
		try {
			return clazz.newInstance();
		} catch (InstantiationException pException) {
			throw new RuntimeException(newInstanceProblemMessage);
		} catch (IllegalAccessException pException) {
			throw new RuntimeException(newInstanceProblemMessage);
		}
	}

	/**
	 * Gets the class from type parameter.
	 * 
	 * @param clazz
	 * @param paramIndex
	 * @return
	 */
	public static Class<?> getParamClass(Class<?> clazz, int paramIndex) {
		Type type = clazz.getGenericSuperclass();
		ParameterizedType paramType = (ParameterizedType) type;
		Type subParamType = paramType.getActualTypeArguments()[paramIndex];
		if (subParamType instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) subParamType).getRawType();
		} else if (subParamType instanceof Class<?>) {
			return (Class<?>) subParamType; 
		}
		return null;
	}

	/**
	 * Creates a new array of given type
	 * 
	 * @param <O> The handled Class type
	 * @param type The handled type
	 * @return A fresh new array of given type
	 */
	public static <O> O[] createNewArray(Class<O> type, int length) {
		return (O[]) Array.newInstance(type, length);
	}

	/**
	 * Creates a new array of given type
	 * 
	 * @param <O> The handled Class type
	 * @param type The handled type
	 * @return A fresh new array of given type
	 */
	public static <O> O[] createNewArray(O type, int length) {
		return createNewArray((Class<O>) type.getClass(), length);
	}

	/**
	 * Fills new array with given object 
	 * 
	 * @param object
	 * @param size
	 * @return
	 */
	@Deprecated
	public static <O> O[] fillNewArray(O object, int size) {
		O[] array = createNewArray(object, size);
		Arrays.fill(array, object);
		return array;
	}
}