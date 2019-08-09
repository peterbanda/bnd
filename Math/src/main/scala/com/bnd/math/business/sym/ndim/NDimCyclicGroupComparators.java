package com.bnd.math.business.sym.ndim;

import com.bnd.core.util.ObjectUtil;

import java.util.Comparator;

public interface NDimCyclicGroupComparators {

    public static class NDimCyclicGroupGenVectorComparator implements Comparator<NDimCyclicGroup> {

        @Override
        public int compare(NDimCyclicGroup s1, NDimCyclicGroup s2) {
            int commonSize = Math.min(s1.dim, s2.dim);
            for (int i = 0; i < commonSize; i++) {
                int result = ObjectUtil.compareObjects(s1.genVector[i], s2.genVector[i]);
                if (result != 0)
                    return result;
            }
            return 0;
        }
    }

    public static class NDimCyclicGroupReverseGenVectorComparator implements Comparator<NDimCyclicGroup> {

		@Override
		public int compare(NDimCyclicGroup s1, NDimCyclicGroup s2) {
			int commonSize = Math.min(s1.dim, s2.dim);
			for (int i = commonSize - 1; i >= 0; i--) {
			    int result = ObjectUtil.compareObjects(s1.genVector[i], s2.genVector[i]);
                if (result != 0)
                    return result;
			}
			return 0;
		}
	}

    public static class NDimCyclicGropuOrderComparator implements Comparator<NDimCyclicGroup> {

        @Override
        public int compare(NDimCyclicGroup s1, NDimCyclicGroup s2) {
            return ObjectUtil.compareObjects(s1.order, s2.order);
        }
    }
}
