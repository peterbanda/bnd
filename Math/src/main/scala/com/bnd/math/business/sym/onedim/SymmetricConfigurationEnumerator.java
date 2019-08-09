package com.bnd.math.business.sym.onedim;

import com.bnd.function.enumerator.ListEnumerator;
import com.bnd.function.enumerator.ListEnumeratorFactory;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SymmetricConfigurationEnumerator {

	protected final int arraySize;
	protected final int alphabetSize;
	protected final ListEnumeratorFactory enumerator;

	public SymmetricConfigurationEnumerator(
		int arraySize,
		int alphabetSize,
		ListEnumeratorFactory enumerator
	) {
		this.arraySize = arraySize;
		this.alphabetSize = alphabetSize;
		this.enumerator = enumerator;
	}

	private static boolean isDivisibleBy(int num, Collection<Integer> divisors) {
		for (Integer divisor : divisors) {
			if (num % divisor == 0) {
				return true;
			}
		}
		return false;
	}

	public static List<Integer> calcPrimeDivisors(int num) {
		List<Integer> primeDivisors = new ArrayList<Integer>();
		for (int divisor = 2; divisor <= num; divisor++) {
			if (num % divisor == 0 && !isDivisibleBy(divisor, primeDivisors)) {
				primeDivisors.add(divisor);
			}			
		}
		return primeDivisors;
	}

	public static List<Integer> calcPrimeDivisorPows(int num) {
		List<Integer> primePows = new ArrayList<Integer>();
		List<Integer> primeDivisors = calcPrimeDivisors(num);
		for (int primeDivisor : primeDivisors) {
			int primePow = primeDivisor;
			while (num % primePow == 0) {
				primePows.add(primePow);
				primePow *= primeDivisor;
			}
		}
		return primePows;
	}

	public long enumerateAll() {
		return enumerateAll(alphabetSize);
	}

	protected long enumerateAll(int alphabetSize) {
		final List<Integer> primeDivisors = calcPrimeDivisors(arraySize);
		final ListEnumerator<Integer> primeCombEnumerator = enumerator.createInstance(false, primeDivisors);
		final int omega = primeDivisors.size();

		long sum = 0;
		for (int i = 1; i <= omega; i++) {
			long partialSum = 0;
			for (List<Integer> selectedPrimes : primeCombEnumerator.enumerate(i)) {
//				System.out.println(selectedPrimes);
				int primeProduct = 1;
				for (Integer selectedPrime : selectedPrimes) primeProduct *=  selectedPrime;
				partialSum += Math.pow(alphabetSize, arraySize / primeProduct); 
			}
			if (i % 2 == 0)
				sum -= partialSum;
			else
				sum += partialSum;				
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
				partialSum += ArithmeticUtils.binomialCoefficientDouble(arraySize /  primeProduct, activeCellsNum / primeProduct)
								* Math.pow((alphabetSize - 1), (arraySize - activeCellsNum) / primeProduct);
			}
			if (i % 2 == 0)
				sum -= partialSum;
			else
				sum += partialSum;				
		}
		return sum;
	}
}