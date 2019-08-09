package com.bnd.core.converter;

import com.bnd.core.util.ConversionUtil;

/**
 * @author © Peter Banda
 * @since 2012   
 */
class BooleanNumberConverter<T extends Number> implements Converter<Boolean, T> {

	final private Class<T> clazz;

	BooleanNumberConverter(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T convert(Boolean src) {
		if (src == null) {
			return null;
		}
		if (src.booleanValue()) {
			return ConversionUtil.convertSimpleInt(1, clazz);
		}
		return ConversionUtil.convertSimpleInt(0, clazz);
	}

	@Override
	public Boolean reconvert(T src) {
		if (src == null) {
			return null;
		}
		return (src.intValue() == 1d);
	}
}