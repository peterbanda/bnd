package com.bnd.function.enumerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.bnd.core.reflection.ReflectionUtil;

/**
 * @author © Peter Banda
 * @since 2012  
 */
abstract class RangeListEnumerator<T> extends ListEnumeratorImpl<T> {

	private final T rangeFrom;
	private final T rangeTo;

	public RangeListEnumerator(
		boolean allowRepetitions,
		T rangeFrom,
		T rangeTo
	) {
		super(allowRepetitions);
		this.rangeFrom = rangeFrom;
		this.rangeTo = rangeTo;
	}

	protected abstract T inc(T element);

	protected T[] createNewArray(int listLength) {
		return ReflectionUtil.createNewArray(rangeFrom, listLength);
	}

	@Override
	protected Collection<List<T>> enumerateWithoutRepetitions(int listLength) {
		Collection<List<T>> lists = new ArrayList<List<T>>();
		int handledIndex = listLength - 1;
		T[] list = createNewArray(listLength);
		list[0] = rangeFrom;
		for (int i = 1; i < listLength; i++) {
			list[i] = inc(list[i - 1]);
			if (list[i] == rangeTo && i < listLength - 1) {
				return lists;
			}
		}
		while (true) {
			lists.add(Arrays.asList(list.clone()));
			while ((handledIndex == listLength - 1 && list[handledIndex] == rangeTo)
					|| (handledIndex < listLength - 1 && handledIndex >= 0 && inc(list[handledIndex]) == list[handledIndex + 1])) {				
				handledIndex--;
			}
			if (handledIndex < 0) {
				break;
			}
			list[handledIndex] = inc(list[handledIndex]);
			for (; handledIndex < listLength - 1; handledIndex++) {
				list[handledIndex + 1] = inc(list[handledIndex]);
			}
		}
		return lists;
	}

	protected Collection<List<T>> enumerateWithRepetitions(int listLength) {
		Collection<List<T>> lists = new ArrayList<List<T>>();
		int handledIndex = listLength - 1;
		T[] list = createNewArray(listLength);
		for (int i = 0; i < listLength; i++) {
			list[i] = rangeFrom;
		}
		while (true) {
			lists.add(Arrays.asList(list.clone()));
			while (handledIndex >= 0 && list[handledIndex] == rangeTo) {
				handledIndex--;
			}
			if (handledIndex < 0) {
				break;
			}
			list[handledIndex] = inc(list[handledIndex]);
			for (; handledIndex < listLength - 1; handledIndex++) {
				list[handledIndex + 1] = rangeFrom;
			}
		}
		return lists;
	}
}