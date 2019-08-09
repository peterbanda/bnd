package com.bnd.math.business.rand;

import com.bnd.math.domain.rand.BooleanDensityUniformDistribution;

public class RandomDistributionTest {

	public static void main(String[] args) {
		int size = 20;
		int repetitions = 100000;
		int[] counts = new int[size + 1];
		int[] indexOccurences = new int[size];
		RandomDistributionProvider<Boolean> rdp = RandomDistributionProviderFactory.apply(new BooleanDensityUniformDistribution());
		for (int i = 0; i < repetitions; i++) {
			int count = 0;
			int index = 0;
			for (boolean val : rdp.nextList(size)) {
				if (val) {
					count++;
					indexOccurences[index]++;
				}
				index++;
			}
			counts[count]++;
		}
		int i = 0;
		for (int count : counts) {
			System.out.println(i + ":" + count);
			i++;
		}
		System.out.println();
		i = 0;
		for (int occurence : indexOccurences) {
			System.out.println(i + ":" + occurence);
			i++;
		}
	}
}