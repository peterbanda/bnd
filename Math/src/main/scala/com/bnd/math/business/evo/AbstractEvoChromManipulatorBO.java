package com.bnd.math.business.evo;

import com.bnd.core.reflection.ReflectionProvider;
import com.bnd.core.util.ObjectUtil;
import com.bnd.math.domain.evo.BitMutationType;
import com.bnd.math.domain.evo.Chromosome;

public abstract class AbstractEvoChromManipulatorBO<H extends Chromosome<C>, C> implements EvoChromManipulatorBO<H, C> {

	protected final ReflectionProvider<H> chromosomeRF;
	protected final int codeSize;
	protected BitMutationType bitMutationType;
	protected Double pertrubMutationStrength;

	public AbstractEvoChromManipulatorBO(
		ReflectionProvider<H> chromosomeRF,
		int codeSize,
		BitMutationType bitMutationType
	) {
		this(chromosomeRF, codeSize, bitMutationType, null);
	}

	public AbstractEvoChromManipulatorBO(
		ReflectionProvider<H> chromosomeRF,
		int codeSize,
		BitMutationType bitMutationType,
		Double pertrubMutationStrength
	) {
		this.chromosomeRF = chromosomeRF;
		this.codeSize = codeSize;
		this.bitMutationType = bitMutationType;
		this.pertrubMutationStrength = pertrubMutationStrength;
	}

	protected abstract C createBlankCode();

	protected H createNewChromosome() {
		H newChromosome = chromosomeRF.createNewInstance();
		newChromosome.setCode(createBlankCode());
		return newChromosome;
	}

	@Override
	public H cloneChromosome(H chromosome) {
		H newChromosome = chromosomeRF.clone(chromosome);
		ObjectUtil.nullIdAndVersion(newChromosome);
		return newChromosome;
	}
}