package com.bnd.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.bnd.core.util.ObjectUtil;

public class HashMultiSet<E> implements MultiSet<E> {

	class MultipleItem<T> {
		protected T item;
		protected int multiplicity;

		public MultipleItem(T item) {
			this.item = item;
			this.multiplicity = 1;
		}

		public MultipleItem(T item, int multiplicity) {
			this.item = item;
			this.multiplicity = multiplicity;
		}

		@Override
		public int hashCode() {
			return item.hashCode();
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}
		    if (object == null || !(object instanceof MultipleItem)) {
		        return false;
		    }
		    return ObjectUtil.areObjectsEqual(item, ((MultipleItem) object).item);
		}
	}

	private Map<E, MultipleItem<E>> itemMultiplicityMap = new HashMap<E, MultipleItem<E>>();

	@Override
	public boolean add(E element, int multiplicity) {
		return add(new MultipleItem<E>(element, multiplicity));
	}

	@Override
	public boolean add(E element) {
		return add(new MultipleItem<E>(element));
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean result = true;
		for (E element : c) {
			result = result && add(element);
		}
		return result;
	}

	private boolean add(MultipleItem<E> multipleItem) {
		MultipleItem<E> existingMultipleItem = itemMultiplicityMap.get(multipleItem.item);
		if (existingMultipleItem == null) {
			itemMultiplicityMap.put(multipleItem.item, multipleItem);
		} else {
			existingMultipleItem.multiplicity += multipleItem.multiplicity;			
		}
		return true;
	}

	@Override
	public int getMultiplicity(E element) {
		MultipleItem<E> existingMultipleItem = itemMultiplicityMap.get(element);
		if (existingMultipleItem != null) {
			return existingMultipleItem.multiplicity;
		}
		return 0;
	}

	@Override
	public boolean remove(E element, int multiplicity) {
		return remove(new MultipleItem<E>(element, multiplicity));
	}

	@Override
	public boolean remove(Object object) {
		return remove(new MultipleItem<E>((E) object));
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = true;
		for (Object element : c) {
			result = result && remove(element);
		}
		return result;
	}

	private boolean remove(MultipleItem<E> multipleItem) {
		MultipleItem<E> existingMultipleItem = itemMultiplicityMap.get(multipleItem.item);
		if (existingMultipleItem == null) {
			return false;
		} else {
			if (existingMultipleItem.multiplicity > multipleItem.multiplicity) {
				existingMultipleItem.multiplicity -= multipleItem.multiplicity;
			} else {
				// not enough items
				return false;
			}
		}
		return true;
	}

	@Override
	public void clear() {
		itemMultiplicityMap.clear();
	}

	@Override
	public boolean contains(Object o) {
		return itemMultiplicityMap.containsKey(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean result = true;
		for (Object element : c) {
			result = result && contains(element);
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		return itemMultiplicityMap.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return itemMultiplicityMap.keySet().iterator();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		return itemMultiplicityMap.size();
	}

	@Override
	public Object[] toArray() {
		return itemMultiplicityMap.keySet().toArray(); 
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return itemMultiplicityMap.keySet().toArray(a);
	}
}