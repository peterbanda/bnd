package com.bnd.math.business.sym.ndim;

import org.apache.commons.math3.util.ArithmeticUtils;

public class NDimCyclicGroup {
    public final int[] genVector;
    public final boolean[] space;
    public final int dim;
    public final int n;
    public int order;

    public NDimCyclicGroup(int[] genVector, int size) {
        this.genVector = genVector;
        this.n = size;
        this.dim = genVector.length;
        this.space = new boolean[(int) Math.pow(n, dim)];
        this.order = initSpace(space, genVector, n);
    }

    private static int initSpace(boolean[] space, int[] vector, int n) {
        int dim = vector.length;
        int[] pos = new int[dim];
        int innerIndex;
        int order = 0;
        while (!space[innerIndex = getInnerIndex(n, dim, pos)]) {
            space[innerIndex] = true;
            order++;
            for (int i = 0; i < dim; i++) {
                pos[i] = (pos[i] + vector[i]) % n;
            }
        }
        return order;
    }

    private static int getInnerIndex(int n, int dim, int[] position) {
        int index = 0;
        for (int i = 0; i < dim; i++) {
            index = (n * index) + position[i];
        }
        return index;
    }

    private int getInnerIndex(int[] position) {
        return getInnerIndex(n, dim, position);
    }

    private boolean getElement(int[] position) {
        return space[getInnerIndex(position)];
    }

    private void setElement(int[] position) {
        space[getInnerIndex(position)] = true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean element : space)
            if (element)
                sb.append("X");
            else
                sb.append(" ");
        sb.append("|\n");
        return sb.toString();
    }

    public boolean contains(NDimCyclicGroup cyclicGroup) {
        if (n != cyclicGroup.n || dim != cyclicGroup.dim)
            return false;

        for (int innerIndex = 0; innerIndex < space.length; innerIndex++) {
            if (space[innerIndex] && !cyclicGroup.space[innerIndex])
                return false;
        }
        return true;
    }

    private int calcVectorGcd() {
        if (dim == 0)
            return 0;

        int gcd = genVector[0];
        for (int vectorElement : genVector)
            gcd = ArithmeticUtils.gcd(gcd, vectorElement);

        return gcd;
    }

    public int calcOrder() {
        return n / ArithmeticUtils.gcd(n, calcVectorGcd());
    }

    public int calcSymConfigCount() {
        return ((int) Math.pow(n, dim - 1)) * ArithmeticUtils.gcd(n, calcVectorGcd());
    }

    public NDimCyclicGroup intersect(NDimCyclicGroup group) {
        NDimCyclicGroup intersectGroup = new NDimCyclicGroup(new int[dim], n);
        intersectGroup.order = 0;
        for (int i = 0; i < space.length; i++)
            if (space[i] && group.space[i]) {
                intersectGroup.order++;
                intersectGroup.space[i] = true;
            }
        return intersectGroup;
    }
}