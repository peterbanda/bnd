package com.bnd.core.converter;

import java.util.Collection;

import com.bnd.core.BndRuntimeException;
import com.bnd.core.reflection.ReflectionUtil;

/**
 * @author © Peter Banda
 * @since 2012   
 */
public class ConverterFactory {

	private final static ConverterFactory instance = new ConverterFactory();

	private ConverterFactory() {
		// no-op
	}

	public static ConverterFactory getInstance() {
		return instance;
	}

	public <S,T> Converter<Collection<S>,Collection<T>> createCollectionConverter(final Class<S> sourceClazz, final Class<T> targetClazz) {
		final Converter<S,T> itemConverter = createConverter(sourceClazz, targetClazz);
		return new CollectionConverter<S, T>(itemConverter);
	}

	public <S,T> Converter<Collection<S>,Collection<T>> createCollectionConverter(final Converter<S,T> itemConverter) {
		return new CollectionConverter<S, T>(itemConverter);
	}

	public <S,T> Converter<S,T> createConverter(final Class<S> sourceClazz, final Class<T> targetClazz) {
		Converter<S,T> converter = null;
		if (Number.class.isAssignableFrom(targetClazz)) {
			if (Number.class.isAssignableFrom(sourceClazz)) {
				converter = new NumberConverter((Class<Number>) sourceClazz, (Class<Number>) targetClazz);
			} else if (Boolean.class.isAssignableFrom(sourceClazz)) {
				converter = new BooleanNumberConverter((Class<Number>) targetClazz);
			} else if (String.class.isAssignableFrom(sourceClazz)) {
				converter = new StringNumberConverter((Class<Number>) targetClazz);
			}
		}
		if (converter == null) {
			throw new BndRuntimeException("A converter for the source class '" + sourceClazz + "' and the target clazz '" + targetClazz + "' is not defined.");
		}
		return converter;
	}

	public <S,T> Converter<S,T> createConverter(
		final Class<S> sourceClazz,
		final Class<T> targetClazz,
		final Mapper<S,T> mapper
	) {
		return new Converter<S,T>() {

			@Override
			public T convert(S src) {
				final T target = ReflectionUtil.createNewInstance(targetClazz);
				mapper.map(src, target);
				return target;
			}

			@Override
			public S reconvert(T src) {
				final S target = ReflectionUtil.createNewInstance(sourceClazz);
				mapper.remap(src, target);
				return target;
			}
		};
	}

	public static final void main(String args[]) {
//		Converter<Integer,Double> numericConverter = ConverterFactory.getInstance().createConverter(Integer.class, Double.class);
//		Double converted1 = numericConverter.convert(101);
//		Integer converted2 = numericConverter.reconvert(12.3);
//		System.out.println("Converted 1: " + converted1);
//		System.out.println("Converted 2: " + converted2);
		Converter<Boolean,Double> numericConverter = ConverterFactory.getInstance().createConverter(Boolean.class, Double.class);
		Double converted1 = numericConverter.convert(Boolean.TRUE);
		Double converted2 = numericConverter.convert(false);
		Boolean converted3 = numericConverter.reconvert(1d);
		Boolean converted4 = numericConverter.reconvert(0d);
		Boolean converted5 = numericConverter.reconvert(0.5d);

		System.out.println("True = " + converted1);
		System.out.println("false = " + converted2);
		System.out.println("1d = " + converted3);
		System.out.println("0d = " + converted4);
		System.out.println("0.5d = " + converted5);
	}
}