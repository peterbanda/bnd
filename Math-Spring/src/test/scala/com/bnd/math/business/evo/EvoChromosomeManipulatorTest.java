package com.bnd.math.business.evo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bnd.core.domain.ValueBound;
import com.bnd.core.reflection.ReflectionProvider;
import com.bnd.core.util.ObjectUtil;
import com.bnd.core.util.RandomUtil;
import com.bnd.math.business.MathTest;
import com.bnd.math.domain.evo.ArrayChromosome;
import com.bnd.math.domain.evo.BitMutationType;
import com.bnd.math.domain.evo.Chromosome;
import com.bnd.math.domain.evo.CompositeChromosome;

public class EvoChromosomeManipulatorTest extends MathTest {

	private Collection<EvoChromManipulatorBO<?, ?>> testInstances = new ArrayList<EvoChromManipulatorBO<?, ?>>();

	@Autowired
	ReflectionProvider<? extends ArrayChromosome<?>> chromosomeRF;
	
	@Before
	public void setUp() {
		// Instance 1
		testInstances.add(new EvoArrayChromManipulatorBO<Double>(
				(ReflectionProvider<ArrayChromosome<Double>>) chromosomeRF,
				20,
				BitMutationType.Replacement,
				null,
				Double.class,
				new ValueBound<Double>(0d, 9.9d)));

		// Instance 2
		Collection<ValueBound<Integer>> valueBounds = new ArrayList<ValueBound<Integer>>();
		for (int i = 0; i < 10; i++) {
			valueBounds.add(new ValueBound<Integer>(RandomUtil.nextInt(0, 10), RandomUtil.nextInt(30, 40)));
		}
		testInstances.add(new EvoArrayChromManipulatorBO<Integer>(
				(ReflectionProvider<ArrayChromosome<Integer>>) chromosomeRF,
				BitMutationType.Perturbation,
				0.1,
				Integer.class,
				valueBounds.toArray(new ValueBound[0])));

		// Instance 3
		Collection<ValueBound<Double>> valueBounds2 = new ArrayList<ValueBound<Double>>();
		for (int i = 0; i < 24; i++) {
			valueBounds2.add(new ValueBound<Double>(0d, 1d));
		}
		testInstances.add(new EvoArrayChromManipulatorBO<Double>(
				(ReflectionProvider<ArrayChromosome<Double>>) chromosomeRF,
				BitMutationType.Perturbation,
				1d,
				Double.class,
				valueBounds2.toArray(new ValueBound[0])));

		// Instance 4
		ValueBound<Boolean> valueBound3 = new ValueBound<Boolean>(new Boolean[] {Boolean.TRUE, Boolean.FALSE});
		testInstances.add(new EvoArrayChromManipulatorBO<Boolean>(
				(ReflectionProvider<ArrayChromosome<Boolean>>) chromosomeRF,
				63,
				BitMutationType.Replacement,
				null,
				Boolean.class,
				valueBound3));
	}

	@Test
	public void testGenerateRandomChromosome() {
		for (EvoChromManipulatorBO<?, ?> chromManipulator : testInstances) {
			Chromosome<?> chromosome = chromManipulator.generateRandomChromosome();
			assertEquals(chromosome.getCodeSize(), chromManipulator.codeSize);
			testChromosomeWithinBounds(chromManipulator, chromosome);
		}
	}

	private void testChromosomeWithinBounds(EvoChromManipulatorBO<?, ?> chromManipulator, Chromosome<?> chromosome) {
		int codeSize = chromManipulator.codeSize;
		if (chromManipulator instanceof EvoCompositeChromManipulatorBO<?, ?, ?>) {
			EvoCompositeChromManipulatorBO<?, ?, ?> uniformChromManipulator = (EvoCompositeChromManipulatorBO<?, ?, ?>) chromManipulator;
			ValueBound<?>[] bounds = uniformChromManipulator.codeElementValueBounds;
			assertEquals(codeSize, bounds.length);
			CompositeChromosome<?, ?> uniformChromosome = (CompositeChromosome<?, ?>) chromosome;
			for (int codePos = 0; codePos < codeSize; codePos++) {
				ValueBound<?> codeElementBound = bounds[codePos];
				Object element = uniformChromosome.getCodeAt(codePos);
				if (codeElementBound.hasEnumeratedValues()) {
					boolean found = false;
					for (Object enumeratedValue : codeElementBound.getEnumeratedOrderedValues()) {
						if (ObjectUtil.areObjectsEqual(enumeratedValue, element)) {
							found = true;
							break;
						}
					}
					assertTrue(found);
				} else if (element instanceof Number) {
					Number numberElement = (Number) element;
					assertTrue(numberElement.doubleValue() >= ((Number) codeElementBound.getFrom()).doubleValue());
					assertTrue(numberElement.doubleValue() <= ((Number) codeElementBound.getTo()).doubleValue());
				} else {
					// in this most general case we can not do anything
				}
			}
		}
	}

