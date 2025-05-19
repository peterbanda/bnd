package com.bnd.core.util;

import junit.framework.TestCase;

import org.junit.Test;

// delete
@Deprecated
public class LatexPutTest extends TestCase {

	private static final int[] DATA_2 = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,1,1,0};
	private static final int[] DATA = new int[]{0,0,0,1,0,1,0,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,0,1,1,1,1,0,1,1,1,0};

	private static final int Y = 112;
	private static final double START_X = 22;
	private static final double X_STEP = 3.08;

	@Test
	public void testLatexPut() {
		double x = START_X;
		StringBuilder sb = new StringBuilder();
		for (int dataPoint : DATA) {
			sb.append("\\put(");
			sb.append((double) Math.round(100 * x) / 100);
			sb.append(",");
			sb.append(Y);
			sb.append("){{\\tiny ");
			sb.append(dataPoint);
			sb.append("}}\n");
			x += X_STEP;
		}
		System.out.println(sb.toString());
	}
}