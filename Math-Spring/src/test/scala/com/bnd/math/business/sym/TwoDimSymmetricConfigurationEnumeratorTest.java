package com.bnd.math.business.sym;

import com.bnd.core.util.FileUtil;
import com.bnd.function.enumerator.ListEnumeratorFactory;
import com.bnd.math.business.MathTest;
import com.bnd.math.business.sym.twodim.TwoDimSymmetricConfigurationEnumerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;

public class TwoDimSymmetricConfigurationEnumeratorTest extends MathTest {

	@Autowired
	ListEnumeratorFactory listEnumeratorFactory;

	final FileUtil fu = FileUtil.getInstance();

	final int MIN_ARRAY_SIZE = 2;
	final int MAX_ARRAY_SIZE = 100;

//	@Test
	public void testEnumerator() {
		final int arraySize = 3;
		final int activeCellsNum = 3;
		final int alphabetSize = 2;
		TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
		final BigInteger count = enumerator.enumerate(activeCellsNum);
		final BigInteger count2 = enumerator.enumerate2(activeCellsNum);
		System.out.println("array size " + arraySize + ", alphabet size " + alphabetSize + ": " + count);
		System.out.println("array size " + arraySize + ", alphabet size " + alphabetSize + ": " + count2);
	}

	@Test
	public void testUniformCount() {
		final int alphabetSize = 2;
		System.out.println("Uniform");
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			final BigInteger count = enumerator.enumerateAll();
			System.out.println(arraySize + "," + count.toString());
            sb.append(arraySize + "," + count.toString() + '\n');
        }
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricUniformCount.csv");
	}

	@Test
	public void testUniformCount2() {
		final int alphabetSize = 2;
		System.out.println("Uniform2");
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			final BigInteger count = enumerator.enumerateAll2();
			System.out.println(arraySize + "," + count.toString());
            sb.append(arraySize + "," + count.toString() + '\n');
        }
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricUniformCount2.csv");
	}

	@Test
	public void testUniformCount3() {
		final int alphabetSize = 2;
		System.out.println("Uniform3");
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			final BigInteger count = enumerator.enumerateAll3();
			System.out.println(arraySize + "," + count.toString());
			sb.append(arraySize + "," + count.toString() + '\n');
		}
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricUniformCount3.csv");
	}

	public void testDensityUniformCount() {
		final int alphabetSize = 2;
        System.out.println("Density Uniform");
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
            TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
            BigInteger count = BigInteger.ZERO;
            for (int activeCellsNum = 0; activeCellsNum <= arraySize * arraySize; activeCellsNum++) {
                final BigInteger partialCount = enumerator.enumerate(activeCellsNum);
                if (partialCount.compareTo(BigInteger.ZERO) < 0) {
                    System.out.println("Sum bellow zero: " + arraySize + ", " + activeCellsNum + ": " + partialCount);
                }
                count = count.add(partialCount);
            }
			System.out.println(arraySize + "," + count.toString());
            sb.append(arraySize + "," + count.toString() + '\n');
        }
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricDensityUniformCount.csv");
	}

    public void testDensityUniformCount2() {
		final int alphabetSize = 2;
		System.out.println("Density Uniform2");
        final StringBuilder sb = new StringBuilder();
        for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
            TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
            BigInteger count = BigInteger.ZERO;
            for (int activeCellsNum = 0; activeCellsNum <= arraySize * arraySize; activeCellsNum++) {
                final BigInteger partialCount = enumerator.enumerate2(activeCellsNum);
                count = count.add(partialCount);
            }
            System.out.println(arraySize + "," + count.toString());
            sb.append(arraySize + "," + count.toString() + '\n');
        }
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricDensityUniformCount2.csv");
	}

    @Test
    public void testDensityUniformCount3() {
		final int alphabetSize = 2;
		System.out.println("Density Uniform3");
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationEnumerator enumerator = new TwoDimSymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			BigInteger count = BigInteger.ZERO;
			for (int activeCellsNum = 0; activeCellsNum <= arraySize * arraySize; activeCellsNum++) {
				final BigInteger partialCount = enumerator.enumerate3(activeCellsNum);
				count = count.add(partialCount);
			}
			System.out.println(arraySize + "," + count.toString());
			sb.append(arraySize + "," + count.toString() + '\n');
		}
		fu.overwriteStringToFileSafe(sb.toString(), "twoDimSymmetricDensityUniformCount3.csv");
	}
}