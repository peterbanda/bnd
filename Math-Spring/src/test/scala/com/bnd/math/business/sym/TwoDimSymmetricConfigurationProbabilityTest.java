package com.bnd.math.business.sym;

import com.bnd.core.util.FileUtil;
import com.bnd.function.enumerator.ListEnumeratorFactory;
import com.bnd.math.business.MathTest;
import com.bnd.math.business.sym.twodim.TwoDimSymmetricConfigurationEnumerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class TwoDimSymmetricConfigurationProbabilityTest extends MathTest {

	@Autowired
	ListEnumeratorFactory listEnumeratorFactory;

	final FileUtil fu = FileUtil.getInstance();

	final int MIN_ARRAY_SIZE = 2;
	final int MAX_ARRAY_SIZE = 100;

//	@Test
	public void testUniformProb() {
		final int alphabetSize = 2;
		final BigInteger alphabet = BigInteger.valueOf(alphabetSize);
		System.out.println("Uniform");
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			final BigInteger count = enumerator.enumerateAll();
			final BigInteger total = alphabet.pow(arraySize * arraySize);
			BigDecimal ratio = new BigDecimal(count).divide(new BigDecimal(total)); 
//			System.out.println(arraySize + "," + ((double) count / Math.pow(alphabetSize, arraySize)));
			sb.append(arraySize + "," + ratio.toString() + '\n');
			System.out.println(arraySize + "," + ratio.toString());
		}
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricUniform.csv");
	}

//	@Test
	public void testUniformProb2() {
		final int alphabetSize = 2;
		final BigInteger alphabet = BigInteger.valueOf(alphabetSize);
		System.out.println("Uniform2");
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			final BigInteger count = enumerator.enumerateAll2();
			final BigInteger total = alphabet.pow(arraySize * arraySize);
			BigDecimal ratio = new BigDecimal(count).divide(new BigDecimal(total)); 
//			System.out.println(arraySize + "," + ((double) count / Math.pow(alphabetSize, arraySize)));
			sb.append(arraySize + "," + ratio.toString() + '\n');
			System.out.println(arraySize + "," + ratio.toString());
		}
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricUniform2.csv");
	}

//	@Test
	public void testDensityUniformProb() {
		System.out.println("Density Uniform");
		final StringBuilder sb = new StringBuilder();
		final int alphabetSize = 2;
		final BigInteger alphabetMinusOne = BigInteger.valueOf(alphabetSize - 1);
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			BigDecimal ratioSum = BigDecimal.ZERO;
			for (int activeCellsNum = 0; activeCellsNum <= arraySize * arraySize; activeCellsNum++) {
				BigInteger nonActiveTotal = alphabetMinusOne.pow(arraySize * arraySize - activeCellsNum);
				final BigInteger count = enumerator.enumerate(activeCellsNum);
				if (count.doubleValue() != 0) {
					BigInteger total = binomial(arraySize * arraySize, activeCellsNum).multiply(nonActiveTotal);
					BigDecimal partialRatio = new BigDecimal(count).divide(new BigDecimal(total), MathContext.DECIMAL128); 
					ratioSum = ratioSum.add(partialRatio);
				}
			}
			BigDecimal ratio = ratioSum.divide(BigDecimal.valueOf(arraySize * arraySize + 1), MathContext.DECIMAL128);
			System.out.println(arraySize + "," + ratio);
			sb.append(arraySize + "," + ratio + '\n');
		}
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricDensityUniform.csv");
	}

//	@Test
	public void testDensityUniformProb2() {
		System.out.println("Density Uniform2");
		final StringBuilder sb = new StringBuilder();
		final int alphabetSize = 2;
		final BigInteger alphabetMinusOne = BigInteger.valueOf(alphabetSize - 1);
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			BigDecimal ratioSum = BigDecimal.ZERO;
			for (int activeCellsNum = 0; activeCellsNum <= arraySize * arraySize; activeCellsNum++) {
				BigInteger nonActiveTotal = alphabetMinusOne.pow(arraySize * arraySize - activeCellsNum);
				final BigInteger count = enumerator.enumerate2(activeCellsNum);
				if (count.doubleValue() != 0) {
					BigInteger total = binomial(arraySize * arraySize, activeCellsNum).multiply(nonActiveTotal);
					BigDecimal partialRatio = new BigDecimal(count).divide(new BigDecimal(total), MathContext.DECIMAL128); 
					ratioSum = ratioSum.add(partialRatio);
				}
			}
			BigDecimal ratio = ratioSum.divide(BigDecimal.valueOf(arraySize * arraySize + 1), MathContext.DECIMAL128);
			System.out.println(arraySize + "," + ratio);
			sb.append(arraySize + "," + ratio + '\n');
		}
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricDensityUniform2.csv");
	}

	@Test
	public void testDensityUniformProb3() {
		System.out.println("Density Uniform3");
		final StringBuilder sb = new StringBuilder();
		final int alphabetSize = 2;
		final BigInteger alphabetMinusOne = BigInteger.valueOf(alphabetSize - 1);
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			BigDecimal ratioSum = BigDecimal.ZERO;
			for (int activeCellsNum = 0; activeCellsNum <= arraySize * arraySize; activeCellsNum++) {
				BigInteger nonActiveTotal = alphabetMinusOne.pow(arraySize * arraySize - activeCellsNum);
				final BigInteger count = enumerator.enumerate3(activeCellsNum);
				if (count.doubleValue() != 0) {
					BigInteger total = binomial(arraySize * arraySize, activeCellsNum).multiply(nonActiveTotal);
					BigDecimal partialRatio = new BigDecimal(count).divide(new BigDecimal(total), MathContext.DECIMAL128);
					ratioSum = ratioSum.add(partialRatio);
				}
			}
			BigDecimal ratio = ratioSum.divide(BigDecimal.valueOf(arraySize * arraySize + 1), MathContext.DECIMAL128);
			System.out.println(arraySize + "," + ratio);
			sb.append(arraySize + "," + ratio + '\n');
		}
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricDensityUniform3.csv");
	}

	private BigInteger binomial(final int N, final int K) {
	    BigInteger ret = BigInteger.ONE;
	    for (int k = 0; k < K; k++) {
	        ret = ret.multiply(BigInteger.valueOf(N - k)).divide(BigInteger.valueOf(k + 1));
	    }
	    return ret;
	}
}