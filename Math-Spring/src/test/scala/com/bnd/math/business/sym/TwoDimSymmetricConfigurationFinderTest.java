package com.bnd.math.business.sym;

import com.bnd.core.util.FileUtil;
import com.bnd.function.enumerator.ListEnumeratorFactory;
import com.bnd.math.BndMathException;
import com.bnd.math.business.MathTest;
import com.bnd.math.business.sym.onedim.SymmetricConfigurationEnumerator;
import com.bnd.math.business.sym.twodim.TwoDimCyclicGroup;
import com.bnd.math.business.sym.twodim.TwoDimCyclicGroupComparators.TwoDimCyclicGroupYXComparator;
import com.bnd.math.business.sym.twodim.TwoDimSymmetricConfigurationFinder;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class TwoDimSymmetricConfigurationFinderTest extends MathTest {

	@Autowired
	ListEnumeratorFactory listEnumeratorFactory;

	final FileUtil fu = FileUtil.getInstance();

	final int MIN_ARRAY_SIZE = 3;
	final int MAX_ARRAY_SIZE = 50;

	public static class Matrix {
		int[][] m;

		public Matrix(int x1, int x2, int y1, int y2) {
			m = new int[][]{{x1,x2},{y1,y2}};
		}

		public void swap(int x, int y) {
			if (x == 1 && y == 1) {
				swap(1, 1, 0, 0);
				swap(0, 1, 1, 0);
			} else if (x == 0 && y == 1) {
				swap(0, 1, 0, 0);
				swap(1, 1, 1, 0);
			} else if (x == 1 && y == 0) {
				swap(1, 0, 0, 0);
				swap(0, 1, 1, 1);
			} else if (x == 0 && y == 0) {
				// no-op
			}
		}

		public void div(int num) {
			m[0][0] = m[0][0] / num;
			m[1][0] = m[1][0] / num;
			m[0][1] = m[0][1] / num;
			m[1][1] = m[1][1] / num;
		}

		public void mult(int num) {
			m[0][0] = m[0][0] * num;
			m[1][0] = m[1][0] * num;
			m[0][1] = m[0][1] * num;
			m[1][1] = m[1][1] * num;
		}

		public int determinant() {
			return m[0][0] * m[1][1] - m[0][1] * m[1][0];
		}

		private void swap(int x1, int y1, int x2, int y2) {
			int z = m[x1][y1];
			m[x1][y1] = m[x2][y2];
			m[x2][y2] = z;
		}
	}

//	@Test
	public void testEnumerateAllVectors() {
		int alphabetSize = 2;
//		final StringBuilder sb = new StringBuilder();
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationFinder enumerator = new TwoDimSymmetricConfigurationFinder(arraySize, listEnumeratorFactory);
			final Map<TwoDimCyclicGroup, List<TwoDimCyclicGroup>> cyclicGroupGroupMap = enumerator.findAllVectors();

			List<TwoDimCyclicGroup> cyclicGroups = new ArrayList<TwoDimCyclicGroup>();
			cyclicGroups.addAll(cyclicGroupGroupMap.keySet());
			Collections.sort(cyclicGroups, new TwoDimCyclicGroupYXComparator());

			System.out.println("SIZE " + arraySize + "\n");
			for (TwoDimCyclicGroup reprSymSpace : cyclicGroups) {
				List<TwoDimCyclicGroup> group = cyclicGroupGroupMap.get(reprSymSpace);
				System.out.println("Repr:" + reprSymSpace.x + "," + reprSymSpace.y + " (" + reprSymSpace.order + "), [" + group.size() + "].");
//				System.out.println(reprSymSpace.toString());
//				System.out.println("-----------------------------");
//				for (SymSpace cyclicGroup : group) {
//					System.out.println(cyclicGroup.x + "," + cyclicGroup.y + "(" + cyclicGroup.imagesNum + ")");
//					System.out.println(cyclicGroup.toString());					
//				}
//				System.out.println("-----------------------------");
			}
			System.out.println("\n");
//			sb.append(arraySize + "," + ((double) count / Math.pow(alphabetSize, arraySize)) + '\n');
//			System.out.println(arraySize + " : " + count);
		}
//		fu.overwriteStringToFileSafe(sb.toString(), "symmetricUniform.csv");
	}

	@Test
	public void testEnumerateAllBaseVectors() {
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationFinder enumerator = new TwoDimSymmetricConfigurationFinder(arraySize, listEnumeratorFactory);
			final Map<TwoDimCyclicGroup, List<TwoDimCyclicGroup>> cyclicGroupGroupMap = enumerator.findAllBaseVectors();

			List<TwoDimCyclicGroup> cyclicGroups = new ArrayList<TwoDimCyclicGroup>();
			cyclicGroups.addAll(cyclicGroupGroupMap.keySet());
			Collections.sort(cyclicGroups, new TwoDimCyclicGroupYXComparator());

			List<Integer> primeFactors = SymmetricConfigurationEnumerator.calcPrimeDivisors(arraySize);
			System.out.println("SIZE " + arraySize + " primes: " + primeFactors + ", gens: " + cyclicGroups.size());

			for (TwoDimCyclicGroup reprSymSpace : cyclicGroups) {
				List<TwoDimCyclicGroup> group = cyclicGroupGroupMap.get(reprSymSpace);
				System.out.println("Repr:" + reprSymSpace.x + "," + reprSymSpace.y + " (" + reprSymSpace.order + ")");
				System.out.println("-----------------------------");
				for (TwoDimCyclicGroup cyclicGroup : group) {
					System.out.println(cyclicGroup.x + "," + cyclicGroup.y + "(" + cyclicGroup.order + ")");
					int symCount = cyclicGroup.n * cyclicGroup.n / cyclicGroup.order;
					if (cyclicGroup.calcSymConfigCount() != symCount)
						throw new BndMathException("Mismatch of cyclic group sym count '" + cyclicGroup.calcSymConfigCount() + "' vs. '" + symCount + "'.");

					if (cyclicGroup.calcOrder() != cyclicGroup.order)
						throw new BndMathException("Mismatch of cyclic group order '" + cyclicGroup.calcOrder() + "' vs. '" + cyclicGroup.order + "'.");
				}
				System.out.println("-----------------------------");
			}
			System.out.println("\n");
		}
	}

