package com.bnd.math.business.sym.twodim;

import com.bnd.core.util.RandomUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

public class TwoDimLooselyCoupledConfigurationSampleEnumerator {

	private final int size;      // N
	private final int alphabetSize;   // \Sigma
	private final int radius;         // r
	private final int repetitions;
	private final Collection<int[]> positions;

	public TwoDimLooselyCoupledConfigurationSampleEnumerator(
		int size,
		int alphabetSize,
		int radius,
		int repetitions
	) {
		this.size = size;
		this.alphabetSize = alphabetSize;
		this.radius = radius;
		this.repetitions = repetitions;
		this.positions = new ArrayList<int[]>();
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				positions.add(new int[]{x,y});
	}

	private int calcMaxActiveCellsNum() {
		return size * size / ((2 * radius + 1) * (2 * radius + 1));
	}

	public BigDecimal enumerate(int activeCellsNum) {
		if (activeCellsNum > calcMaxActiveCellsNum()) {
			return BigDecimal.ZERO;
		}
		if (activeCellsNum == 0) {
			return BigDecimal.valueOf(alphabetSize - 1).pow(size * size);
		}

		int looselyCoupledPlacements = 0;
		for (int i = 0; i < repetitions; i++) {
			if (isRandomLooselyCoupled(activeCellsNum))
				looselyCoupledPlacements++;
		}

		BigInteger total = binomial(size * size, activeCellsNum);
		BigDecimal placementsNum = BigDecimal.valueOf(((double) looselyCoupledPlacements / repetitions)).multiply(new BigDecimal(total));
		BigInteger pow = BigInteger.valueOf(alphabetSize - 1).pow(size * size - activeCellsNum);
		return placementsNum.multiply(new BigDecimal(pow));
	}

	public BigDecimal enumerateAll() {
		int maxActiveCellsNum = calcMaxActiveCellsNum();
		BigDecimal sum = BigDecimal.ZERO;
		for (int activeCellsNum = 0; activeCellsNum <= maxActiveCellsNum; activeCellsNum++) {
			sum = sum.add(enumerate(activeCellsNum));
		}
		return sum;
	}

	private boolean isRandomLooselyCoupled(int activeCellsNum) {
		boolean[][] matrix = new boolean[size][size];
		Collection<int[]> selectedPositions = RandomUtil.nextElementsWithoutRepetitions(positions, activeCellsNum);
		for (int[] position : selectedPositions) {
			boolean overlap = placeNeighborhood(matrix, position[0], position[1]);
			if (overlap)
				return false;
		}
		return true;
	}

	private boolean placeNeighborhood(boolean[][] matrix, int x, int y) {
		for (int i = -radius; i <= radius; i++)
			for (int j = -radius; j <= radius; j++) {
				int xa = (x + i) % size;
				if (xa < 0) xa = size + xa;

				int ya = (y + j) % size;
				if (ya < 0) ya = size + ya;

				if (matrix[xa][ya])
					return true;
				matrix[xa][ya] = true;
			}
			
		return false;
	}

	private BigInteger binomial(final int N, final int K) {
	    BigInteger ret = BigInteger.ONE;
	    for (int k = 0; k < K; k++) {
	        ret = ret.multiply(BigInteger.valueOf(N - k)).divide(BigInteger.valueOf(k + 1));
	    }
	    return ret;
	}	
}