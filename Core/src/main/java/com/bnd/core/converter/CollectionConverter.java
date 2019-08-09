package com.bnd.core.converter;

import java.util.Collection;

import com.bnd.core.reflection.ReflectionUtil;

/**
 * @author © Peter Banda
 * @since 2012   
 */
class CollectionConverter<S,T> implements Converter<Collection<S>, Collection<T>>{

	private final Converter<S,T> itemConverter;

	public CollectionConverter(Converter<S,T> itemConverter) {
		this.itemConverter = itemConverter;
	}

	@Override
	public Collection<T> convert(final Collection<S> srcCollection) {
		if (srcCollection == null) {
			return null;
		}
		Collection<T> convertedItems = ReflectionUtil.createNewInstance(srcCollection.getClass());
		for (S item : srcCollection) {
			convertedItems.add(itemConverter.convert(item));
		}
		return convertedItems;
	}

	@Override
	public Collection<S> reconvert(Collection<T> srcCollection) {
		if (srcCollection == null) {
			return null;
		}
		Collection<S> convertedItems = ReflectionUtil.createNewInstance(srcCollection.getClass());
		for (T item : srcCollection) {
			convertedItems.add(itemConverter.reconvert(item));
		}
		return convertedItems;
	}
}