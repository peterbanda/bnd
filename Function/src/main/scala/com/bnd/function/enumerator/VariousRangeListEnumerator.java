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
abstract class VariousRangeListEnumerator<T> extends ListEnumeratorImpl<T> {

	public VariousRangeListEnumerator(
		boolean allowRepetitions
	) {
		super(allowRepetitions);
	}

	protected abstract T inc(T element);

	protected abstract T getRangeFrom(int i);

	protected abstract T getRangeTo(int i);

	protected T[] createNewArray(int listLength) {
		T[] newArray = ReflectionUtil.createNewArray(getRangeFrom(0), listLength);
		for (int i = 0; i < listLength; i++) {
			newArray[i] = getRangeFrom(i);
		}
		return newArray;
	}

	@Override
	protected Collection<List<T>> enumerateWithoutRepetitions(int listLength) {
		Collection<List<T>> lists = new ArrayList<List<T>>();
		int handledIndex = listLength - 1;
		T[] list = createNewArray(listLength);
		list[0] = getRangeFrom(0);
		for (int i = 1; i < listLength; i++) {
			list[i] = inc(list[i - 1]);
			if (list[i] == getRangeTo(i) && i < listLength - 1) {
				return lists;
			}
		}
		while (true) {
			lists.add(Arrays.asList(list.clone()));
			while ((handledIndex == listLength - 1 && list[handledIndex] == getRangeTo(handledIndex))
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
			list[i] = getRangeFrom(i);
		}
		while (true) {
			lists.add(Arrays.asList(list.clone()));
			while (handledIndex >= 0 && list[handledIndex] == getRangeTo(handledIndex)) {
				handledIndex--;
			}
			if (handledIndex < 0) {
				break;
			}
			list[handledIndex] = inc(list[handledIndex]);
			for (; handledIndex < listLength - 1; handledIndex++) {
				list[handledIndex + 1] = getRangeFrom(handledIndex + 1);
			}
		}
		return lists;
	}
}