//	@Test
	public void testMultSize() {
		int alphabetSize = 2;
//		final StringBuilder sb = new StringBuilder();
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			TwoDimSymmetricConfigurationFinder enumerator = new TwoDimSymmetricConfigurationFinder(arraySize, listEnumeratorFactory);
			final Collection<List<TwoDimCyclicGroup>> groupedCyclicGroups = enumerator.findAllVectors().values();
			final Collection<TwoDimCyclicGroup> cyclicGroups = new ArrayList<TwoDimCyclicGroup>();
			for (List<TwoDimCyclicGroup> cyclicGroups2 : groupedCyclicGroups) {
				cyclicGroups.addAll(cyclicGroups2);
			}

			for (TwoDimCyclicGroup cyclicGroup1 : cyclicGroups)
				for (TwoDimCyclicGroup cyclicGroup2 : cyclicGroups) {
					final TwoDimCyclicGroup multGroup = cyclicGroup1.mult(cyclicGroup2);
//					int lcmX = ArithmeticUtils.lcm(ArithmeticUtils.gcd(cyclicGroup1.x, arraySize), ArithmeticUtils.gcd(cyclicGroup2.x, arraySize));
//					int lcmY = ArithmeticUtils.lcm(ArithmeticUtils.gcd(cyclicGroup1.y, arraySize), ArithmeticUtils.gcd(cyclicGroup2.y, arraySize));

					int gcdX = ArithmeticUtils.gcd(cyclicGroup1.x, cyclicGroup2.x);
					int gcdY = ArithmeticUtils.gcd(cyclicGroup1.y, cyclicGroup2.y);

//					int lcmX = ArithmeticUtils.lcm(arraySize / ArithmeticUtils.gcd(cyclicGroup1.x, arraySize), arraySize / ArithmeticUtils.gcd(cyclicGroup2.x, arraySize));
//					int lcmY = ArithmeticUtils.lcm(arraySize / ArithmeticUtils.gcd(cyclicGroup1.y, arraySize), arraySize / ArithmeticUtils.gcd(cyclicGroup2.y, arraySize));

//					int lcmX = ArithmeticUtils.lcm(cyclicGroup1.x, cyclicGroup2.x);
//					int lcmY = ArithmeticUtils.lcm(cyclicGroup1.y, cyclicGroup2.y);

//					int x = (ArithmeticUtils.lcm(lcmX, lcmY) * cyclicGroup1.x) % arraySize;
//					int y = (ArithmeticUtils.lcm(lcmX, lcmY) * cyclicGroup1.y) % arraySize;

					Matrix matrix = new Matrix(
							cyclicGroup1.x, (arraySize - cyclicGroup2.x),
							cyclicGroup1.y, (arraySize - cyclicGroup2.y));

						int gcdCommon = gcd(new int[]{matrix.m[0][0], matrix.m[1][0], matrix.m[0][1], matrix.m[1][1], arraySize});

					int n = arraySize;
						int pivotX = 0;
						int pivotY = 0;
						if (ArithmeticUtils.gcd(matrix.m[0][0], arraySize) > 1)
							if (ArithmeticUtils.gcd(matrix.m[0][1], arraySize) > 1)
								if (ArithmeticUtils.gcd(matrix.m[1][0], arraySize) > 1)
									if (ArithmeticUtils.gcd(matrix.m[1][1], arraySize) > 1) {
										matrix.div(gcdCommon);
										n = (n / gcdCommon);
										// TODO
									} else {
//										pivotX = 1;
//										pivotY = 1;
//										matrix.swap(1,1);
									}	
								else {
//									pivotX = 1;
//									pivotY = 0;
//									matrix.swap(1,0);
								}
							else {
//								pivotX = 0;
//								pivotY = 1;
//								matrix.swap(0,1);
							}

					int determinant = matrix.determinant();
					determinant = determinant % n;
					if (determinant < 0)
						determinant = (n + determinant);
//					if (matrix2.determinant() == 0) 
//						determinant = 0;

					int intersectX =  0;
					int intersectY = 0;
					int c2coef = 0;
					int c2 = 0;
					TwoDimCyclicGroup intersectionGroup = null;

					int intersectionOrder = Integer.MAX_VALUE;
						if (determinant == 0) {
							intersectX =  ArithmeticUtils.gcd(arraySize, ArithmeticUtils.lcm(cyclicGroup1.x, cyclicGroup2.x));
							intersectY = ArithmeticUtils.gcd(arraySize, ArithmeticUtils.lcm(cyclicGroup1.y, cyclicGroup2.y));
							intersectionGroup = new TwoDimCyclicGroup(intersectX, intersectY, arraySize);
							intersectionOrder = intersectionGroup.order;
						} else {
							c2coef = determinant;
//							if (matrix.m[0][0] == matrix.m[1][0])
//								c2coef = (matrix.m[1][1] - matrix.m[0][1]) % n;
//							if (c2coef < 0)
//								c2coef = (n + c2coef);

							if (determinant == 1)
								c2 = 0;
							else {
								matrix.mult(gcdCommon);
								int gcdC = gcd(new int[]{matrix.m[0][0], matrix.m[1][0], matrix.m[0][1], matrix.m[1][1]});								

								determinant = matrix.determinant();
								System.out.println(determinant);

								int remainder = 0;
								if (gcdC > 0 && determinant == 0) {
									determinant = arraySize / gcdC;
									remainder = arraySize % gcdC;
									System.out.println(determinant);
								} else {
									determinant = determinant / gcdC;
									remainder = determinant % gcdC;
									System.out.println(determinant);
								}

								determinant = determinant % arraySize;
								if (determinant < 0)
									determinant = (arraySize + determinant);
								System.out.println(determinant);

//								if (determinant == 0) {
//									determinant = gcdC;
//								} else {
//									determinant = determinant / gcdC;
//									remainder = determinant % gcdC;									
//								}
//								if ((determinant == cyclicGroup1.order || determinant == cyclicGroup2.order) || remainder > 0 || determinant == 0) {
								if (remainder > 0 || determinant == 0) {
									c2 = 0;	
								} else if (arraySize == 12 && determinant == 6 && 
										((cyclicGroup1.order == 6 && cyclicGroup2.order == 4) ||
										 (cyclicGroup1.order == 4 && cyclicGroup2.order == 6))) { 
									c2 = 0;
								} else if (arraySize == 12 && determinant == 8 && 
									cyclicGroup1.order == 6 && cyclicGroup2.order == 6) { 
									c2 = 2;
								} else{
									c2 = ArithmeticUtils.lcm(determinant, n) / determinant;
									c2 = (c2 % n);
								}

								intersectX =  (c2 * cyclicGroup1.x) % n;
								intersectY = (c2 * cyclicGroup1.y) % n;

								TwoDimCyclicGroup intersectionGroup1 = new TwoDimCyclicGroup(intersectX, intersectY, n);

								intersectX =  (c2 * cyclicGroup2.x) % n;
								intersectY = (c2 * cyclicGroup2.y) % n;
									
								TwoDimCyclicGroup intersectionGroup2 = new TwoDimCyclicGroup(intersectX, intersectY, n);

								intersectionOrder = (cyclicGroup1.order * cyclicGroup2.order / multGroup.order);

								if (intersectionOrder == intersectionGroup1.order) {
									intersectionGroup = intersectionGroup1;
								} else if (intersectionOrder == intersectionGroup2.order) {
									intersectionGroup = intersectionGroup2;
								} else {
									StringBuilder sb = new StringBuilder();
									sb.append("Intersection order '" + intersectionOrder + "', vs '" + intersectionGroup1.order + "," + intersectionGroup2.order + "'\n");
									sb.append("n = '" + arraySize + "', group orders '" + cyclicGroup1.order + "', '" + cyclicGroup2.order + "'\n");
									sb.append("Groups '" + cyclicGroup1.x + "," + cyclicGroup1.y + "', and '" + cyclicGroup2.x + "," + cyclicGroup2.y + "'\n");
									sb.append("c2coef '" + c2coef + "', c2 '" + c2 + "'\n");
									sb.append("Intersect x '" + intersectX + "', y '" + intersectY + "'\n");
									sb.append("Det '" + determinant + "'.\n");
									throw new BndMathException(sb.toString());
								}
//							if (intersectionOrder > intersectionGroup.order)
//								intersectionOrder = intersectionGroup.order;
//								int gcdCoef = gcd(new int[]{matrix.m[0][0], n, c2coef});
//								if (gcdCoef > 1) {
//									List<Integer> primeDifs = SymmetricConfigurationEnumerator.calcPrimeDivisorPows(gcdCoef);
//									for (int prime : primeDifs) {
//										int c2coefp = c2coef / prime;
//										if (c2coefp == 1) {
//											c2 = 0;
//											intersectX = 0;
//											intersectY = 0;									
//											intersectionGroup = new TwoDimCyclicGroup(intersectX, intersectY, n);
//											intersectionGroup.initMatrix();
//											intersectionOrder = 1;
//										} else {
//											int newn = n / prime;
//											c2 = ArithmeticUtils.lcm(c2coefp, newn) / c2coefp;
//											c2 = (c2 % newn);
//
//											if (pivotX == 0 && pivotY == 0) {
//												intersectX =  (c2 * cyclicGroup2.x) % n;
//												intersectY = (c2 * cyclicGroup2.y) % n;
//											} else if (pivotX == 0 && pivotY == 1) {
//												intersectX =  (c2 * cyclicGroup1.x) % n;
//												intersectY = (c2 * cyclicGroup1.y) % n;									
//											} else if (pivotX == 1 && pivotY == 0) {
//												intersectX =  (c2 * cyclicGroup2.y) % n;
//												intersectY = (c2 * cyclicGroup2.x) % n;									
//											} else if (pivotX == 1 && pivotY == 1) {
//												intersectX =  (c2 * cyclicGroup1.y) % n;
//												intersectY = (c2 * cyclicGroup1.x) % n;									
//											}
//											intersectionGroup = new TwoDimCyclicGroup(intersectX, intersectY, n);
//											intersectionGroup.initMatrix();
//											if (intersectionOrder > intersectionGroup.order)
//												intersectionOrder = intersectionGroup.order;
//										}
//									}
//								} else {
//									if (c2coef == 0)
//										c2 = 0;
//									else {
//										c2 = ArithmeticUtils.lcm(c2coef, n) / c2coef;
//										c2 = (c2 % n);
//									}
//								}
							}
							if (intersectionGroup == null) {
								if (pivotX == 0 && pivotY == 0) {
									intersectX =  (c2 * cyclicGroup2.x) % arraySize;
									intersectY = (c2 * cyclicGroup2.y) % arraySize;
								} else if (pivotX == 0 && pivotY == 1) {
									intersectX =  (c2 * cyclicGroup1.x) % arraySize;
									intersectY = (c2 * cyclicGroup1.y) % arraySize;									
								} else if (pivotX == 1 && pivotY == 0) {
									intersectX =  (c2 * cyclicGroup2.y) % arraySize;
									intersectY = (c2 * cyclicGroup2.x) % arraySize;									
								} else if (pivotX == 1 && pivotY == 1) {
									intersectX =  (c2 * cyclicGroup1.y) % arraySize;
									intersectY = (c2 * cyclicGroup1.x) % arraySize;									
								}
								intersectionGroup = new TwoDimCyclicGroup(intersectX, intersectY, arraySize);
								intersectionOrder = intersectionGroup.order;
							}
						}

						int calcedOrder = (cyclicGroup1.order * cyclicGroup2.order / intersectionOrder);

						if (calcedOrder != multGroup.order) {
							StringBuilder sb = new StringBuilder();
							sb.append("Calced mult order '" + calcedOrder + "', vs '" + multGroup.order + "'\n");
							sb.append("n = '" + arraySize + "', group orders '" + cyclicGroup1.order + "', '" + cyclicGroup2.order + "'\n");
							sb.append("Groups '" + cyclicGroup1.x + "," + cyclicGroup1.y + "', and '" + cyclicGroup2.x + "," + cyclicGroup2.y + "'\n");
							sb.append("c2coef '" + c2coef + "', c2 '" + c2 + "'\n");
							sb.append("Intersect x '" + intersectX + "', y '" + intersectY + "'\n");
							sb.append("Det '" + determinant + "'.\n");
							throw new BndMathException(sb.toString());
						}
//					}
//					int gcd = ArithmeticUtils.gcd(gcdX, gcdY);

//					System.out.println("Order x order '" + cyclicGroup1.order * cyclicGroup2.order + "'");
//					System.out.println("GCD '" + gcd);
//							
//							cyclicGroup1.order * cyclicGroup2.order;
//					if (calcOrder != multGroup.order) {
//						throw new CoelRuntimeException("Calced mult order '" + calcOrder + "' not equal to actual order of '" + multGroup.order + "' for groups '" + cyclicGroup1.x + "," + cyclicGroup1.y + "', and '" + cyclicGroup2.x + "," + cyclicGroup2.y + "', n = '" + arraySize + "'.");
//					}
				}
		}
	}

	private int gcd(int[] nums) {
		int gcdCommon = nums[0];
		for (int num : nums) {
			gcdCommon = ArithmeticUtils.gcd(num, gcdCommon);
		}
		return gcdCommon;
	}

