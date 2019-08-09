package com.bnd.math.business.sym;

import com.bnd.core.util.FileUtil;
import com.bnd.function.enumerator.ListEnumerator;
import com.bnd.function.enumerator.ListEnumeratorFactory;
import com.bnd.math.BndMathException;
import com.bnd.math.business.MathTest;
import com.bnd.math.business.sym.ndim.NDimCyclicGroup;
import com.bnd.math.business.sym.ndim.NDimCyclicGroupComparators;
import com.bnd.math.business.sym.ndim.NDimSymmetricConfigurationFinder;
import com.bnd.math.business.sym.onedim.SymmetricConfigurationEnumerator;
import com.bnd.math.business.sym.twodim.TwoDimCyclicGroup;
import com.bnd.math.business.sym.twodim.TwoDimSymmetricConfigurationFinder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class NDimSymmetricConfigurationFinderTest extends MathTest {

	@Autowired
	ListEnumeratorFactory listEnumeratorFactory;

	final FileUtil fu = FileUtil.getInstance();

	final int TWO_DIM_MIN_SIZE = 2;
	final int TWO_DIM_MAX_SIZE = 50;

    final int THREE_DIM_MIN_SIZE = 2;
    final int THREE_DIM_MAX_SIZE = 30;

    final int FOUR_DIM_MIN_SIZE = 2;
    final int FOUR_DIM_MAX_SIZE = 13;

    @Test
	public void testTwoDimEnumerateAllBaseVectors() {
		for (int size = TWO_DIM_MIN_SIZE; size <= TWO_DIM_MAX_SIZE; size++) {
            NDimSymmetricConfigurationFinder finder = new NDimSymmetricConfigurationFinder(size, 2, listEnumeratorFactory);
			final Map<NDimCyclicGroup, List<NDimCyclicGroup>> cyclicGroupGroupMap = finder.findAllBaseVectors();

            List<Integer> primeFactors = SymmetricConfigurationEnumerator.calcPrimeDivisors(size);
            int actualGensCount = cyclicGroupGroupMap.keySet().size();
            System.out.println("SIZE " + size + " primes: " + primeFactors + ", gens: " + actualGensCount);

            assertGensCount(primeFactors, 2, actualGensCount);
            assertGens(primeFactors, 2, size, cyclicGroupGroupMap.keySet());
            assertGensEmptyIntersects(cyclicGroupGroupMap.keySet());

            reportGroups(cyclicGroupGroupMap, true);
		}
	}

    @Test
    public void testThreeDimEnumerateAllBaseVectors() {
        for (int size = THREE_DIM_MIN_SIZE; size <= THREE_DIM_MAX_SIZE; size++) {
            NDimSymmetricConfigurationFinder finder = new NDimSymmetricConfigurationFinder(size, 3, listEnumeratorFactory);
            final Map<NDimCyclicGroup, List<NDimCyclicGroup>> cyclicGroupGroupMap = finder.findAllBaseVectors();

            List<Integer> primeFactors = SymmetricConfigurationEnumerator.calcPrimeDivisors(size);
            int actualGensCount = cyclicGroupGroupMap.keySet().size();
            System.out.println("SIZE " + size + " primes: " + primeFactors + ", gens: " + actualGensCount);

            assertGensCount(primeFactors, 3, actualGensCount);
            assertGens(primeFactors, 3, size, cyclicGroupGroupMap.keySet());
            assertGensEmptyIntersects(cyclicGroupGroupMap.keySet());

            reportGroups(cyclicGroupGroupMap, false);
        }
    }

    @Test
    public void testFourDimEnumerateAllBaseVectors() {
        for (int size = FOUR_DIM_MIN_SIZE; size <= FOUR_DIM_MAX_SIZE; size++) {
            NDimSymmetricConfigurationFinder finder = new NDimSymmetricConfigurationFinder(size, 4, listEnumeratorFactory);
            final Map<NDimCyclicGroup, List<NDimCyclicGroup>> cyclicGroupGroupMap = finder.findAllBaseVectors();

            List<Integer> primeFactors = SymmetricConfigurationEnumerator.calcPrimeDivisors(size);
            int actualGensCount = cyclicGroupGroupMap.keySet().size();
            System.out.println("SIZE " + size + " primes: " + primeFactors + ", gens: " + actualGensCount);

            assertGensCount(primeFactors, 4, actualGensCount);
            assertGens(primeFactors, 4, size, cyclicGroupGroupMap.keySet());
            assertGensEmptyIntersects(cyclicGroupGroupMap.keySet());

            reportGroups(cyclicGroupGroupMap, false);
        }
    }

	private void reportGroups(Map<NDimCyclicGroup, List<NDimCyclicGroup>> cyclicGroupGroupMap, boolean withSubGroups) {
        List<NDimCyclicGroup> cyclicGroups = new ArrayList<NDimCyclicGroup>();
        cyclicGroups.addAll(cyclicGroupGroupMap.keySet());
        Collections.sort(cyclicGroups, new NDimCyclicGroupComparators.NDimCyclicGroupGenVectorComparator());

        for (NDimCyclicGroup reprSymSpace : cyclicGroups) {
            List<NDimCyclicGroup> group = cyclicGroupGroupMap.get(reprSymSpace);
            if (withSubGroups) {
                System.out.println("Repr:" + Arrays.toString(reprSymSpace.genVector) + " (" + reprSymSpace.order + ")");
                System.out.println("-----------------------------");
                for (NDimCyclicGroup cyclicGroup : group) {
                    System.out.println(Arrays.toString(cyclicGroup.genVector) + "(" + cyclicGroup.order + ")");
                }
                System.out.println("-----------------------------");
            }

            for (NDimCyclicGroup cyclicGroup : group) {
                int symCount = ((int) Math.pow(cyclicGroup.n, cyclicGroup.dim)) / cyclicGroup.order;
                if (cyclicGroup.calcSymConfigCount() != symCount)
                    throw new BndMathException("Mismatch of cyclic group sym count '" + cyclicGroup.calcSymConfigCount() + "' vs. '" + symCount + "'.");

                if (cyclicGroup.calcOrder() != cyclicGroup.order)
                    throw new BndMathException("Mismatch of cyclic group order '" + cyclicGroup.calcOrder() + "' vs. '" + cyclicGroup.order + "'.");
            }
        }
        System.out.println("\n");
    }

    @Test
    public void testCompareBaseVectorsWithTwoDimFinder() {
        for (int size = TWO_DIM_MIN_SIZE; size <= TWO_DIM_MAX_SIZE; size++) {
            NDimSymmetricConfigurationFinder nDimFinder = new NDimSymmetricConfigurationFinder(size, 2, listEnumeratorFactory);
            final Map<NDimCyclicGroup, List<NDimCyclicGroup>> nDimCyclicGroupGroupMap = nDimFinder.findAllBaseVectors();
            int nDimGensCount = nDimCyclicGroupGroupMap.size();

            TwoDimSymmetricConfigurationFinder twoDimFinder = new TwoDimSymmetricConfigurationFinder(size, listEnumeratorFactory);
            final Map<TwoDimCyclicGroup, List<TwoDimCyclicGroup>> twoDimCyclicGroupGroupMap = twoDimFinder.findAllBaseVectors();
            int twoDimGensCount = nDimCyclicGroupGroupMap.size();

            assertEquals("For " + size + " n-dim generator count " + nDimGensCount + " differs from the two-dim gens count " + twoDimGensCount, nDimGensCount, twoDimGensCount);
        }
    }

    private void assertGensCount(List<Integer> primeFactors, int dim, int actualGensCount) {
        int expectedGensCount = 0;
        for (int i = 0; i < dim; i++) {
            for (int primeFactor : primeFactors) {
                expectedGensCount += (int) Math.pow(primeFactor, i);
            }
        }
        assertEquals(expectedGensCount, actualGensCount);
    }

    private void assertGens(List<Integer> primeFactors, int dim, int n, Collection<NDimCyclicGroup> gens) {
        Set<int[]> actualGenVectors = new HashSet<int[]>();
        for (NDimCyclicGroup generator: gens) {
            assertEquals(generator.genVector.length, dim);
            actualGenVectors.add(generator.genVector);
        }

        for (int leadingNonZeroIndex = dim - 1; leadingNonZeroIndex >= 0; leadingNonZeroIndex--) {
            for (int primeFactor : primeFactors) {
                int ratio = n / primeFactor;

                ListEnumerator<Integer> enumerator = listEnumeratorFactory.createInstance(true, 0, primeFactor - 1);
                Collection<List<Integer>> primeMultConsts = enumerator.enumerate(dim - leadingNonZeroIndex - 1);
                for (List<Integer> mutlConsts: primeMultConsts) {
                    int[] expectedGenVector = new int[dim];
                    expectedGenVector[leadingNonZeroIndex] = ratio;

                    int i = leadingNonZeroIndex + 1;
                    for (int multConst: mutlConsts) {
                        expectedGenVector[i] = multConst * ratio;
                        i++;
                    }

                    assertEquals(Arrays.toString(expectedGenVector) + " is not contained in the actual gen vector list for dim " + dim + ", n " + n, true, containsArray(actualGenVectors, expectedGenVector));
                }
            }
        }
    }

    private void assertGensEmptyIntersects(Collection<NDimCyclicGroup> gens) {
        for (NDimCyclicGroup gen1 : gens)
            for (NDimCyclicGroup gen2 : gens) {
                if (!Arrays.equals(gen1.genVector, gen2.genVector)) {
                    NDimCyclicGroup intersectGroup = gen1.intersect(gen2);
                    int intersectOrder = intersectGroup.order;
                    assertEquals(1, intersectOrder);
                    for (int i = 1; i < intersectGroup.space.length; i++) {
                        assertEquals(false, intersectGroup.space[i]);
                    }
                }
            }
    }

    private boolean containsArray(Set<int[]> elements, int[] element) {
        for (int[] el: elements) {
            if (Arrays.equals(el, element))
                return true;
        }
        return false;
    }
}