package com.bnd.math.domain.evo;

public class ArrayChromosome<E> extends CompositeChromosome<E[],E> {

	public ArrayChromosome() {
		super();
	}

	@Override
	public int getCodeSize() {
		return code.length;
	}

	@Override
	public E getCodeAt(int pos) {
		if (pos >= getCodeSize()) {
			return null;
		}
		return code[pos];
	}

	@Override
	public void setCodeAt(int pos, E value) {
		code[pos] = value;
	}
}