	@Test
	public void testCrossOver() {
		for (EvoChromManipulatorBO<?, ?> chromManipulator : testInstances) {
			testCrossOverWithRandomChromosomes(chromManipulator, false);
		}
	}

	@Test
	public void testCrossOverRandom() {
		for (EvoChromManipulatorBO<?, ?> chromManipulator : testInstances) {
			testCrossOverWithRandomChromosomes(chromManipulator, true);
		}
	}

	@Test
	public void testMutateOneBit() {
		for (EvoChromManipulatorBO<?, ?> chromManipulator : testInstances) {
			testMutateOneBitWithRandomChromosomes(chromManipulator);
		}
	}

	@Test
	public void testMutateTwoBits() {
		for (EvoChromManipulatorBO<?, ?> chromManipulator : testInstances) {
			testMutateTwoBitsWithRandomChromosomes(chromManipulator);
		}
	}

	@Test
	public void testMutateBySwapping() {
		for (EvoChromManipulatorBO<?, ?> chromManipulator : testInstances) {
			testMutateBySwappingWithRandomChromosomes(chromManipulator);
		}
	}

	@Test
	public void testMutatePerBit() {
		for (EvoChromManipulatorBO<?, ?> chromManipulator : testInstances) {
			testMutatePerBitWithRandomChromosomes(chromManipulator);
		}
	}

	private <H extends Chromosome<C>, C> void testMutateOneBitWithRandomChromosomes(EvoChromManipulatorBO<H, C> chromManipulator) {
		H chromosome = chromManipulator.generateRandomChromosome();
		H originalChromosome = ((ReflectionProvider<H>) chromosomeRF).clone(chromosome);

		chromManipulator.mutateOneBit(chromosome);

		testChromosomeWithinBounds(chromManipulator, chromosome);
		testUniformChromosomesCodeDiff(originalChromosome, chromosome, 1);
	}

	private <H extends Chromosome<C>, C> void testMutateTwoBitsWithRandomChromosomes(EvoChromManipulatorBO<H, C> chromManipulator) {
		H chromosome = chromManipulator.generateRandomChromosome();
		H originalChromosome = ((ReflectionProvider<H>) chromosomeRF).clone(chromosome);

		chromManipulator.mutateTwoBits(chromosome);

		testChromosomeWithinBounds(chromManipulator, chromosome);
		testUniformChromosomesCodeDiff(originalChromosome, chromosome, 2);
	}

	private <H extends Chromosome<C>, C> void testMutateBySwappingWithRandomChromosomes(EvoChromManipulatorBO<H, C> chromManipulator) {
		H chromosome = chromManipulator.generateRandomChromosome();
		H originalChromosome = ((ReflectionProvider<H>) chromosomeRF).clone(chromosome);

		chromManipulator.mutateBySwapping(chromosome);

		// TODO: Swapping might mess up the bound structure of chromosome
//		testChromosomeWithinBounds(chromManipulator, chromosome);
		testUniformChromosomesCodeDiff(originalChromosome, chromosome, 2);
	}

