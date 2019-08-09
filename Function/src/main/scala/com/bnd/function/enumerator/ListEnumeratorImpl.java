package com.bnd.function.enumerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author © Peter Banda
 * @since 2012  
 */
abstract class ListEnumeratorImpl<T> implements ListEnumerator<T> {

	private final boolean allowRepetitions;

	ListEnumeratorImpl(boolean allowRepetitions) {
		this.allowRepetitions = allowRepetitions;
	}

	public boolean isAllowRepetitions() {
		return allowRepetitions;
	}

	@Override
	public Collection<List<T>> enumerate(int listLength) {
		return enumerate(listLength, listLength);
	}

	@Override
	public Collection<List<T>> enumerate(int listLengthFrom, int listLengthTo) {
		Collection<List<T>> lists = new ArrayList<List<T>>();
		for (int listLength = listLengthFrom; listLength <= listLengthTo; listLength++) {
			lists.addAll(isAllowRepetitions() ?
					enumerateWithRepetitions(listLength) : enumerateWithoutRepetitions(listLength));
		}
		return lists;
	}

	protected abstract Collection<List<T>> enumerateWithoutRepetitions(int listLength);

	protected abstract Collection<List<T>> enumerateWithRepetitions(int listLength);
}