package com.bnd.math.business.evo;


import com.bnd.math.domain.evo.Chromosome;

public class EvoTaskBO<H extends Chromosome<C>, C, T> {

	private final EvoChromManipulatorBO<H, C> chromManipulator;
	private final EvoFitnessEvaluator<H, T> fitnessEvaluator;
	private final EvoTestSampleGeneratorBO<T> testSampleGenerator;

	public EvoTaskBO(
		EvoChromManipulatorBO<H, C> chromManipulator,
		EvoFitnessEvaluator<H, T> fitnessEvaluator,
		EvoTestSampleGeneratorBO<T> testSampleGenerator
	) {
		this.chromManipulator = chromManipulator;
		this.fitnessEvaluator = fitnessEvaluator;
		this.testSampleGenerator = testSampleGenerator;
	}

	public EvoChromManipulatorBO<H, C> getChromManipulator() {
		return chromManipulator;
	}

	public EvoFitnessEvaluator<H, T> getFitnessEvaluator() {
		return fitnessEvaluator;
	}

	public EvoTestSampleGeneratorBO<T> getTestSampleGenerator() {
		return testSampleGenerator;
	}	
}