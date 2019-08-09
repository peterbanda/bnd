package com.bnd.core.converter;

import com.bnd.core.util.ConversionUtil;

/**
 * @author © Peter Banda
 * @since 2012   
 */
class StringNumberConverter<T extends Number> implements Converter<String, T> {

	final private Class<T> clazz;

	StringNumberConverter(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T convert(String src) {
		if (src == null) {
			return null;
		}
		return ConversionUtil.convert(clazz, src);
	}

	@Override
	public String reconvert(T src) {
		if (src == null) {
			return null;
		}
		return src.toString();
	}
}