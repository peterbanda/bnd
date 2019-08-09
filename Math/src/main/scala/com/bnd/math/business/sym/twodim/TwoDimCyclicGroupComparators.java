package com.bnd.math.business.sym.twodim;

import com.bnd.core.util.ObjectUtil;

import java.util.Comparator;

public interface TwoDimCyclicGroupComparators {

    public static class TwoDimCyclicGroupYXComparator implements Comparator<TwoDimCyclicGroup> {

        @Override
        public int compare(TwoDimCyclicGroup s1, TwoDimCyclicGroup s2) {
            int result = ObjectUtil.compareObjects(s1.y, s2.y);
            if (result == 0)
                result = ObjectUtil.compareObjects(s1.x, s2.x);
            return result;
        }
    }

    public static class TwoDimCyclicGroupXYComparator implements Comparator<TwoDimCyclicGroup> {

        @Override
        public int compare(TwoDimCyclicGroup s1, TwoDimCyclicGroup s2) {
            int result = ObjectUtil.compareObjects(s1.x, s2.x);
            if (result == 0)
                result = ObjectUtil.compareObjects(s1.y, s2.y);
            return result;
        }
    }

    public static class TwoDimCyclicGroupOrderComparator implements Comparator<TwoDimCyclicGroup> {

        @Override
        public int compare(TwoDimCyclicGroup s1, TwoDimCyclicGroup s2) {
            return ObjectUtil.compareObjects(s1.order, s2.order);
        }
    }
}