package com.bnd.math.business.sym;

import com.bnd.core.util.FileUtil;
import com.bnd.math.business.MathTest;
import com.bnd.math.business.sym.onedim.LooselyCoupledConfigurationEnumerator;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.junit.Test;

public class LooselyCoupledConfigurationEnumeratorTest extends MathTest {

	final FileUtil fu = FileUtil.getInstance();
	final int MAX_ARRAY_SIZE = 200;

	@Test
	public void testUniformAllInsolvable() {
		final int alphabetSize = 2;
		final int radius = 3;
		System.out.println("Insolvable Uniform: alphabet size " + alphabetSize + ", radius " + radius);
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = 2; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			final LooselyCoupledConfigurationEnumerator enumerator = new LooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius);
			long count = enumerator.enumerateAll();
			// exclude configurations with one active cell, which is solvable
			count -= enumerator.enumerate(1);
			System.out.println(arraySize + "," + ((double) count / Math.pow(alphabetSize, arraySize)));
			sb.append(arraySize + "," + ((double) count / Math.pow(alphabetSize, arraySize)) + '\n');
//			System.out.println(arraySize + " : " + count);
		}
		fu.overwriteStringToFileSafe(sb.toString(), "looselyCoupledUniform.csv");
	}

	@Test
	public void testDensityUniformAllInsolvable() {
		final int alphabetSize = 2;
		final int radius = 3;
		System.out.println("Insolvable Density Uniform: alphabet size " + alphabetSize + ", radius " + radius);
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = 2; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			final LooselyCoupledConfigurationEnumerator enumerator = new LooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius);
			double probabilitySum = 0;
			for (int activeCellsNum = 0; activeCellsNum <= arraySize; activeCellsNum++) {
				final long count = enumerator.enumerate(activeCellsNum);
				// exclude configurations with one active cell, which is solvable
				if (activeCellsNum != 1 && count != 0)
					probabilitySum += ((double) count / ((ArithmeticUtils.binomialCoefficientDouble(arraySize, activeCellsNum) * Math.pow(alphabetSize - 1, (arraySize - activeCellsNum)))));
			}
			System.out.println(arraySize + "," + (double) probabilitySum / (arraySize + 1));
			sb.append(arraySize + "," + (double) probabilitySum / (arraySize + 1) + '\n');
		}
		fu.overwriteStringToFileSafe(sb.toString(), "looselyCoupledDensityUniform.csv");
	}
}