package com.bnd.math.business.sym.twodim;

import com.bnd.core.util.RandomUtil;
import com.bnd.function.enumerator.ListEnumeratorFactory;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class TwoDimSymmetricLooselyCoupledConfigurationEnumerator {

	private final int size;
	private final int alphabetSize;
	private final ListEnumeratorFactory enumerator;
	private final int radius;
	private final int repetitions;
	private final List<Integer> sizePrimeDivisors; 
	private final TwoDimSymmetricConfigurationEnumerator symEnumerator;

	public TwoDimSymmetricLooselyCoupledConfigurationEnumerator(
		int size,
		int alphabetSize,
		int radius,
		int repetitions,
		ListEnumeratorFactory enumerator
	) {
		this.size = size;
		this.alphabetSize = alphabetSize;
		this.enumerator = enumerator;
		this.radius = radius;
		this.repetitions = repetitions;
		this.sizePrimeDivisors = TwoDimSymmetricConfigurationEnumerator.calcPrimeDivisors(size);
		this.symEnumerator = new TwoDimSymmetricConfigurationEnumerator(size, alphabetSize, enumerator);
	}

	private int calcMaxActiveCellsNum() {
		return size * size / ((2 * radius + 1) * (2 * radius + 1));
	}

	public BigDecimal enumerateAll() {
		int maxActiveCellsNum = calcMaxActiveCellsNum();
		BigDecimal sum = BigDecimal.ZERO;
		for (int activeCellsNum = 0; activeCellsNum <= maxActiveCellsNum; activeCellsNum++) {
			sum = sum.add(enumerate(activeCellsNum));
		}
		return sum;
	}

	public BigDecimal enumerate(int activeCellsNum) {
		BigDecimal symmetricConfigsNum = new BigDecimal(symEnumerator.enumerate(activeCellsNum));
		if (symmetricConfigsNum.equals(BigInteger.ZERO)) 
			return BigDecimal.ZERO;

		if (activeCellsNum == 0)
			return symmetricConfigsNum;

		if (activeCellsNum == 1)
			return BigDecimal.ZERO;

		int maxActiveCells = calcMaxActiveCellsNum();
		if (activeCellsNum > maxActiveCells)
			return BigDecimal.ZERO;

		boolean divisible = false;
		for (int sizePrimeDivisor : sizePrimeDivisors)
			if (activeCellsNum % sizePrimeDivisor == 0)
				divisible = true;

		if (!divisible)
			return BigDecimal.ZERO;

		int looselyCoupledPlacements = 0;
		for (int i = 0; i < repetitions; i++) {
			boolean[][] matrix = new boolean[size][size];
			List<int[]> positions = createRandomSymmetricConfig(matrix, activeCellsNum);
			if (isLooselyCoupled(matrix, positions))
				looselyCoupledPlacements++;
		}

		return BigDecimal.valueOf(((double) looselyCoupledPlacements / repetitions)).multiply(symmetricConfigsNum);
	}

	private boolean isLooselyCoupled(boolean[][] matrix, List<int[]> locations) {
		for (int[] location : locations) {
			int x = location[0];
			int y = location[1];
			for (int i = -radius; i <= radius; i++)
			for (int j = -radius; j <= radius; j++)
				if (i != 0 && j != 0) {
					int xa = (x + i) % size;
					if (xa < 0) xa = size + xa;

					int ya = (y + j) % size;
					if (ya < 0) ya = size + ya;

					if (matrix[xa][ya])
						return false;
				}
		}

		return true;
	}

	private List<int[]> createRandomSymmetricConfig(boolean[][] matrix, int activeCellsNum) {
		List<int[]> locations = new ArrayList<int[]>();
		int groupSize = 0;
		int vx = RandomUtil.nextInt(size);
		int vy = RandomUtil.nextInt(size);

		do {
			vx = RandomUtil.nextInt(size);
			vy = RandomUtil.nextInt(size);
			groupSize = size / ArithmeticUtils.gcd(vx, ArithmeticUtils.gcd(vy, size));
		} while (activeCellsNum % groupSize != 0);

		int cellsPlaced = 0;

		while (cellsPlaced < activeCellsNum) {
			int xstart = 0;
			int ystart = 0;
			do {
				xstart = RandomUtil.nextInt(size);
				ystart = RandomUtil.nextInt(size);
			} while (matrix[xstart][ystart]);

			int x = xstart;
			int y = ystart;
			do {
				matrix[x][y] = true;
				locations.add(new int[]{x,y});
				cellsPlaced++;
				x += vx;
				y += vy;
				x = x % size;
				y = y % size;
			} while (x != xstart || y != ystart);
		}
		return locations;
	}
}