package com.bnd.math.business.evo;

import java.util.Collection;

import com.bnd.core.reflection.ReflectionProvider;
import com.bnd.core.util.ObjectUtil;
import com.bnd.math.domain.evo.BitMutationType;
import com.bnd.math.domain.evo.Chromosome;

public abstract class EvoChromManipulatorBO<H extends Chromosome<C>, C> {

	protected final ReflectionProvider<H> chromosomeRF;
	protected final int codeSize; 
	protected BitMutationType bitMutationType;
	protected Double pertrubMutationStrength;

	public EvoChromManipulatorBO(
		ReflectionProvider<H> chromosomeRF,
		int codeSize,
		BitMutationType bitMutationType
	) {
		this(chromosomeRF, codeSize, bitMutationType, null);
	}

	public EvoChromManipulatorBO(
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

	/**
	 * Creates new test samples and discard old if any.
	 */
	public abstract H generateRandomChromosome();

	/**
	 * Performs the cross over operation on given parents producing two new offsprings.
	 * 
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @param position The position in the chromosome code where crossover is executed
	 * @return The new offsprings created by crossing the code of its parents
	 */
	public abstract Collection<H> crossOverSplit(H parent1, H parent2, int position);

	/**
	 * Performs the cross over operation on given parents producing two new offsprings.
	 * 
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @param firstParentPositionNum The position in the chromosome code where crossover is executed
	 * @return The new offsprings created by crossing the code of its parents
	 */
	public abstract Collection<H> crossOverShuffle(H parent1, H parent2, int firstParentPositionNum);

	public abstract void mutatePerBit(H chromosome, double mutationPerBitProbability);

	public abstract void mutateOneBit(H chromosome);

	public abstract void mutateTwoBits(H chromosome);

	public abstract void mutateBySwapping(H chromosome);

	protected H createNewChromosome() {
		H newChromosome = chromosomeRF.createNewInstance();
		newChromosome.setCode(createBlankCode());
		return newChromosome;
	}

	public H cloneChromosome(H chromosome) {
		H newChromosome = chromosomeRF.clone(chromosome);
		ObjectUtil.nullIdAndVersion(newChromosome);
		return newChromosome;
	}
}