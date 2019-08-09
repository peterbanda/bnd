package com.bnd.math.business.evo;

import java.util.Collection;

import com.bnd.math.domain.evo.Chromosome;

public interface EvoFitnessEvaluator<H extends Chromosome<?>, T> {

	/**
	 * Evaluates chromosome by using given test sample.
	 * 
	 * @param chromosome Chromosome to evaluate
	 * @param testSample Single test sample used for evaluation
	 * @Return Fitness score 
	 */
	Double evaluateScore(H chromosome, T testSample);

	/**
	 * Calculates fitness from score, if supported.
	 * 
	 * @param score Score of the chromosome 
	 * @return Fitness of the chromosome
	 */
	Double calcFitness(Double score);

	/**
	 * Evaluates and sets score and fitness for given chromosomes in a batch.
	 * 
	 * 
	 * @param chromosomes
	 * @param testSamples
	 */
	void evaluateScoreAndFitness(Collection<H> chromosomes, Collection<T> testSamples);
}