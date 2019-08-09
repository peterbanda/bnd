package com.bnd.math.business.sym.onedim;

import org.apache.commons.math3.util.ArithmeticUtils;

public class LooselyCoupledConfigurationEnumerator {

	private final int arraySize;      // N
	private final int alphabetSize;   // \Sigma
	private final int radius;         // r

	public LooselyCoupledConfigurationEnumerator(
		int arraySize,
		int alphabetSize,
		int radius
	) {
		this.arraySize = arraySize;
		this.alphabetSize = alphabetSize;
		this.radius = radius;
	}

	private int calcFreeCellsNum(int activeCellsNum) {
		return arraySize - activeCellsNum * (2 * radius + 1);
	}

	private int calcMaxActiveCellsNum() {
		return arraySize / (2 * radius + 1);
	}

	public long enumerate(int activeCellsNum) {
		if (activeCellsNum > calcMaxActiveCellsNum()) {
			return 0;
		}
		final int freeCellsNum = calcFreeCellsNum(activeCellsNum);
		if (activeCellsNum == 0) {
			return (long) Math.pow(alphabetSize - 1, arraySize);
		}
		long placementsNum = (long) (ArithmeticUtils.binomialCoefficientDouble(activeCellsNum + freeCellsNum - 1, activeCellsNum - 1) * (2 * radius + 1));
		if (freeCellsNum != 0) {
			placementsNum += (long) (ArithmeticUtils.binomialCoefficientDouble(activeCellsNum + freeCellsNum - 1, activeCellsNum));			
		}
		return (long) (placementsNum * Math.pow(alphabetSize - 1, arraySize - activeCellsNum));
	}

	public long enumerateAll() {
		int maxActiveCellsNum = calcMaxActiveCellsNum();
		long sum = 0;
		for (int activeCellsNum = 0; activeCellsNum <= maxActiveCellsNum; activeCellsNum++) {
			sum += enumerate(activeCellsNum);
		}
		return sum;
	}
}