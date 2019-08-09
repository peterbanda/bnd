package com.bnd.function.enumerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bnd.core.reflection.ReflectionUtil;

/**
 * @author © Peter Banda
 * @since 2012  
 */
class EnumListEnumerator<T> implements ListEnumerator<T> {

	private final ListEnumerator<Integer> indexListEnumerator;
	private final T[] values;

	EnumListEnumerator(
		ListEnumerator<Integer> indexListEnumerator,
		List<T> values
	) {
		this(indexListEnumerator, values.toArray(ReflectionUtil.createNewArray(values.get(0), 0)));
	}

	EnumListEnumerator(
		ListEnumerator<Integer> indexListEnumerator,
		T[] values
	) {
		this.indexListEnumerator = indexListEnumerator;
		this.values = values;
	}

	@Override
	public Collection<List<T>> enumerate(int listLength) {
		return getSelectedValues(indexListEnumerator.enumerate(listLength));
	}

	@Override
	public Collection<List<T>> enumerate(int listLengthFrom, int listLengthTo) {
		return getSelectedValues(indexListEnumerator.enumerate(listLengthFrom, listLengthTo));
	}

	private Collection<List<T>> getSelectedValues(Collection<List<Integer>> indexGroups) {
		Collection<List<T>> listEnumerations = new ArrayList<List<T>>();
		for (List<Integer> indeces : indexGroups) {
			listEnumerations.add(getSelectedValues(indeces));
		}
		return listEnumerations;
	}

	private List<T> getSelectedValues(List<Integer> indeces) {
		List<T> selectedValues = new ArrayList<T>();
		for (Integer index : indeces) {
			selectedValues.add(values[index]);
		}
		return selectedValues;
	}
}