package com.bnd.math.business.evo;

import java.util.Collection;

import com.bnd.math.domain.evo.Chromosome;

public interface EvoChromManipulatorBO<H extends Chromosome<C>, C> {

	/**
	 * Creates new test samples and discard old if any.
	 */
	H generateRandomChromosome();

	/**
	 * Performs the cross over operation on given parents producing two new offsprings.
	 * 
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @param position The position in the chromosome code where crossover is executed
	 * @return The new offsprings created by crossing the code of its parents
	 */
	Collection<H> crossOverSplit(H parent1, H parent2, int position);

	/**
	 * Performs the cross over operation on given parents producing two new offsprings.
	 * 
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @param firstParentPositionNum The position in the chromosome code where crossover is executed
	 * @return The new offsprings created by crossing the code of its parents
	 */
	Collection<H> crossOverShuffle(H parent1, H parent2, int firstParentPositionNum);

	void mutatePerBit(H chromosome, double mutationPerBitProbability);

	void mutateOneBit(H chromosome);

	void mutateTwoBits(H chromosome);

	void mutateBySwapping(H chromosome);

	H cloneChromosome(H chromosome);
}