	private <H extends Chromosome<C>, C> void testMutatePerBitWithRandomChromosomes(EvoChromManipulatorBO<H, C> chromManipulator) {
		H chromosome = chromManipulator.generateRandomChromosome();
		H originalChromosome = ((ReflectionProvider<H>) chromosomeRF).clone(chromosome);

		chromManipulator.mutatePerBit(chromosome, 0d);

		testChromosomeWithinBounds(chromManipulator, chromosome);
		testUniformChromosomesCodeDiff(originalChromosome, chromosome, 0);

		chromManipulator.mutatePerBit(chromosome, 1d);

		testChromosomeWithinBounds(chromManipulator, chromosome);
		testUniformChromosomesCodeDiff(originalChromosome, chromosome, chromosome.getCodeSize());
	}

	private  <H extends Chromosome<C>, C> void testUniformChromosomesCodeDiff(H originalChromosome, H mutatedChromosome, int maxExpectedDiffCount) {
		int originalCodeSize = originalChromosome.getCodeSize(); 
		assertEquals(originalCodeSize, mutatedChromosome.getCodeSize());
		if (originalChromosome instanceof CompositeChromosome) {
			CompositeChromosome<C, ?> originalUniformChromosome = (CompositeChromosome<C, ?>) originalChromosome;
			CompositeChromosome<C, ?> mutatedUniformChromosome = (CompositeChromosome<C, ?>) mutatedChromosome;
			int diffCount = 0;
			for (int codePos = 0; codePos < originalCodeSize; codePos++) {
				if (!ObjectUtil.areObjectsEqual(originalUniformChromosome.getCodeAt(codePos), mutatedUniformChromosome.getCodeAt(codePos))) {
					diffCount++;
				}
			}
			assertTrue(diffCount <= maxExpectedDiffCount);
		}
	}

	private <H extends Chromosome<C>, C> void testCrossOverWithRandomChromosomes(EvoChromManipulatorBO<H, C> chromManipulator, boolean randomPositions) {
		final H parent1 = chromManipulator.generateRandomChromosome();
		final H parent2 = chromManipulator.generateRandomChromosome();
		final int codeSize = parent1.getCodeSize();

		assertTrue(parent1 != parent2);
		assertEquals(codeSize, parent2.getCodeSize());
		final int num = RandomUtil.nextInt(codeSize + 1);
		final Collection<H> offsprings = randomPositions ?
				chromManipulator.crossOverShuffle(parent1, parent2, num) :
				chromManipulator.crossOverSplit(parent1, parent2, num);

		assertEquals(2, offsprings.size());
		H offspring1 = ObjectUtil.getFirst(offsprings);
		H offspring2 = ObjectUtil.getSecond(offsprings);

		assertTrue(offspring1 != offspring2);
		assertTrue(parent1 != offspring1);
		assertTrue(parent1 != offspring2);
		assertTrue(parent2 != offspring1);
		assertTrue(parent2 != offspring2);
		assertEquals(codeSize, offspring1.getCodeSize());
		assertEquals(codeSize, offspring2.getCodeSize());
		testChromosomeWithinBounds(chromManipulator, offspring1);
		testChromosomeWithinBounds(chromManipulator, offspring2);

		if (randomPositions) {
			System.out.println("Parent 1 " + Arrays.toString((Object[]) parent1.getCode()));
			System.out.println("Parent 2 " + Arrays.toString((Object[]) parent2.getCode()));
			System.out.println("Parent 1 cross over positions num " + num);
			System.out.println("Offspring 1 " + Arrays.toString((Object[]) offspring1.getCode()));
			System.out.println("Offspring 2 " + Arrays.toString((Object[]) offspring2.getCode()));
		}

		for (H offspring : offsprings) {
			if (offspring instanceof CompositeChromosome) {
				CompositeChromosome<C, ?> uniformOffspring = (CompositeChromosome<C, ?>) offspring;
				CompositeChromosome<C, ?> uniformParent1 = (CompositeChromosome<C, ?>) parent1;
				CompositeChromosome<C, ?> uniformParent2 = (CompositeChromosome<C, ?>) parent2;
				for (int codePos = 0; codePos < codeSize; codePos++) {
					assertTrue(ObjectUtil.areObjectsEqual(uniformOffspring.getCodeAt(codePos), uniformParent1.getCodeAt(codePos))
							|| ObjectUtil.areObjectsEqual(uniformOffspring.getCodeAt(codePos), uniformParent2.getCodeAt(codePos)));
				}
			}
		}
	}
}