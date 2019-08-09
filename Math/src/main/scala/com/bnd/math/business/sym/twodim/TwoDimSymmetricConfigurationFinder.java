package com.bnd.math.business.sym.twodim;

import com.bnd.function.enumerator.ListEnumeratorFactory;

import java.util.*;

public class TwoDimSymmetricConfigurationFinder {

	protected final int size;
	protected final ListEnumeratorFactory enumerator;

	public TwoDimSymmetricConfigurationFinder(
		int arraySize,
		ListEnumeratorFactory enumerator
	) {
		this.size = arraySize;
		this.enumerator = enumerator;
	}

	public Map<TwoDimCyclicGroup, List<TwoDimCyclicGroup>> findAllVectors() {
		List<TwoDimCyclicGroup> cyclicGroups = new ArrayList<TwoDimCyclicGroup>();
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				if (x != 0 && y != 0) {
					cyclicGroups.add(new TwoDimCyclicGroup(x,y,size));
				}
		Map<TwoDimCyclicGroup, List<TwoDimCyclicGroup>> cyclicGroupGroupMap = new HashMap<TwoDimCyclicGroup, List<TwoDimCyclicGroup>>();
		Set<TwoDimCyclicGroup> cyclicGroupGroupedSet = new HashSet<TwoDimCyclicGroup>();
		for (int i = 0; i < cyclicGroups.size(); i++) {
			final TwoDimCyclicGroup cyclicGroup1 = cyclicGroups.get(i);
			if (!cyclicGroupGroupedSet.contains(cyclicGroup1)) {
				List<TwoDimCyclicGroup> group = new ArrayList<TwoDimCyclicGroup>();
				group.add(cyclicGroup1);
				cyclicGroupGroupMap.put(cyclicGroup1, group);
				for (int j = i + 1; j < cyclicGroups.size(); j++) {
					final TwoDimCyclicGroup cyclicGroup2 = cyclicGroups.get(j);
					if (Arrays.deepEquals(cyclicGroup1.matrix, cyclicGroup2.matrix)) {
						group.add(cyclicGroup2);
						cyclicGroupGroupedSet.add(cyclicGroup2);
					}
				}
			}
		}
		return cyclicGroupGroupMap;
	}

	public Map<TwoDimCyclicGroup, List<TwoDimCyclicGroup>> findAllBaseVectors() {
		List<TwoDimCyclicGroup> cyclicGroups = new ArrayList<TwoDimCyclicGroup>();
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				if (x != 0 || y != 0) {
					cyclicGroups.add(new TwoDimCyclicGroup(x, y, size));
				}
		Collections.sort(cyclicGroups, new TwoDimCyclicGroupComparators.TwoDimCyclicGroupOrderComparator());
		Map<TwoDimCyclicGroup, List<TwoDimCyclicGroup>> cyclicGroupGroupMap = new HashMap<TwoDimCyclicGroup, List<TwoDimCyclicGroup>>();
		Set<TwoDimCyclicGroup> cyclicGroupGroupedSet = new HashSet<TwoDimCyclicGroup>();
		for (int i = 0; i < cyclicGroups.size(); i++) {
			final TwoDimCyclicGroup cyclicGroup1 = cyclicGroups.get(i);
			if (!cyclicGroupGroupedSet.contains(cyclicGroup1)) {
				List<TwoDimCyclicGroup> group = new ArrayList<TwoDimCyclicGroup>();
				group.add(cyclicGroup1);
				cyclicGroupGroupMap.put(cyclicGroup1, group);
				for (int j = i + 1; j < cyclicGroups.size(); j++) {
					final TwoDimCyclicGroup cyclicGroup2 = cyclicGroups.get(j);
					if (cyclicGroup1.contains(cyclicGroup2)) {
						group.add(cyclicGroup2);
						cyclicGroupGroupedSet.add(cyclicGroup2);
					}
				}
			}
		}
		return cyclicGroupGroupMap;
	}
}