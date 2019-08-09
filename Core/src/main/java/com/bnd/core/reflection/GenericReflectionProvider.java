package com.bnd.core.reflection;

import com.bnd.core.domain.ClassValueBoundBundle;

/**
 * GenericReflectionProvider is usually less effective than class-specific ReflectionProvider.
 *  
 * @author Peter Banda
 * @since 2012
 */
public interface GenericReflectionProvider {

	<T> T createNewInstance(Class<T> clazz);

	<T> T[] createNewArray(Class<T> clazz, int length);

	<T> T[] fillNewArray(T object, int size);

	<T> T clone(T object);

	<T> void copy(T from, T to);

	<T> T createRandomInstance(Class<T> clazz);

	<T> void setRandomValues(T object, ClassValueBoundBundle supportedClassBoundBundle);

	<T> String getValuesAsString(T object);
}