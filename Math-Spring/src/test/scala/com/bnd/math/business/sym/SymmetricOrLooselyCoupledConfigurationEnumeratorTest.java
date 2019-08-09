package com.bnd.math.business.sym;

import com.bnd.core.util.FileUtil;
import com.bnd.function.enumerator.ListEnumeratorFactory;
import com.bnd.math.business.MathTest;
import com.bnd.math.business.sym.onedim.LooselyCoupledConfigurationEnumerator;
import com.bnd.math.business.sym.onedim.SymmetricConfigurationEnumerator;
import com.bnd.math.business.sym.onedim.SymmetricLooselyCoupledConfigurationEnumerator;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SymmetricOrLooselyCoupledConfigurationEnumeratorTest extends MathTest {

	@Autowired
	ListEnumeratorFactory listEnumeratorFactory;

	final FileUtil fu = FileUtil.getInstance();

	final int MAX_ARRAY_SIZE = 200;

	public void testUniformAllInsolvable() {
		final int alphabetSize = 2;
		final int radius = 3;
		System.out.println("Insolvable Uniform: alphabet size " + alphabetSize + ", radius " + radius);
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = 2; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			final SymmetricConfigurationEnumerator symmetricEnumerator = new SymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			final LooselyCoupledConfigurationEnumerator looselyCoupledEnumerator = new LooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius);
			final SymmetricLooselyCoupledConfigurationEnumerator intersectEnumerator = new SymmetricLooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius, listEnumeratorFactory);
			long count = symmetricEnumerator.enumerateAll() + looselyCoupledEnumerator.enumerateAll() - intersectEnumerator.enumerateAll();
			// exclude loosely-coupled configurations with one active cell, which is solvable
			count -= looselyCoupledEnumerator.enumerate(1);
			System.out.println(arraySize + "," + ((double) count / Math.pow(alphabetSize, arraySize)));
			sb.append(arraySize + "," + ((double) count / Math.pow(alphabetSize, arraySize)) + '\n');
//			System.out.println(arraySize + " : " + count);
		}
		fu.overwriteStringToFileSafe(sb.toString(), "symmetricOrLooselyCoupledUniform.csv");
	}

	public void testDensityUniformAllInsolvable() {
		final int alphabetSize = 2;
		final int radius = 3;
		System.out.println("Insolvable Density Uniform: alphabet size " + alphabetSize + ", radius " + radius);
		final StringBuilder sb = new StringBuilder();
		for (int arraySize = 2; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			final SymmetricConfigurationEnumerator symmetricEnumerator = new SymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			final LooselyCoupledConfigurationEnumerator looselyCoupledEnumerator = new LooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius);
			final SymmetricLooselyCoupledConfigurationEnumerator intersectEnumerator = new SymmetricLooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius, listEnumeratorFactory);

			double probabilitySum = 0;
			for (int activeCellsNum = 0; activeCellsNum <= arraySize; activeCellsNum++) {
				final long count = symmetricEnumerator.enumerate(activeCellsNum) + looselyCoupledEnumerator.enumerate(activeCellsNum) - intersectEnumerator.enumerate(activeCellsNum);
				// exclude configurations with one active cell, which is solvable
				if (activeCellsNum != 1 && count != 0)
					probabilitySum += ((double) count / ((ArithmeticUtils.binomialCoefficientDouble(arraySize, activeCellsNum) * Math.pow(alphabetSize - 1, (arraySize - activeCellsNum)))));
			}
			System.out.println(arraySize + "," + (double) probabilitySum / (arraySize + 1));
			sb.append(arraySize + "," + (double) probabilitySum / (arraySize + 1) + '\n');
		}
		fu.overwriteStringToFileSafe(sb.toString(), "symmetricOrLooselyCoupledDensityUniform.csv");
	}

	@Test
	public void testUniformNForPaper() {
		final int alphabetSize = 2;
		final int radius = 3;
		int[] arraySizes = new int[] {149, 593, 1001, 1301};
		System.out.println("Uniform for paper");
		for (int arraySize : arraySizes) {
			final SymmetricConfigurationEnumerator symmetricEnumerator = new SymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			final LooselyCoupledConfigurationEnumerator looselyCoupledEnumerator = new LooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius);
			final SymmetricLooselyCoupledConfigurationEnumerator intersectEnumerator = new SymmetricLooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius, listEnumeratorFactory);
			long count = symmetricEnumerator.enumerateAll() + looselyCoupledEnumerator.enumerateAll() - intersectEnumerator.enumerateAll();
			// exclude loosely-coupled configurations with one active cell, which is solvable
			count -= looselyCoupledEnumerator.enumerate(1);
			System.out.println(arraySize + "," + ((double) count / Math.pow(alphabetSize, arraySize)));
//			System.out.println(arraySize + " : " + count);
		}
	}

	@Test
	public void testDensityUniformNForPaper() {
		final int alphabetSize = 2;
		final int radius = 3;
		int[] arraySizes = new int[] {149, 593, 1001, 1301};
		System.out.println("Density Uniform for paper");
		for (int arraySize : arraySizes) {
			final SymmetricConfigurationEnumerator symmetricEnumerator = new SymmetricConfigurationEnumerator(arraySize, alphabetSize, listEnumeratorFactory);
			final LooselyCoupledConfigurationEnumerator looselyCoupledEnumerator = new LooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius);
			final SymmetricLooselyCoupledConfigurationEnumerator intersectEnumerator = new SymmetricLooselyCoupledConfigurationEnumerator(arraySize, alphabetSize, radius, listEnumeratorFactory);

			double probabilitySum = 0;
			for (int activeCellsNum = 0; activeCellsNum <= arraySize; activeCellsNum++) {
				final long count = symmetricEnumerator.enumerate(activeCellsNum) + looselyCoupledEnumerator.enumerate(activeCellsNum) - intersectEnumerator.enumerate(activeCellsNum);
				// exclude configurations with one active cell, which is solvable
				if (activeCellsNum != 1 && count != 0)
					probabilitySum += ((double) count / ((ArithmeticUtils.binomialCoefficientDouble(arraySize, activeCellsNum) * Math.pow(alphabetSize - 1, (arraySize - activeCellsNum)))));
			}
			System.out.println(arraySize + "," + (double) probabilitySum / (arraySize + 1));
		}
	}
}