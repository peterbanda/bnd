package com.bnd.math.business.sym.ndim;

import com.bnd.core.util.ConversionUtil;
import com.bnd.function.enumerator.ListEnumerator;
import com.bnd.function.enumerator.ListEnumeratorFactory;

import java.util.*;

public class NDimSymmetricConfigurationFinder {

	protected final int size;
	protected final int dim;
	protected final ListEnumeratorFactory enumeratorFactory;

	public NDimSymmetricConfigurationFinder(
		int size,
        int dim,
		ListEnumeratorFactory enumeratorFactory
	) {
		this.size = size;
		this.dim = dim;
		this.enumeratorFactory = enumeratorFactory;
	}

	public Map<NDimCyclicGroup, List<NDimCyclicGroup>> findAllVectors() {
        List<NDimCyclicGroup> cyclicGroups = findAllGroups();

		Map<NDimCyclicGroup, List<NDimCyclicGroup>> cyclicGroupGroupMap = new HashMap<NDimCyclicGroup, List<NDimCyclicGroup>>();
		Set<NDimCyclicGroup> cyclicGroupGroupedSet = new HashSet<NDimCyclicGroup>();
		for (int i = 0; i < cyclicGroups.size(); i++) {
			final NDimCyclicGroup cyclicGroup1 = cyclicGroups.get(i);
			if (!cyclicGroupGroupedSet.contains(cyclicGroup1)) {
				List<NDimCyclicGroup> group = new ArrayList<NDimCyclicGroup>();
				group.add(cyclicGroup1);
				cyclicGroupGroupMap.put(cyclicGroup1, group);
				for (int j = i + 1; j < cyclicGroups.size(); j++) {
					final NDimCyclicGroup cyclicGroup2 = cyclicGroups.get(j);
					if (Arrays.equals(cyclicGroup1.space, cyclicGroup2.space)) {
						group.add(cyclicGroup2);
						cyclicGroupGroupedSet.add(cyclicGroup2);
					}
				}
			}
		}
		return cyclicGroupGroupMap;
	}

	public Map<NDimCyclicGroup, List<NDimCyclicGroup>> findAllBaseVectors() {
		List<NDimCyclicGroup> cyclicGroups = findAllGroups();

		Collections.sort(cyclicGroups, new NDimCyclicGroupComparators.NDimCyclicGropuOrderComparator());
		Map<NDimCyclicGroup, List<NDimCyclicGroup>> cyclicGroupGroupMap = new HashMap<NDimCyclicGroup, List<NDimCyclicGroup>>();
		Set<NDimCyclicGroup> cyclicGroupGroupedSet = new HashSet<NDimCyclicGroup>();
		for (int i = 0; i < cyclicGroups.size(); i++) {
			final NDimCyclicGroup cyclicGroup1 = cyclicGroups.get(i);
			if (!cyclicGroupGroupedSet.contains(cyclicGroup1)) {
				List<NDimCyclicGroup> group = new ArrayList<NDimCyclicGroup>();
				group.add(cyclicGroup1);
				cyclicGroupGroupMap.put(cyclicGroup1, group);
				for (int j = i + 1; j < cyclicGroups.size(); j++) {
					final NDimCyclicGroup cyclicGroup2 = cyclicGroups.get(j);
					if (cyclicGroup1.contains(cyclicGroup2)) {
						group.add(cyclicGroup2);
						cyclicGroupGroupedSet.add(cyclicGroup2);
					}
				}
			}
		}
		return cyclicGroupGroupMap;
	}

	private List<NDimCyclicGroup> findAllGroups() {
        List<NDimCyclicGroup> cyclicGroups = new ArrayList<NDimCyclicGroup>();
        ListEnumerator<Integer> listEnumerator = enumeratorFactory.createInstance(true, 0, size - 1);
        Collection<List<Integer>> vectors = listEnumerator.enumerate(dim);
        for (List<Integer> vector : vectors) {
            boolean isNotZero = false;
            for (int vectorElement : vector) {
                if (!isNotZero && vectorElement != 0)
                    isNotZero = true;
            }
            if (isNotZero) {
                final NDimCyclicGroup cyclicGroup = new NDimCyclicGroup(ConversionUtil.toSimpleType(vector.toArray(new Integer[0])), size);
                cyclicGroups.add(cyclicGroup);
            }
        }
        return cyclicGroups;
    }
}