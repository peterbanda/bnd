package com.bnd.math.business.sym.onedim;

public class SymmetricConfigurationEvent {
	
	private class SymmetricRegionInfo {
		private int length;
		private int match;

		public SymmetricRegionInfo(int length) {
			match = 0;
			this.length = length;
		}

		public void incMatch() {
			match++;
			if (match >= length) {
				match = 0;
			}
		}
	}

//	private SymmetricConfigurationsDefinition symmetricConfigurationDefinition;
//	private boolean[] configuration;
//	private Random random = new Random();
//
//	private static double symmetricConfProbability = 0;
//	
//	/**
//	 * Creates new instance of the class SymmetricConfigurationEvent and initializes it.
//	 * 
//	 * @param symmetricConfigurationDefintion
//	 */
//	public SymmetricConfigurationEvent(SymmetricConfigurationsDefinition symmetricConfigurationDefintion) {
//		this.symmetricConfigurationDefinition = symmetricConfigurationDefintion;
//		configuration = new boolean[symmetricConfigurationDefintion.arraySize];
//	}
//
//	/**
//	 * Resets configuration.
//	 */
//	private void resetConfiguration() {
//		for (int i = 0; i < configuration.length; i++) {
//			configuration[i] = false;
//		}
//	}
//
//	/**
//	 */
//	private void generateUniformRandomConfiguration() {
//		for (int i = 0; i < configuration.length; i++) {
//			configuration[i] = random.nextBoolean();
//		}
//	}
//
//	/**
//	 */
//	private void generateDensityUniformRandomConfiguration() {
//		resetConfiguration();
//		int activeCellsNum = random.nextInt(configuration.length + 1);
//		for (int i = 0; i < activeCellsNum; i++) {
//			int pos;
//			do {
//				pos = random.nextInt(configuration.length);
//			} while (configuration[pos]);
//			configuration[pos] = true;
//		}
//	}
//
//	/**
//	 * Checks if configuration is symmetric considering given divisor.
//     * 
//	 * @return True if configuration is symmetric
//	 */
//	private boolean isConfigurationSymmetric() {
//		int divisor = symmetricConfigurationDefinition.divisor;
//		if (configuration.length % divisor != 0) {
//			return false;
//		}
//		int segmentsNum = configuration.length / divisor;
//		for (int index = 0; index < divisor; index++) {
//			boolean flag = configuration[index];
//			for (int segment = 1; segment < segmentsNum; segment++) {
//				if (configuration[segment * divisor + index] != flag) {
//					return false;
//				}
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * Checks if configuration is symmetric considering all divisors.
//     * 
//	 * @return True if configuration is symmetric
//	 */
//	private boolean isConfigurationSymmetricForAllDivisors() {
//		ConcurrentSkipListSet<SymmetricRegionInfo> symmetricRegions = new ConcurrentSkipListSet<SymmetricRegionInfo>();
//		for (int index = 0; index < configuration.length; index++) {
//			Iterator<SymmetricRegionInfo> symmetricRegionsIterator = symmetricRegions.iterator();
//			while (symmetricRegionsIterator.hasNext()) {
//				SymmetricRegionInfo region = symmetricRegionsIterator.next();
//				if (configuration[region.match] == configuration[index]) {
//					region.incMatch();
//				} else {
//					symmetricRegions.remove(region);
//				}				
//			}
//			if (index < configuration.length / 2) {
//				symmetricRegions.add(new SymmetricRegionInfo(index));
//			}
//		}
//		for (SymmetricRegionInfo symmetricRegion : symmetricRegions) {
//			if (symmetricRegion.length < configuration.length && symmetricRegion.match == symmetricRegion.length) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * Runs random event.
//	 * 
//	 * @return True if configuration is symmetric, otherwise false
//	 */
//	private boolean runRandomly(boolean allDivisors) {
//		if (symmetricConfigurationDefinition.densityUniformFlag) {
//			generateDensityUniformRandomConfiguration();
//		} else {
//			generateUniformRandomConfiguration();
//		}
//		if (allDivisors) {
//			Collection<Integer> divisors = symmetricConfigurationDefinition.getAllDivisors();
//			for (Integer divisor : divisors) {
//				symmetricConfigurationDefinition.divisor = divisor;
//				if (isConfigurationSymmetric()) {
//					return true;
//				}
//			}
//			return false;
////			return isConfigurationSymmetricForAllDivisors();
//		} else {
//			return isConfigurationSymmetric();
//		}
//	}
//
//	/**
//	 * Runs randomly given number of times.
//	 * 
//	 * @return The fraction of symmetric to total number of cases.
//	 */
//	public static double runRandomlyInParallel(
//		final SymmetricConfigurationsDefinition symmetricConfigurationDefinition,
//		long numberOfRuns,
//		int numberOfThreads
//	) {
//		symmetricConfProbability = 0;
//		if (numberOfRuns % numberOfThreads != 0) {
//			throw new RuntimeException("The number of runs must be divisible by 4.");
//		}
//		final long runsPerThread = numberOfRuns / numberOfThreads;
//		CountDownLatch doneSignal = new CountDownLatch(numberOfThreads);
//		for (int i = 0; i < numberOfThreads; i++) {
//			Worker eventWorker = new Worker(doneSignal) {
//
//				@Override
//				protected void doWork() {
//					SymmetricConfigurationEvent event = new SymmetricConfigurationEvent((SymmetricConfigurationsDefinition) symmetricConfigurationDefinition.clone());
//					addToSymmetricConfProbability(event.runRandomly(runsPerThread));
//				}				
//			};
//			new Thread(eventWorker).start();
//		}
//		try {
//			doneSignal.await();
//		} catch (InterruptedException anException) {
//			throw new RuntimeException(anException);
//		}
//		return (double) symmetricConfProbability / numberOfThreads;
//	}
//
//	public synchronized static void addToSymmetricConfProbability(double prob) {
//		symmetricConfProbability += prob;
//	}
//
//	/**
//	 * Runs randomly given number of times.
//	 * 
//	 * @return The fraction of symmetric to total number of cases.
//	 */
//	public double runRandomly(long numberOfRuns) {
//		long numberOfSymmetricCases = 0;
//		boolean divisorNotDefined = symmetricConfigurationDefinition.divisor == -1;
//		for (long i = 0; i < numberOfRuns; i++) {
//			if (runRandomly(divisorNotDefined)) {
//				numberOfSymmetricCases++;
//			}
//		}
//		return (double) numberOfSymmetricCases / numberOfRuns;
//	}
//
//	private static final long REPETITIONS_BASE = 1000000000l;
//
//	/**
//	 * Just for testing!
//	 * 
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		if (args == null || args.length == 0) {
//			System.out.println("No input specified!");
//			System.out.println("Params: <array_size> <density_uniform_flag> <repetitions> <threads>");
//			return;
//		}
//		int arraySize = Integer.parseInt(args[0]);
//		boolean densityUniformFlag = Integer.parseInt(args[1]) == 1;
//		long repetitions = REPETITIONS_BASE * Integer.parseInt(args[2]);	
//		int threads = Integer.parseInt(args[3]);
//		int divisor = -1;
//		Date now = new Date();
//		SymmetricConfigurationsDefinition symmetricConfigurationDefinition = new SymmetricConfigurationsDefinition(arraySize, divisor, densityUniformFlag);
//		double calculatedProbability = symmetricConfigurationDefinition.computeProbabilityOfSymmetricConfiguration();
////		double simulatedProbability = new SymmetricConfigurationEvent(symmetricConfigurationDefinition).runRandomly(50000000); // 9000000000l
//		double simulatedProbability = runRandomlyInParallel(symmetricConfigurationDefinition, repetitions, threads);
//		double diff = 1 - calculatedProbability / simulatedProbability;
//
//		StringBuffer sb = new StringBuffer();
//		sb.append("Divisor: " + divisor + "    Array size: " + arraySize + '\n');
//		sb.append("Density uniform " + densityUniformFlag + '\n');
//		sb.append("Repetitions " + repetitions + '\n');
//		sb.append('\n');
//		sb.append(" - Calculated prob: " + calculatedProbability + '\n');
//		sb.append(" - Simulated prob: " + simulatedProbability + '\n');
//		sb.append(" -      Difference prob: " + diff + '\n');
//		Date after = new Date();
//		sb.append("Time elapsed: " + (after.getTime() - now.getTime()));
//		FileUtil fu = FileUtil.getInstance();
//		try {
//			fu.overwriteStringToFile(sb.toString(), "output_" + now.getTime());
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}