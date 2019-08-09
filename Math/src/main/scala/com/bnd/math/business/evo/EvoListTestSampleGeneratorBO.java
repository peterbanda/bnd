package com.bnd.math.business.evo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bnd.math.business.rand.RandomDistributionProvider;

public class EvoListTestSampleGeneratorBO<T extends Number> implements EvoTestSampleGeneratorBO<List<T>> {

	private final RandomDistributionProvider<T> randomDistributionProvider;
	private final int sampleNum;
	private final int listSize;

	public EvoListTestSampleGeneratorBO(
		RandomDistributionProvider<T> randomDistributionProvider,
		int sampleNum,
		int listSize
	) {
		this.randomDistributionProvider = randomDistributionProvider;
		this.sampleNum = sampleNum;
		this.listSize = listSize;
	}

	@Override
	public Collection<List<T>> createTestSamples() {
		Collection<List<T>> testSamples = new ArrayList<List<T>>();
		for (int i = 0; i < sampleNum; i++) {
			testSamples.add(randomDistributionProvider.nextList(listSize));
		}
		return testSamples;
	}
}