package com.bnd.math.business.sym;

import com.bnd.core.util.FileUtil;
import com.bnd.function.enumerator.ListEnumeratorFactory;
import com.bnd.math.business.MathTest;
import com.bnd.math.business.sym.onedim.SymmetricConfigurationEnumerator;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SymmetricConfigurationEnumeratorTest extends MathTest {

	@Autowired
	ListEnumeratorFactory listEnumeratorFactory;

	final FileUtil fu = FileUtil.getInstance();

	final int MAX_ARRAY_SIZE = 200;

//	@Test
	public void testEnumerator() {
		final int arraySize = 2;
		final int alphabetSize = 2;
		SymmetricConfigurationEnumerator enumerator = new SymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
		final long count = enumerator.enumerateAll();
		System.out.println("array size " + arraySize + ", alphabet size " + alphabetSize + ": " + count);
	}

	@Test
	public void testUniformAll() {
		final int alphabetSize = 2;
		System.out.println("Uniform");
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = 2; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			SymmetricConfigurationEnumerator enumerator = new SymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			final long count = enumerator.enumerateAll();
			System.out.println(arraySize + "," + ((double) count / Math.pow(alphabetSize, arraySize)));
			sb.append(arraySize + "," + ((double) count / Math.pow(alphabetSize, arraySize)) + '\n');
//			System.out.println(arraySize + " : " + count);
		}
		fu.overwriteStringToFileSafe(sb.toString(), "symmetricUniform.csv");
	}

	@Test
	public void testUniformActiveCells() {
		System.out.println("Density Uniform");
		final StringBuilder sb = new StringBuilder();
		final int alphabetSize = 2;
		for (int arraySize = 2; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			SymmetricConfigurationEnumerator enumerator = new SymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			double probabilitySum = 0;
			for (int activeCellsNum = 0; activeCellsNum <= arraySize; activeCellsNum++) {
				final long count = enumerator.enumerate(activeCellsNum);
				if (count != 0)
					probabilitySum += ((double) count / (ArithmeticUtils.binomialCoefficientDouble(arraySize, activeCellsNum) * Math.pow(alphabetSize - 1, (arraySize - activeCellsNum))));
//			System.out.println(arraySize + "," + ((double) count / Math.pow(alphabetSize, arraySize)));
//			System.out.println(arraySize + ", " + activeCellsNum + " : " + count);
			}
			System.out.println(arraySize + "," + (double) probabilitySum / (arraySize + 1));
			sb.append(arraySize + "," + (double) probabilitySum / (arraySize + 1) + '\n');
		}
		fu.overwriteStringToFileSafe(sb.toString(), "symmetricDensityUniform.csv");
	}
}