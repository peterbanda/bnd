package com.bnd.math.business.sym.onedim;

import com.bnd.function.enumerator.ListEnumerator;
import com.bnd.function.enumerator.ListEnumeratorFactory;

import java.util.List;

public class SymmetricLooselyCoupledConfigurationEnumerator extends SymmetricConfigurationEnumerator {

	private final int radius;
	
	public SymmetricLooselyCoupledConfigurationEnumerator(
		int arraySize,
		int alphabetSize,
		int radius,
		ListEnumeratorFactory enumerator
	) {
		super(arraySize, alphabetSize, enumerator);
		this.radius = radius;
	}

	private LooselyCoupledConfigurationEnumerator createLooselyCoupledEnumerator(int arraySize) {
		return new LooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius);
	}

	private int calcMaxActiveCellsNum() {
		return arraySize / (2 * radius + 1);
	}

	public long enumerateAll() {
		int maxActiveCellsNum = calcMaxActiveCellsNum();
		long sum = 0;
		for (int activeCellsNum = 0; activeCellsNum <= maxActiveCellsNum; activeCellsNum++) {
			sum += enumerate(activeCellsNum);
		}
		return sum;
	}

	public long enumerate(int activeCellsNum) {
		if (activeCellsNum == 0) { 
			return enumerateAll(alphabetSize - 1);
		}
		final List<Integer> arrayPrimeDivisors = calcPrimeDivisors(arraySize);
		final List<Integer> activeCellsPrimeDivisors = calcPrimeDivisors(activeCellsNum);
		activeCellsPrimeDivisors.retainAll(arrayPrimeDivisors);

		if (activeCellsPrimeDivisors.isEmpty())
			return 0;

//		System.out.println(activeCellsPrimeDivisors);

		final ListEnumerator<Integer> primeCombEnumerator = enumerator.createInstance(false, activeCellsPrimeDivisors);
		final int omega = activeCellsPrimeDivisors.size();

		long sum = 0;
		for (int i = 1; i <= omega; i++) {
			long partialSum = 0;
			for (List<Integer> selectedPrimes : primeCombEnumerator.enumerate(i)) {
//				System.out.println(selectedPrimes);
				int primeProduct = 1;
				for (Integer selectedPrime : selectedPrimes) primeProduct *=  selectedPrime;

				final LooselyCoupledConfigurationEnumerator lcEnumerator = createLooselyCoupledEnumerator(arraySize / primeProduct);
				partialSum += lcEnumerator.enumerate(activeCellsNum / primeProduct);
			}
			if (i % 2 == 0)
				sum -= partialSum;
			else
				sum += partialSum;
		}
		return sum;
	}
}