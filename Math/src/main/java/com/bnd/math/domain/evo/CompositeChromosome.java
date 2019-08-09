package com.bnd.math.domain.evo;

import java.io.Serializable;

public abstract class CompositeChromosome<C, E> extends Chromosome<C> implements Serializable {

	public CompositeChromosome() {
		super();
	}

	public abstract E getCodeAt(int pos);

	public abstract void setCodeAt(int pos, E value);

	public void copyCodeAt(int pos, CompositeChromosome<C, E> chromosome) {
		setCodeAt(pos, chromosome.getCodeAt(pos));
	}

	public void swapElements(int pos1, int pos2) {
		E element1 = getCodeAt(pos1);
		setCodeAt(pos1, getCodeAt(pos2));
		setCodeAt(pos2, element1);
	}
}