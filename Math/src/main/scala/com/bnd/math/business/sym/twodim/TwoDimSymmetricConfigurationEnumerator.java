package com.bnd.math.business.sym.twodim;

import com.bnd.function.enumerator.ListEnumerator;
import com.bnd.function.enumerator.ListEnumeratorFactory;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TwoDimSymmetricConfigurationEnumerator {

	protected final int size;
	protected final int alphabetSize;
	protected final ListEnumeratorFactory enumerator;

	public TwoDimSymmetricConfigurationEnumerator(
		int size,
		int alphabetSize,
		ListEnumeratorFactory enumerator
	) {
		this.size = size;
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

	public BigInteger enumerateAll() {
		return enumerateAll(alphabetSize);
	}

	public BigInteger enumerateAll2() {
		return enumerateAll2(alphabetSize);
	}

    public BigInteger enumerateAll3() {
        return enumerateAll3(alphabetSize);
    }

	protected BigInteger enumerateAll(int alphabetSize) {
		final List<Integer> primeDivisors = calcPrimeDivisors(size);
		final int omega = primeDivisors.size();
		List<Integer> rangeFrom = new ArrayList<Integer>();
		List<Integer> rangeTo = new ArrayList<Integer>();
		for (int i = 0; i < omega; i++) {
			rangeFrom.add(0);
			rangeTo.add(primeDivisors.get(i) + 1);
		}
		final ListEnumerator<Integer> lCombEnumerator = enumerator.createInstance(true, rangeFrom, rangeTo);

		BigInteger sum = BigInteger.valueOf(0);
		BigInteger alphabet = BigInteger.valueOf(alphabetSize);
		for (List<Integer> l : lCombEnumerator.enumerate(omega)) {
			BigInteger binomProduct = BigInteger.ONE;
			int primeProduct = 1;
			long lSum = 0;

			for (int i = 0; i < omega; i++) {
				BigInteger binom = binomialCoefficientBigInteger((primeDivisors.get(i) + 1), l.get(i));
				binomProduct = binomProduct.multiply(binom);
				primeProduct *= Math.pow(primeDivisors.get(i), Math.min(l.get(i), 2));
				lSum += l.get(i);
			}

			if (lSum != 0) {
				BigInteger partialSum = binomProduct.multiply(alphabet.pow(size * size / primeProduct));
				if (lSum % 2 == 0)
					sum = sum.subtract(partialSum);
				else
					sum = sum.add(partialSum);
			}
		}
		return sum;
	}

	protected BigInteger enumerateAll2(int alphabetSize) {
		final List<Integer> primeDivisors = calcPrimeDivisors(size);
		final int omega = primeDivisors.size();
		final ListEnumerator<Integer> kCombEnumerator = enumerator.createInstance(true, 0, 2);

		BigInteger sum = BigInteger.valueOf(0);
		BigInteger alphabet = BigInteger.valueOf(alphabetSize);
		for (List<Integer> k : kCombEnumerator.enumerate(omega)) {
			int primeProduct = 1;
			long kSum = 0;

			for (int i = 0; i < omega; i++) {
				primeProduct *= Math.pow(primeDivisors.get(i), k.get(i));
				kSum += k.get(i);
			}
			
			if (kSum != 0) {
				BigInteger commonCoef = alphabet.pow(size * size / primeProduct);

				List<Integer> rangeFrom = new ArrayList<Integer>();
				List<Integer> rangeTo = new ArrayList<Integer>();
				for (int i = 0; i < omega; i++) {
					int ki = k.get(i);
					rangeFrom.add(ki);
					rangeTo.add((ki < 2) ? ki : primeDivisors.get(i) + 1);					
				}
				final ListEnumerator<Integer> lCombEnumerator = enumerator.createInstance(true, rangeFrom, rangeTo);

				BigInteger partialSum = BigInteger.ZERO;
				for (List<Integer> l : lCombEnumerator.enumerate(omega)) {
					BigInteger binomProduct = BigInteger.ONE;
					long lSum = 0;

					for (int i = 0; i < omega; i++) {
						BigInteger binom = binomialCoefficientBigInteger((primeDivisors.get(i) + 1), l.get(i));
						binomProduct = binomProduct.multiply(binom);
						lSum += l.get(i);
					}

					if (lSum % 2 == 0)
						partialSum = partialSum.subtract(binomProduct);
					else
						partialSum = partialSum.add(binomProduct);
				}
				sum = sum.add(commonCoef.multiply(partialSum));
			}
		}
		return sum;
	}

	protected BigInteger enumerateAll3(int alphabetSize) {
		final List<Integer> primeDivisors = calcPrimeDivisors(size);
		final int omega = primeDivisors.size();
		final ListEnumerator<Integer> kCombEnumerator = enumerator.createInstance(true, 0, 2);

		BigInteger sum = BigInteger.ZERO;
		BigInteger alphabet = BigInteger.valueOf(alphabetSize);
		for (List<Integer> k : kCombEnumerator.enumerate(omega)) {
			int primeProduct = 1;
            int combProduct = 1;
			long kSum = 0;

			for (int i = 0; i < omega; i++) {
                int prime = primeDivisors.get(i);
                int exp = k.get(i);

                if (exp != 0) {
                    primeProduct *= Math.pow(prime, exp);

                    if (exp == 1)
                        combProduct *= (prime + 1);
                    else if (exp == 2)
                        combProduct *= prime;
                }
				kSum += exp;
			}

			if (kSum != 0) {
                BigInteger partial = alphabet.pow(size * size / primeProduct).multiply(BigInteger.valueOf(combProduct));

                if (kSum % 2 == 1)
                    sum = sum.add(partial);
				else
                    sum = sum.subtract(partial);
			}
		}
		return sum;
	}

	public BigInteger enumerate(int activeCellsNum) {
		if (activeCellsNum == 0) { 
			return enumerateAll(alphabetSize - 1);
		}
		final List<Integer> gcdPrimeDivisors = calcPrimeDivisors(ArithmeticUtils.gcd(size, activeCellsNum));
		if (gcdPrimeDivisors.isEmpty())
			return BigInteger.valueOf(0);

		final int omega = gcdPrimeDivisors.size();
		List<Integer> rangeFrom = new ArrayList<Integer>();
		List<Integer> rangeTo = new ArrayList<Integer>();
		for (int i = 0; i < omega; i++) {
			rangeFrom.add(0);
			rangeTo.add(gcdPrimeDivisors.get(i) + 1);
		}
		final ListEnumerator<Integer> lCombEnumerator = enumerator.createInstance(true, rangeFrom, rangeTo);

		BigInteger alphabetMinusOne = BigInteger.valueOf(alphabetSize - 1);
		BigInteger sum = BigInteger.ZERO;
		for (List<Integer> l : lCombEnumerator.enumerate(omega)) {
			BigInteger binomProduct = BigInteger.ONE;
			int primeProduct = 1;
			long lSum = 0;

			for (int i = 0; i < omega; i++) {
                BigInteger binom = binomialCoefficientBigInteger((gcdPrimeDivisors.get(i) + 1), l.get(i));
				binomProduct = binomProduct.multiply(binom);
				primeProduct *= Math.pow(gcdPrimeDivisors.get(i), Math.min(l.get(i), 2));
				lSum += l.get(i);
			}

			if (lSum != 0 && activeCellsNum % primeProduct == 0) {
				final BigInteger powElement = alphabetMinusOne.pow((size * size - activeCellsNum) / primeProduct);
                final BigInteger binom = binomialCoefficientBigInteger(size * size / primeProduct, activeCellsNum / primeProduct);
                final BigInteger partialSum = binomProduct.multiply(binom).multiply(powElement);

                if (lSum % 2 == 0)
					sum = sum.subtract(partialSum);
				else
					sum = sum.add(partialSum);
			}			
		}
		return sum;
	}

	public BigInteger enumerate2(int activeCellsNum) {
		if (activeCellsNum == 0) { 
			return enumerateAll2(alphabetSize - 1);
		}
		final List<Integer> gcdPrimeDivisors = calcPrimeDivisors(ArithmeticUtils.gcd(size, activeCellsNum));
		if (gcdPrimeDivisors.isEmpty())
			return BigInteger.valueOf(0);

		final int omega = gcdPrimeDivisors.size();
		final ListEnumerator<Integer> kCombEnumerator = enumerator.createInstance(true, 0, 2);

		BigInteger alphabetMinusOne = BigInteger.valueOf(alphabetSize - 1);
		BigInteger sum = BigInteger.valueOf(0);
		for (List<Integer> k : kCombEnumerator.enumerate(omega)) {
			int primeProduct = 1;
			long kSum = 0;

			for (int i = 0; i < omega; i++) {
				primeProduct *= Math.pow(gcdPrimeDivisors.get(i), k.get(i));
				kSum += k.get(i);
			}
			if (kSum != 0 && activeCellsNum % primeProduct == 0) {
				final BigInteger powElement = alphabetMinusOne.pow((size * size - activeCellsNum) / primeProduct);
				final BigInteger binomCommon = binomialCoefficientBigInteger(size * size / primeProduct, activeCellsNum / primeProduct);

				BigInteger commonCoef = binomCommon.multiply(powElement);

				List<Integer> rangeFrom = new ArrayList<Integer>();
				List<Integer> rangeTo = new ArrayList<Integer>();
				for (int i = 0; i < omega; i++) {
					int ki = k.get(i);
					rangeFrom.add(ki);
					rangeTo.add((ki < 2) ? ki : gcdPrimeDivisors.get(i) + 1);					
				}
				final ListEnumerator<Integer> lCombEnumerator = enumerator.createInstance(true, rangeFrom, rangeTo);

				BigInteger partialSum = BigInteger.ZERO;
				for (List<Integer> l : lCombEnumerator.enumerate(omega)) {
					BigInteger binomProduct = BigInteger.ONE;
					long lSum = 0;

					for (int i = 0; i < omega; i++) {
						final BigInteger binom = binomialCoefficientBigInteger((gcdPrimeDivisors.get(i) + 1), l.get(i));
						binomProduct = binomProduct.multiply(binom);
						lSum += l.get(i);
					}

					if (lSum % 2 == 0)
						partialSum = partialSum.subtract(binomProduct);
					else
						partialSum = partialSum.add(binomProduct);
				}
				sum = sum.add(commonCoef.multiply(partialSum));
			}
		}
		return sum;
	}

	public BigInteger enumerate3(int activeCellsNum) {
		if (activeCellsNum == 0) {
			return enumerateAll3(alphabetSize - 1);
		}
		final List<Integer> gcdPrimeDivisors = calcPrimeDivisors(ArithmeticUtils.gcd(size, activeCellsNum));
		if (gcdPrimeDivisors.isEmpty())
			return BigInteger.valueOf(0);

		final int omega = gcdPrimeDivisors.size();
		final ListEnumerator<Integer> kCombEnumerator = enumerator.createInstance(true, 0, 2);

		BigInteger alphabetMinusOne = BigInteger.valueOf(alphabetSize - 1);
		BigInteger sum = BigInteger.valueOf(0);
		for (List<Integer> k : kCombEnumerator.enumerate(omega)) {
            int primeProduct = 1;
            int combProduct = 1;
            long kSum = 0;

            for (int i = 0; i < omega; i++) {
                int prime = gcdPrimeDivisors.get(i);
                int exp = k.get(i);

                if (exp != 0) {
                    primeProduct *= Math.pow(prime, exp);

                    if (exp == 1)
                        combProduct *= (prime + 1);
                    else if (exp == 2)
                        combProduct *= prime;
                }
                kSum += exp;
            }

            if (kSum != 0 && activeCellsNum % primeProduct == 0) {
                final BigInteger powElement = alphabetMinusOne.pow((size * size - activeCellsNum) / primeProduct);
                final BigInteger binomCommon = binomialCoefficientBigInteger(size * size / primeProduct, activeCellsNum / primeProduct);

                BigInteger partial = binomCommon.multiply(powElement).multiply(BigInteger.valueOf(combProduct));

                if (kSum % 2 == 1)
                    sum = sum.add(partial);
                else
                    sum = sum.subtract(partial);
            }
		}
		return sum;
	}

    public static BigInteger binomialCoefficientBigInteger(int n, int k) {
        if (n < k || k < 0)
            return BigInteger.ZERO;

        if (n == k && k == 0)
            return BigInteger.ONE;

        if (k == 1 || k == n - 1)
            return BigInteger.valueOf(n);

        if (k > n / 2)
            return binomialCoefficientBigInteger(n, n - k);

        BigInteger result = BigInteger.ONE;

        for (int i = n - k + 1; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        for (int j = 1; j <= k; j++) {
            result = result.divide(BigInteger.valueOf(j));
        }

//        for (int i = 1; i <= k; ++i)
//            result = result.multiply(BigInteger.valueOf((n - k + i) / i));

        return result;
    }
}