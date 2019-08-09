package com.bnd.math.business.sym.twodim;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.ArithmeticUtils;

public class TwoDimCyclicGroup {
    public final int x;
    public final int y;
    public final int n;
    protected final boolean[][] matrix;
    public int order;

    public TwoDimCyclicGroup(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.matrix = new boolean[size][size];
        this.n = size;
        this.order = initMatrix(matrix, x, y, n);
    }

    private static int initMatrix(boolean[][] matrix, int x, int y, int n) {
        int posX = 0;
        int posY = 0;
        int order = 0;
        while (!matrix[posX][posY]) {
            matrix[posX][posY] = true;
            order++;
            posX = (posX + x) % n;
            posY = (posY + y) % n;
        }
        return order;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean[] row : matrix) {
            for (boolean element : row)
                if (element)
                    sb.append("X");
                else
                    sb.append(" ");
            sb.append("|\n");
        }
        sb.append(StringUtils.repeat("-", n) + "\n");

        return sb.toString();
    }

    public boolean contains(TwoDimCyclicGroup cyclicGroup) {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (matrix[i][j] && !cyclicGroup.matrix[i][j])
                    return false;
        return true;
    }

    public int calcOrder() {
//			return ArithmeticUtils.lcm(n / ArithmeticUtils.gcd(x,n), n / ArithmeticUtils.gcd(y,n));
        return n / ArithmeticUtils.gcd(n, ArithmeticUtils.gcd(x,y));
//			return ArithmeticUtils.lcm(ArithmeticUtils.gcd(x,n), ArithmeticUtils.gcd(y,n));
    }

    public int calcSymConfigCount() {
        return n * ArithmeticUtils.gcd(n, ArithmeticUtils.gcd(x,y));
    }

    public TwoDimCyclicGroup mult(TwoDimCyclicGroup group) {
        TwoDimCyclicGroup multGroup = new TwoDimCyclicGroup(-1, -1, n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (matrix[i][j]) {
                    int posX = i;
                    int posY = j;
                    // while (!multGroup.matrix[posX][posY]) {
                    for (int k = 0; k < n; k++) {
                        if (!multGroup.matrix[posX][posY]) {
                            multGroup.order++;
                            multGroup.matrix[posX][posY] = true;
                        }
                        posX = (posX + group.x) % n;
                        posY = (posY + group.y) % n;
                    }
                }
        return multGroup;
    }

    public TwoDimCyclicGroup intersect(TwoDimCyclicGroup group) {
        TwoDimCyclicGroup multGroup = new TwoDimCyclicGroup(-1, -1, n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (matrix[i][j] && group.matrix[i][j]) {
                    multGroup.order++;
                    multGroup.matrix[i][j] = true;
                }
        return multGroup;
    }
}