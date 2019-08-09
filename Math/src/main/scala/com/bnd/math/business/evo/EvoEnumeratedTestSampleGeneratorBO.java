package com.bnd.math.business.evo;

import java.util.*;

import com.bnd.math.BndMathException;


public class EvoEnumeratedTestSampleGeneratorBO<T> implements EvoTestSampleGeneratorBO<T> {

	private final List<T> availableTestSamples;
	private final Integer numberOfSelectedSampleCopies;
	private final Integer numberOfSamplesToSelect;

	public EvoEnumeratedTestSampleGeneratorBO(Collection<T> availableTestSamples) {
		this(availableTestSamples, null);
	}

	public EvoEnumeratedTestSampleGeneratorBO(
		Collection<T> availableTestSamples,
		Integer numberOfSelectedSampleCopies
	) {
		this(availableTestSamples, numberOfSelectedSampleCopies, null);
	}

	public EvoEnumeratedTestSampleGeneratorBO(
		Collection<T> availableTestSamples,
		Integer numberOfSelectedSampleCopies,
		Integer numberOfSamplesToSelect
	) {
		this.availableTestSamples = new ArrayList<T>(availableTestSamples);
		this.numberOfSelectedSampleCopies = numberOfSelectedSampleCopies;
		this.numberOfSamplesToSelect = numberOfSamplesToSelect;
		if (numberOfSamplesToSelect != null && numberOfSamplesToSelect > availableTestSamples.size()) {
			throw new BndMathException("The number of test samples to select is greater than the number of available test samples.");
		}
	}

	@Override
	public Collection<T> createTestSamples() {
		Collection<T> testSamples = null;
		if (numberOfSamplesToSelect == null) {
			testSamples = availableTestSamples;
		} else {
			testSamples = selectTestSamplesRandomly();
		}
		if (numberOfSelectedSampleCopies != null && numberOfSelectedSampleCopies > 1) {
			Collection<T> duplicatedTestSamples = new ArrayList<T>();
			for (int copyIndex = 0; copyIndex < numberOfSelectedSampleCopies; copyIndex++) {
				duplicatedTestSamples.addAll(testSamples);
			}
			return duplicatedTestSamples;
		}
		return testSamples;
	}

	protected Collection<T> selectTestSamplesRandomly() {
		Collection<T> randomlySelectedSamples = new ArrayList<T>();
		Collections.shuffle(availableTestSamples);
		Iterator<T> availableTestSamplesIterator = availableTestSamples.iterator();
		for (int i = 0; i < numberOfSamplesToSelect; i++) {
			randomlySelectedSamples.add(availableTestSamplesIterator.next());
		}
		return randomlySelectedSamples;
	}

	protected List<T> getAvailableTestSamples() {
		return availableTestSamples;
	}

	protected Integer getNumberOfSamplesToSelect() {
		return numberOfSamplesToSelect;
	}

	protected int getTestSamplesNumber() {
		int testSamplesNum = numberOfSamplesToSelect != null ? numberOfSamplesToSelect : availableTestSamples.size();
		return numberOfSelectedSampleCopies != null ? numberOfSelectedSampleCopies * testSamplesNum : testSamplesNum;
	}
}