package com.bnd.math;

import static java.lang.System.out;

import java.util.Arrays;
import java.util.Random;

public class perceptron {

	// My percept function calculates the output of a n-input binary 
	// perceptron given input vector x and n+1-dimensional 
	// weight vector w. percept assumes that w[0] is the bias, but 
	// allows for the case where x is n-dimensional (rather than n+1)
	// by adding a 1 in the first position of x.
	public static double percept(double[] w, double[] x) {
		double[] X;
		if(x.length < w.length) {
			X = new double[x.length+1];
			X[0] = 1;
			for(int i = 0; i < x.length; i++) {
				X[i+1] = x[i];
			}
		} else X = x;
		double y = 0;
		for(int i=0; i < X.length; i++) {
			y += w[i]*X[i];
		}
		return y;
	}

	public static int perceptAndThreshold(double[] w, double[] x) {
		return percept(w, x) > 0 ? 1 : 0;
	}

	// Now for the two-layer perceptron where p0 and p1 feed p2
	public static void main(String[] args) {		
		int[] Ds = {1,1,1,0};
		double a = 0.01;
		double[][] ws = {randomDoubleArray(-1,1,3), randomDoubleArray(-1,1,3), randomDoubleArray(-1,1,3)};
		Integer[] order = {0,1,2,3}; 				// generates our input pool
													// (Integer allows shuffling)
		double[] x = new double[3]; 						// x is input
		double p0, p1;								// each p is an output of a ptron
		int p2;
		double[] d = new double[3];					// d holds the errors
		double[] p2in = new double[3];					// the binary input for p2

		for (int q = 0; q < 1000; q++) {
			java.util.Collections.shuffle(Arrays.asList(order)); //randomizes the input order
			
			for(int i : order) {
				x[0] = 1;
				x[1] = i / 2;						// These lines generate the binary
				x[2] = i % 2;						// input vector
			
				// find each perceptron's output
				out.println("X = " + Arrays.toString(x));
				p0 = percept(ws[0],x);
				p1 = percept(ws[1],x);
				p2in[0] = 1; p2in[1] = p0; p2in[2] = p1;
				p2 = perceptAndThreshold(ws[2],p2in);				// p2 is the total output

				// find errors (local gradients)
				d[2] = Ds[i] - p2;
				
				out.println("D = " + Ds[i] + " vs. Y = " + p2);
				
				//the rest is only necessary if d[2] != 0
				if(d[2] != 0) {
				d[1] = d[2]*ws[2][1];
				d[0] = d[2]*ws[2][0];
				
				// adjust the output perceptron's weights
				for(int j = 0; j < 3; j++) {
					ws[2][j] = ws[2][j] + a*d[2]*p2in[j];		// making the minus a plus
				}												// doesn't fix this
				
				// adjust the input perceptrons' weights
				for(int k = 0; k < 2; k++) {
					for (int j = 0; j < 3; j++) {
						ws[k][j] = ws[k][j] + a*d[k]*x[j];
					}
				}
				} // closing the 'if d2 != 0' actions
				// finally, check whether the perceptron answered correctly
			}
			
		}; 
		// while (d[2] < 0.000000000001);
		out.println(Arrays.toString(ws));	
	}

	private static double[] randomDoubleArray(double from, double to, int size) {		
		final Random random = new Random();
		double[] array = new double[size];
		for (int i = 0; i < size; i++) {
			array[i] = from + (to - from) * random.nextDouble();
		}
		return array;
	}
}