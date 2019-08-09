package com.bnd.core.converter;

import com.bnd.core.util.ConversionUtil;

/**
 * @author © Peter Banda
 * @since 2012   
 */
class NumberConverter<S extends Number,T extends Number> implements Converter<S,T> {

	final private Class<S> sourceClazz;
	final private Class<T> targetClazz;

	NumberConverter(Class<S> sourceClazz, Class<T> targetClazz) {
		this.sourceClazz = sourceClazz;
		this.targetClazz = targetClazz;
	}

	@Override
	public T convert(final S src) {
		return ConversionUtil.convert(src, targetClazz);
	}

	@Override
	public S reconvert(final T src) {
		return ConversionUtil.convert(src, sourceClazz);
	}
}