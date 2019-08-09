package com.bnd.math.business.sym;

import com.bnd.core.util.FileUtil;
import com.bnd.function.enumerator.ListEnumeratorFactory;
import com.bnd.math.business.MathTest;
import com.bnd.math.business.sym.twodim.TwoDimSymmetricLooselyCoupledConfigurationEnumerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class TwoDimSymmetricLooselyCoupledConfigurationEnumeratorTest extends MathTest {

	@Autowired
	ListEnumeratorFactory listEnumeratorFactory;

	final FileUtil fu = FileUtil.getInstance();

	final int MAX_ARRAY_SIZE = 40;
	final int REPETITIONS = 30000;

//	@Test
	public void testUniformAllInsolvable() {
		final int alphabetSize = 2;
		final int radius = 1;
		System.out.println("Insolvable Uniform: alphabet size " + alphabetSize + ", radius " + radius);
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = 2; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			final TwoDimSymmetricLooselyCoupledConfigurationEnumerator enumerator = new TwoDimSymmetricLooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius, REPETITIONS, listEnumeratorFactory);
			BigDecimal count = enumerator.enumerateAll();
			// exclude configurations with one active cell, which is solvable
			count = count.subtract(enumerator.enumerate(1));
			BigDecimal total = BigDecimal.valueOf(alphabetSize).pow(arraySize * arraySize);
			BigDecimal ratio = count.divide(total);
			System.out.println(arraySize + "," + ratio);
			sb.append(arraySize + "," + ratio + '\n');
//			System.out.println(arraySize + " : " + count);
		}
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricLooselyCoupledUniform.csv");
	}

	@Test
	public void testDensityUniformAllInsolvable() {
		final int alphabetSize = 2;
		final int radius = 1;
		System.out.println("Insolvable Density Uniform: alphabet size " + alphabetSize + ", radius " + radius);
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = 2; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			final TwoDimSymmetricLooselyCoupledConfigurationEnumerator enumerator = new TwoDimSymmetricLooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius, REPETITIONS, listEnumeratorFactory);
			BigDecimal partialRatioSum = BigDecimal.ZERO;
			for (int activeCellsNum = 0; activeCellsNum <= arraySize * arraySize; activeCellsNum++) {
				final BigDecimal count = enumerator.enumerate(activeCellsNum);
				// exclude configurations with one active cell, which is solvable
				if (activeCellsNum != 1 && !count.equals(BigDecimal.ZERO)) {
					BigDecimal nonActiveTotal = BigDecimal.valueOf(alphabetSize - 1).pow(arraySize * arraySize - activeCellsNum);
					BigInteger binom = binomial(arraySize * arraySize, activeCellsNum);
					BigDecimal partialRatio = count.divide(new BigDecimal(binom).multiply(nonActiveTotal), MathContext.DECIMAL128);
					partialRatioSum = partialRatioSum.add(partialRatio);
				}
			}
			BigDecimal ratio = partialRatioSum.divide(BigDecimal.valueOf(arraySize * arraySize + 1), MathContext.DECIMAL128);
			System.out.println(arraySize + "," + ratio);
			sb.append(arraySize + "," + ratio + '\n');
		}
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricLooselyCoupledDensityUniform.csv");
	}

	private BigInteger binomial(final int N, final int K) {
	    BigInteger ret = BigInteger.ONE;
	    for (int k = 0; k < K; k++) {
	        ret = ret.multiply(BigInteger.valueOf(N - k)).divide(BigInteger.valueOf(k + 1));
	    }
	    return ret;
	}
}