package com.bnd.math.business.evo;

import com.bnd.core.reflection.ReflectionProvider;
import com.bnd.core.util.RandomUtil;

import com.bnd.math.domain.evo.Chromosome;
import com.bnd.math.task.EvoRunTask;

/**
 * Functional object for a population of elite type.
 */
final class EliteGeneticAlgorithmBO<H extends Chromosome<C>, C, T> extends GeneticAlgorithmBO<H, C, T> {

	protected EliteGeneticAlgorithmBO(
		EvoRunTask evoRunTask,
		EvoTaskBO<H, C, T> evoTaskBO,
		ReflectionProvider<Chromosome<C>> chromosomeRF
	) {
		super(evoRunTask, evoTaskBO, chromosomeRF);
	}

	/**
	 * Copies the elite chromosomes to next generation without changes.
	 */
	private void copyEliteToNextGeneration() {
		int eliteChromosomesCount = 0;
		for (H chromosome : getChromosomes()) {
			if (eliteChromosomesCount == getGaSetting().getEliteNumber()) {
				break;
			}
			addNewGenerationChromosome(
					getChromManipulatorBO().cloneChromosome(chromosome));
			eliteChromosomesCount++;
		}
	}

	/**
	 * Proceeds the cross over operation using elite type.
	 */
	@Override
	protected void crossOver() {
		copyEliteToNextGeneration();
		int theEliteNumber = getGaSetting().getEliteNumber().intValue();
		H[] eliteChromosomes = getNewGenerationChromosomesAsArray();
		while (getNewGenerationChromosomeNumber() < getGaSetting().getPopulationSize()) {
			int parent1Index = RandomUtil.nextInt(theEliteNumber);
			int parent2Index = RandomUtil.nextIntExcept(theEliteNumber, parent1Index);
			H parent1 = eliteChromosomes[parent1Index];
			H parent2 = eliteChromosomes[parent2Index];
			crossOver(parent1, parent2, getGaSetting().getPopulationSize() - getNewGenerationChromosomeNumber() > 1);
		}
	}

	/**
	 * Proceeds the mutation operation using elite type.
	 */
	@Override
	protected void mutate() {
		final H[] chromosomes = getNewGenerationChromosomesAsArray();
		final int chromosomeNumber = getNewGenerationChromosomeNumber();
		for (int chromosomeIndex = getGaSetting().getEliteNumber(); chromosomeIndex < chromosomeNumber; chromosomeIndex++) {
			mutate(chromosomes[chromosomeIndex]);
		}
	}
}