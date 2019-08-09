package com.bnd.core.reflection;

import com.bnd.core.BndRuntimeException;
import com.bnd.core.domain.ClassValueBoundBundle;
import com.bnd.core.domain.ValueBound;
import com.bnd.core.reflection.GenericReflectionProvider;
import com.bnd.core.reflection.GenericReflectionProviderImpl;
import com.bnd.core.reflection.ReflectionProvider;
import com.bnd.core.reflection.ReflectionUtil;
import com.bnd.core.util.RandomUtil;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * A Spring bean based implementation of <code>ReflectionProvider</code>
 * 
 * @author © Peter Banda
 * @since 2012
 */
public class SpringReflectionProviderImpl<T> implements ReflectionProvider<T> {

	private Class<? extends T> clazz;
	private final GenericReflectionProvider genericReflectionProvider;

	public SpringReflectionProviderImpl() {
		this.genericReflectionProvider = new GenericReflectionProviderImpl(SpringReflectionProviderImpl.class);
	}

    public SpringReflectionProviderImpl(Class<? extends T> clazz) {
		this();
		this.clazz = clazz;
	}

	@Override
	public T createNewInstance() {
		return (T) BeanUtils.instantiate(clazz);
	}

	@Override
	public T[] createNewArray(int length) {
		return ReflectionUtil.createNewArray(clazz, length);
	}

	@Override
	public T[] fillNewArray(T object, int size) {
		T[] array = ReflectionUtil.createNewArray(clazz, size);
		Arrays.fill(array, object);
		return array;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T clone(T object) {
		if (object == null) {
			return null;
		}
		T copiedObject = null;
		if (object.getClass().isArray()) {
			copiedObject = (T) ((Object[]) object).clone();;			
		} else {
			copiedObject = (T) createNewInstance();
			copy(object, copiedObject);
		}
		return copiedObject;
	}

	@Override
	public void copy(T from, T to) {
		if (from == null) {
			return;
		}
		BeanUtils.copyProperties(from, to);
	}

	@Override
	public T createRandomInstance() {
		T newInstance = createNewInstance();
		ClassValueBoundBundle supportedClassBoundBundle = ClassValueBoundBundle.getDefaultInstance();
		setRandomValues(newInstance, supportedClassBoundBundle);
		return newInstance;
	}

	@Override
	public void setRandomValues(T object, ClassValueBoundBundle supportedClassBoundBundle) {
		for (final PropertyDescriptor property : BeanUtils.getPropertyDescriptors(object.getClass())) {
			final Class<?> propertyClazz = property.getPropertyType();
			final Object value = createRandomValueIfSupported(propertyClazz, supportedClassBoundBundle);
			if (value != null) {
				try {
					Method writeMethod = property.getWriteMethod();
					if (writeMethod != null) {
						if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
							writeMethod.setAccessible(true);
						}
						writeMethod.invoke(object, value);
					}
				} catch (IllegalArgumentException e) {
					throw new BndRuntimeException("Problem occured while setting a value '" + value + "' to the property '" + property.getName() + "'.", e);
				} catch (IllegalAccessException e) {
					throw new BndRuntimeException("Problem occured while setting a value '" + value + "' to the property '" + property.getName() + "'.", e);
				} catch (InvocationTargetException e) {
					throw new BndRuntimeException("Problem occured while setting a value '" + value + "' to the property '" + property.getName() + "'.", e);
				}
			}
		}
	}

	@Override
	public String getValuesAsString(T object) {
		StringBuilder sb = new StringBuilder();
		for (final PropertyDescriptor property : BeanUtils.getPropertyDescriptors(object.getClass())) {
			try {
				Method readMethod = property.getReadMethod();
				if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
					readMethod.setAccessible(true);
				}
				Object value = readMethod.invoke(object);

				sb.append(property.getName() + "  :");
				sb.append(value);
				sb.append("\n");
			} catch (IllegalArgumentException e) {
				throw new BndRuntimeException("Problem occured while reading a value from the property '" + property.getName() + "'.", e);
			} catch (IllegalAccessException e) {
				throw new BndRuntimeException("Problem occured while reading a value from the property '" + property.getName() + "'.", e);
			} catch (InvocationTargetException e) {
				throw new BndRuntimeException("Problem occured while reading a value from the property '" + property.getName() + "'.", e);
			}
		}
		return sb.toString();
	}

	private <E> E createRandomValueIfSupported(
		Class<E> propertyClazz,
		ClassValueBoundBundle supportedClassBoundBundle
	) {
		E randomValue = null;
		if (supportedClassBoundBundle.contains(propertyClazz)) {
			ValueBound<E> bound = supportedClassBoundBundle.getBound(propertyClazz);
			randomValue = RandomUtil.next(propertyClazz, bound);
		}
		return randomValue;
	}

	@Override
	public Class<? extends T> getObjectClass() {
		return clazz;
	}

	@Override
	public void setObjectClass(Class<? extends T> clazz) {
		this.clazz = clazz;
	}

	protected GenericReflectionProvider getGenericProvider() {
		return genericReflectionProvider;
	}
}