//	@Test
	public void test23MultSize() {
		for (int arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize++) {
			final TwoDimCyclicGroup cyclicGroup1 = new TwoDimCyclicGroup(2,2,arraySize);
			final TwoDimCyclicGroup cyclicGroup2 = new TwoDimCyclicGroup(3,3,arraySize);

			final TwoDimCyclicGroup intersectGroup = cyclicGroup1.intersect(cyclicGroup2);

			StringBuilder sb = new StringBuilder();
			sb.append("n = '" + arraySize + "', group orders '" + cyclicGroup1.order + "', '" + cyclicGroup2.order + "', ");
			sb.append("calculated order '" + intersectGroup.order + "'\n");
			System.out.println(sb.toString());
//			sb.append("Groups '" + cyclicGroup1.x + "," + cyclicGroup1.y + "', and '" + cyclicGroup2.x + "," + cyclicGroup2.y + "'\n");

//					int gcd = ArithmeticUtils.gcd(gcdX, gcdY);

//					System.out.println("Order x order '" + cyclicGroup1.order * cyclicGroup2.order + "'");
//					System.out.println("GCD '" + gcd);
//							
//							cyclicGroup1.order * cyclicGroup2.order;
//					if (calcOrder != multGroup.order) {
//						throw new CoelRuntimeException("Calced mult order '" + calcOrder + "' not equal to actual order of '" + multGroup.order + "' for groups '" + cyclicGroup1.x + "," + cyclicGroup1.y + "', and '" + cyclicGroup2.x + "," + cyclicGroup2.y + "', n = '" + arraySize + "'.");
//					}
		}
	}
}