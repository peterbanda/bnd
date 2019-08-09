package com.bnd.core;

import java.util.Set;

public interface MultiSet<E> extends Set<E> {

	public boolean add(E element, int multiplicity);

	public boolean remove(E element, int multiplicity);

	public int getMultiplicity(E element);
}