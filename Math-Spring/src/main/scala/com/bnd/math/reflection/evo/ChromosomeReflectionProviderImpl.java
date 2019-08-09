package com.bnd.math.reflection.evo;

import com.bnd.core.reflection.SpringReflectionProviderImpl;
import com.bnd.math.domain.evo.Chromosome;

@SuppressWarnings("rawtypes")
public class ChromosomeReflectionProviderImpl extends SpringReflectionProviderImpl<Chromosome> {

	public ChromosomeReflectionProviderImpl(Class<? extends Chromosome<?>> clazz) {
		super(clazz);
	}

	@Override
	public Chromosome clone(Chromosome chromosome) {
		Chromosome chromosomeClone = super.clone(chromosome);
		chromosomeClone.setCode(getGenericProvider().clone(chromosome.getCode()));
		return chromosomeClone;
	}
}