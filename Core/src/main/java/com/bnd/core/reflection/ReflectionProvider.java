package com.bnd.core.reflection;

import com.bnd.core.domain.ClassValueBoundBundle;

/**
 * An interface spefifying common reflection function such as <code>createNewInstance</code>, <code>createNewArray</code>, etc.
 * 
 * @author © Peter Banda
 * @since 2012
 */
public interface ReflectionProvider<T> {

	/**
	 * Gets the associated class.
	 * 
	 * @return
	 */
	Class<? extends T> getObjectClass();

	/**
	 * Sets the object class.
	 * 
	 * @param objectClass
	 */
	void setObjectClass(Class<? extends T> objectClass);

	/**
	 * Creates a new instance of object.
	 * 
	 * @return
	 */
	T createNewInstance();

	/**
	 * Creates a new typed array of given length.
	 * 
	 * @param length
	 * @return
	 */
	T[] createNewArray(int length);

	/**
	 * Creates a new typed array of given length and fills it with fiven object
	 * 
	 * @param object
	 * @param size
	 * @return 
	 */
	T[] fillNewArray(T object, int size);

	/**
	 * Creates a copy of given object.
	 * 
	 * @param object
	 * @return
	 */
	T clone(T object);

	/**
	 * Copies property values from the first object (<code>from</code>) to the second one (<code>to</code>).
	 * 
	 * @param from
	 * @param to 
	 * @return
	 */
	void copy(T from, T to);

	/**
	 * Creates an instance with random values.
	 * 
	 * @return
	 */
	T createRandomInstance();

	/**
	 * Sets random values to given object.
	 * 
	 * @param object
	 * @param supportedClassBoundBundle
	 * @return
	 */
	void setRandomValues(T object, ClassValueBoundBundle supportedClassBoundBundle);

	/**
	 * Gets the property values in a String format.
	 * 
	 * @param object
	 * @return 
	 */
	String getValuesAsString(T object);
}