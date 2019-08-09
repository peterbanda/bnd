package com.bnd.math.business.evo;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Test;

import com.bnd.core.util.RandomUtil;
import com.bnd.math.domain.Stats;


public class EvoTestSampleGeneratorTest extends TestCase {

	private Collection<EvoTestSampleGeneratorBO<?>> testInstances = new ArrayList<EvoTestSampleGeneratorBO<?>>();

	@Override
	protected void setUp() {
		// Instance 1
		Collection<Double> testSamples1 = new ArrayList<Double>();
		for (int i = 0; i < 50; i++) {
			testSamples1.add(RandomUtil.nextDouble(0.1, 10.7));
		}
		testInstances.add(new EvoEnumeratedTestSampleGeneratorBO<Double>(testSamples1));
		// Instance 2
		Collection<Stats> testSamples2 = new ArrayList<Stats>();
		for (int i = 0; i < 60; i++) {
			Stats actionSeries = new Stats();
			actionSeries.setId(new Long(RandomUtil.nextInt(0, 100000)));
			testSamples2.add(actionSeries);
		}
		testInstances.add(new EvoEnumeratedTestSampleGeneratorBO<Stats>(testSamples2, null, 10));
		// Instance 3
		Collection<Integer> testSamples3 = new ArrayList<Integer>();
		for (int i = 0; i < 100; i++) {
			testSamples3.add(RandomUtil.nextInt(1, 1000));
		}
		testInstances.add(new EvoEnumeratedTestSampleGeneratorBO<Integer>(testSamples3, 2, 99));
	}

	@Test
	public void testCreateTestSamples() {
		for (EvoTestSampleGeneratorBO<?> testSampleGenerator : testInstances) {
			Collection<?> testSamples = testSampleGenerator.createTestSamples();
			int testSamplesNum = testSamples.size();
			assertNotNull(testSamples);
			assertTrue(testSamplesNum > 0);
			if (testSampleGenerator instanceof EvoEnumeratedTestSampleGeneratorBO) {
				EvoEnumeratedTestSampleGeneratorBO<?> enumeratedTestSampleGenerator = (EvoEnumeratedTestSampleGeneratorBO<?>) testSampleGenerator;
				assertEquals(enumeratedTestSampleGenerator.getTestSamplesNumber(), testSamples.size());
			}
		}
	}
}