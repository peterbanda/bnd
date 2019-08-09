package com.bnd.math.business.evo;

import com.bnd.core.reflection.ReflectionProvider;
import com.bnd.core.util.RandomUtil;

import com.bnd.math.BndMathException;
import com.bnd.math.domain.evo.Chromosome;
import com.bnd.math.task.EvoRunTask;

/**
 * Functional object for a population of roulette type.
 */
final class RouletteGeneticAlgorithmBO<H extends Chromosome<C>, C, T> extends GeneticAlgorithmBO<H, C, T> {

	protected RouletteGeneticAlgorithmBO(
		EvoRunTask evoRunTask,
		EvoTaskBO<H, C, T> evoTaskBO,
		ReflectionProvider<Chromosome<C>> chromosomeRF
	) {
		super(evoRunTask, evoTaskBO, chromosomeRF);
	}

	/**
	 * Gets the the randomly chosen chromosome with probability 
	 * proportional to its fitness. 
	 * 
	 * @param scale The scale of roulette.
	 * @return The the randomly chosen chromosome using roulette.
	 */
	private H getRouletteChromosomeWinner(double scale) {
		double fitnessHit = RandomUtil.nextDouble(scale);
		double sum = 0;
		for (H chromosome : getChromosomes()) { 
			sum += chromosome.getFitness();
			if (sum > fitnessHit) {
				return chromosome;
			}
		}
		// out of bound
		throw new BndMathException("Roulette chromosome winner out of bound.");
	}

	/**
	 * Processes the cross over operation using roulette paradigm.
	 * 
	 * @param aFitness The fitness in case of conditional fitness.
	 */
	@Override
	protected void crossOver() {
		double theFitnessSum = getPopulationFitnessSum();
		while (getNewGenerationChromosomeNumber() < getGaSetting().getPopulationSize()) {
			// TODO Wrong - the same parent should not be chosen twice, or? 
			H parent1 = getRouletteChromosomeWinner(theFitnessSum);
			H parent2 = getRouletteChromosomeWinner(theFitnessSum);
			crossOver(parent1, parent2, getGaSetting().getPopulationSize() - getNewGenerationChromosomeNumber() > 1);
		}
	}

	/**
	 * Proceeds the mutation operation using roulette type.
	 */
	@Override
	protected void mutate() {
		for (H chromosome : getNewGenerationChromosomes()) {
			mutate(chromosome);
		}
	}
}