package com.bnd.math.business.evo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.bnd.core.domain.ValueBound;
import com.bnd.core.reflection.ReflectionProvider;
import com.bnd.core.reflection.ReflectionUtil;
import com.bnd.core.util.RandomUtil;

import com.bnd.math.BndMathException;
import com.bnd.math.domain.evo.BitMutationType;
import com.bnd.math.domain.evo.CompositeChromosome;


public abstract class EvoCompositeChromManipulatorBO<H extends CompositeChromosome<C, E>, C, E> extends EvoChromManipulatorBO<H, C> {

	protected final Class<E>[] elementClasses;
	protected final ValueBound<E>[] codeElementValueBounds; 

	public EvoCompositeChromManipulatorBO(
		ReflectionProvider<H> chromosomeRF,
		int codeSize,
		BitMutationType bitMutationType,
		Double pertrubMutationStrength,
		Class<E>[] elementClasses,
		ValueBound<E>[] codeElementValueBounds
	) {
		super(chromosomeRF, codeSize, bitMutationType, pertrubMutationStrength);
// 		TODO: This should work in JDK 1.7
//		this.elementClass = (Class<E>) replicator.getParamClass(getClass(), 2);
		this.elementClasses = elementClasses;
		this.codeElementValueBounds = codeElementValueBounds;
	}

	public EvoCompositeChromManipulatorBO(
		ReflectionProvider<H> chromosomeRF,
		int codeSize,
		BitMutationType bitMutationType,
		Double pertrubMutationStrength,
		Class<E> sharedElementClass,
		ValueBound<E>[] codeElementValueBounds
	) {
		this(chromosomeRF, codeSize, bitMutationType, pertrubMutationStrength, ReflectionUtil.fillNewArray(sharedElementClass, codeSize), codeElementValueBounds);
	}

	public EvoCompositeChromManipulatorBO(
		ReflectionProvider<H> chromosomeRF,
		int codeSize,
		BitMutationType bitMutationType,
		Double pertrubMutationStrength,
		Class<E> elementClass
	) {
		this(chromosomeRF, codeSize, bitMutationType, pertrubMutationStrength, elementClass, (ValueBound<E>[]) null);
	}

	public EvoCompositeChromManipulatorBO(
		ReflectionProvider<H> chromosomeRF,
		int codeSize,
		BitMutationType bitMutationType,
		Double pertrubMutationStrength,
		Class<E> elementClass,
		ValueBound<E> sharedCodeElementValueBound
	) {
		this(chromosomeRF, codeSize, bitMutationType, pertrubMutationStrength, elementClass, ReflectionUtil.fillNewArray(sharedCodeElementValueBound, codeSize));
	}

	@Override
	public Collection<H> crossOverSplit(H parent1, H parent2, int position) {
		final int codeSize = parent1.getCodeSize();
		H offspring1 = createNewChromosome();
		H offspring2 = createNewChromosome();
		for (int elementIndex = 0; elementIndex < codeSize; elementIndex++) {
			if (elementIndex == position) {
				H tempParent = parent1;
				parent1 = parent2;
				parent2 = tempParent;
			}
			offspring1.copyCodeAt(elementIndex, parent1);
			offspring2.copyCodeAt(elementIndex, parent2);
		}
		Collection<H> offsprings = new ArrayList<H>();
		offsprings.add(offspring1);
		offsprings.add(offspring2);
		return offsprings;
	}

	@Override
	public Collection<H> crossOverShuffle(H parent1, H parent2, int firstParentPositionNum) {
		final int codeSize = parent1.getCodeSize();
		H offspring1 = createNewChromosome();
		H offspring2 = createNewChromosome();
		Collection<Integer> firstParentPositions = RandomUtil.nextElementsWithoutRepetitions(codeSize, firstParentPositionNum);
		for (int elementIndex = 0; elementIndex < codeSize; elementIndex++) {
			if (firstParentPositions.contains(elementIndex)) {
				offspring1.copyCodeAt(elementIndex, parent1);
				offspring2.copyCodeAt(elementIndex, parent2);
			} else {
				offspring1.copyCodeAt(elementIndex, parent2);
				offspring2.copyCodeAt(elementIndex, parent1);				
			}
		}
		Collection<H> offsprings = new ArrayList<H>();
		offsprings.add(offspring1);
		offsprings.add(offspring2);
		return offsprings;
	}

	@Override
	public H generateRandomChromosome() {
		H newChromosome = createNewChromosome();
		for (int i = 0; i < codeSize; i++) {
			newChromosome.setCodeAt(i, generateRandomCodeElement(i));
		}
		return newChromosome;
	}

	protected E generateRandomCodeElement(int index) {
		return RandomUtil.next(elementClasses[index], codeElementValueBounds != null ? codeElementValueBounds[index] : null);
	}

	protected E generatePerturbatedElement(E value, ValueBound<E> codeElementValueBound, Double strength) {
		if (codeElementValueBound == null) {
			throw new BndMathException("No code element value bound defined, but expected.");
		}
		E perturbatedElementValue = null;
		if (codeElementValueBound.hasEnumeratedValues()) {
			int enumIndex = codeElementValueBound.getEnumeratedValueIndex(value);
			int newIndex = RandomUtil.perturbate(enumIndex, strength, 0, codeElementValueBound.getEnumeratedOrderedValuesNum() - 1);
			perturbatedElementValue = codeElementValueBound.getEnumeratedOrderedValues()[newIndex];
		} else {
			// assume code element is a number
			ValueBound<Number> numberCodeElementValueBound = (ValueBound<Number>) codeElementValueBound;
			perturbatedElementValue = (E) RandomUtil.perturbate((Number) value, strength, numberCodeElementValueBound.getFrom(), numberCodeElementValueBound.getTo());
		}
		return perturbatedElementValue;
	}

	@Override
	public void mutateOneBit(H chromosome) {
		int codeIndex = RandomUtil.nextInt(codeSize);
		mutateElement(chromosome, codeIndex);
	}

	@Override
	public void mutateTwoBits(H chromosome) {
		mutateElements(chromosome, 2);
	}

	@Override
	public void mutateBySwapping(H chromosome) {
		int index1 = RandomUtil.nextInt(codeSize);
		int index2 = RandomUtil.nextIntExcept(codeSize, index1); 
		chromosome.swapElements(index1, index2);		
	}

	@Override
	public void mutatePerBit(H chromosome, double mutationPerBitProbability) {
		for (int i = 0; i < codeSize; i ++) {
			if (RandomUtil.nextDouble() < mutationPerBitProbability) {
				mutateElement(chromosome, i);
			}
		}
	}

	protected void mutateElement(H chromosome, int pos) {
		E mutatedElement = null;
		switch (bitMutationType) {
			case Replacement:
				mutatedElement = generateRandomCodeElement(pos);
				break;
			case Perturbation:
				mutatedElement = generatePerturbatedElement(chromosome.getCodeAt(pos), codeElementValueBounds[pos], pertrubMutationStrength);
				break;
		}
		chromosome.setCodeAt(pos, mutatedElement);
	}

	/**
	 * Mutates the given number of elements in chromosome code.
	 *
	 * @param elementsNum The number of elements to mutate.
	 */
	public void mutateElements(H chromosome, int elementsNum) {
		List<Integer> availableIndeces = new ArrayList<Integer>();
		for (int i = 0; i < codeSize; i++) {
			availableIndeces.add(new Integer(i));
		}
		Collections.shuffle(availableIndeces);
		for (int i = 0; i < elementsNum; i++) {
			int selectedElementIndex = availableIndeces.remove(0);
			mutateElement(chromosome, selectedElementIndex);
		}
	}
}