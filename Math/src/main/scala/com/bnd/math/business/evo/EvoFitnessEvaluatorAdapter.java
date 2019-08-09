package com.bnd.math.business.evo;

import java.io.Serializable;
import java.util.Collection;

import com.bnd.math.BndMathException;
import com.bnd.math.domain.evo.Chromosome;

public abstract class EvoFitnessEvaluatorAdapter<H extends Chromosome<?>, T> implements EvoFitnessEvaluator<H, T>, Serializable {

	@Override
	public Double evaluateScore(H chromosome, T testSample) {
		throw new BndMathException("Evaluate score not implemented.");
	}

	@Override
	public Double calcFitness(Double score) {
		throw new BndMathException("Calc fitness not implemented.");
	}

	@Override
	public void evaluateScoreAndFitness(Collection<H> chromosomes, Collection<T> testSamples) {
		for (H chromosome : chromosomes) {
			chromosome.nullScore();
			for (final T testSample : testSamples) {
				chromosome.addToScore(
					evaluateScore(chromosome, testSample));
			}
			chromosome.setFitness(
				calcFitness(chromosome.getScore()));
		}
	}
}