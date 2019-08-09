package com.bnd.core.reflection;

import com.bnd.core.domain.ClassValueBoundBundle;

/** 
 * @author Peter Banda
 * @since 2012
 */
class GenericReflectionProviderImpl implements GenericReflectionProvider {

	private Class<? extends ReflectionProvider> reflectionProviderClazz;

	GenericReflectionProviderImpl(Class<? extends ReflectionProvider> reflectionProviderClazz) {
		this.reflectionProviderClazz = reflectionProviderClazz;
	}

	@Override
	public <T> T createNewInstance(Class<T> clazz) {
		return createReflectionProvider(clazz).createNewInstance();
	}

	@Override
	public <T> T[] createNewArray(Class<T> clazz, int length) {
		return createReflectionProvider(clazz).createNewArray(length);
	}

	@Override
	public <T> T[] fillNewArray(T object, int size) {
		return createReflectionProvider(object).fillNewArray(object, size);
	}

	@Override
	public <T> T clone(T object) {
		return createReflectionProvider(object).clone(object);
	}

	@Override
	public <T> void copy(T from, T to) {
		createReflectionProvider(from).copy(from, to);		
	}

	@Override
	public <T> T createRandomInstance(Class<T> clazz) {
		return createReflectionProvider(clazz).createRandomInstance();
	}

	@Override
	public <T> void setRandomValues(T object, ClassValueBoundBundle supportedClassBoundBundle) {
		createReflectionProvider(object).setRandomValues(object, supportedClassBoundBundle);		
	}

	@Override
	public <T> String getValuesAsString(T object) {
		return createReflectionProvider(object).getValuesAsString(object);
	}

	@SuppressWarnings("unchecked")
	private <T> ReflectionProvider<T> createReflectionProvider(Class<T> objectClazz) {
		ReflectionProvider<T> reflectionProvider = (ReflectionProvider<T>) ReflectionUtil.createNewInstance(reflectionProviderClazz);
		reflectionProvider.setObjectClass(objectClazz);
		return reflectionProvider;
	}

	@SuppressWarnings("unchecked")
	private <T> ReflectionProvider<T> createReflectionProvider(T object) {
		return createReflectionProvider((Class<T>) object.getClass());
	}
}