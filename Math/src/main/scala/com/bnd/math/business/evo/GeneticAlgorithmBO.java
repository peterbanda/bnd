package com.bnd.math.business.evo;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bnd.core.ReverseComparator;
import com.bnd.core.domain.um.User;
import com.bnd.core.reflection.ReflectionProvider;
import com.bnd.core.util.ObjectUtil;
import com.bnd.core.util.RandomUtil;
import com.bnd.math.BndMathException;
import com.bnd.math.domain.evo.*;
import com.bnd.math.domain.evo.Chromosome.ChromosomeComparator;
import com.bnd.math.task.EvoPopulationContentStoreOption;
import com.bnd.math.task.EvoPopulationSelection;
import com.bnd.math.task.EvoRunTask;

/**
 * Functional object for genetic algorithm.
 */
public abstract class GeneticAlgorithmBO<H extends Chromosome<C>, C, T> {

	private final EvoGaSetting gaSetting;
	private final EvoTaskBO<H, C, T> evoTaskBO;
	private EvoPopulationContentStoreOption storeOption;
	private EvoPopulationSelection populationSelection;
	private final boolean autoSave;

	private final ReflectionProvider<Chromosome<C>> chromosomeRF;
	private GeneticAlgorithmBOAutoSaveHandler<C> autoSaveHandler;

	private int generation = 0;
	private Collection<T> testSamples;
	private List<H> chromosomes = new ArrayList<H>();
	private List<H> newGenerationChromosomes = new ArrayList<H>();

	private EvoRun<C> evoRun;
	private Population<C> lastLocalPopulation;

    private final Log log = LogFactory.getLog(getClass());

	protected GeneticAlgorithmBO(
		EvoRunTask evoRunTask,
		EvoTaskBO<H, C, T> evoTaskBO,
		ReflectionProvider<Chromosome<C>> chromosomeRF
	) {
		final EvoTask evoTask = evoRunTask.getEvoTask();
		this.chromosomeRF = chromosomeRF;
		this.gaSetting = evoTask.getGaSetting();
		this.storeOption = evoRunTask.getPopulationContentStoreOption();
		this.populationSelection = evoRunTask.getPopulationSelection();
		this.autoSave = evoRunTask.isAutoSave();
		this.evoTaskBO = evoTaskBO;
		if (evoRunTask.hasInitChromosomes()) {
			for (Chromosome<?> chromosome : evoRunTask.getInitChromosomes()) {
				chromosomes.add((H) chromosome);
			}
			sortChromosomesByScore();
		}

		// initialize evo run
		initEvoRun(evoRunTask);
	}

	private void initEvoRun(EvoRunTask evoRunTask) {
		final EvoTask evoTask = evoRunTask.getEvoTask();
		if (!evoRunTask.isEvoRunDefined()) {
			// initialize evo run
			evoRun = new EvoRun<C>();
			User user = new User();
			user.setId(1l);
			evoRun.setCreatedBy(user);
			evoTask.addEvolutionRun(evoRun);
			if (evoRunTask.hasInitChromosome()) {
				evoRun.setInitChromosome((C) evoRunTask.getInitChromosome());
			}
		} else {
			evoRun = (EvoRun<C>) evoRunTask.getEvoRun();
			Population<C> lastPopulation = ObjectUtil.getLast(evoRun.getPopulations());
			generation = lastPopulation.getGeneration();
			lastLocalPopulation = createPopulationWithSpecifiedContent();
			if (lastLocalPopulation.hasBestChromosome()) {
				lastLocalPopulation.setBestChromosome(
						chromosomeRF.clone(lastLocalPopulation.getBestChromosome()));	
			}
		}
	}

	/**
	 * Evolves the chromosomes for defined number of steps (generations).
	 */
	public void evolve() {
		// generate the first generation if no chromosomes available, otherwise create the next generation
		if (generation == 0 && chromosomes.isEmpty())
			initFirstGenerationChromosomes();
		else
			createNextGeneration();

		// main loop
		while (generation < gaSetting.getGenerationLimit()) {
			evolveOneGeneration();
			log.info("Generation '" + generation + "' evolved." + "Best score/fitness is " + getBestChromosome().getScore() + "/" + getBestChromosome().getFitness() + ".");
		}

		// needed for the last generation
		evaluateChromosomesWithSamples();
		storeResults();
		if (autoSave) {
			replaceOrDeleteLastAutoSave();
		}
	}

	/**
	 * Evolves the next generation of chromosomes.
	 */
	private void evolveOneGeneration() {
		evaluateChromosomesWithSamples();
		storeResults();
		createNextGeneration();
	}

	private void createNextGeneration() {
		crossOver();
		mutate();
		refreshChromosomes();
		generation++;		
	}

	public void setAutoSaveHandler(GeneticAlgorithmBOAutoSaveHandler<C> autoSaveHandler) {
		this.autoSaveHandler = autoSaveHandler;
	}

	protected void initFirstGenerationChromosomes() {
		for (int chromosomeIndex = 0; chromosomeIndex < gaSetting.getPopulationSize(); chromosomeIndex++) {
			chromosomes.add(getChromManipulatorBO().generateRandomChromosome());
		}		
	}

	protected void setChromosomes(List<H> chromosomes) {
		this.chromosomes = chromosomes;
	}

	protected List<H> getChromosomes() {
		return chromosomes;
	}

	protected List<H> getNewGenerationChromosomes() {
		return newGenerationChromosomes;
	}

	protected H[] getNewGenerationChromosomesAsArray() {
		return (H[]) newGenerationChromosomes.toArray(new Chromosome[0]);
	}

	/**
	 * Gets the number of chromosomes in new generation.
	 * 
	 * @return The number of new generation chromosomes.
	 */
	protected int getNewGenerationChromosomeNumber() {
		return newGenerationChromosomes.size();
	}

	/**
	 * Gets the number of chromosomes.
	 * 
	 * @return The number of chromosomes.
	 */
	protected int getChromosomeNumber() {
		return chromosomes.size();
	}

	/**
	 * Adds the value to attribute newGenerationChromosomes.
	 *
	 * @param newGenerationChromosome The value to add.
	 */
	protected void addNewGenerationChromosome(H newGenerationChromosome) {
		newGenerationChromosomes.add(newGenerationChromosome);
	}

	/**
	 * Adds the value to attribute newGenerationChromosomes.
	 *
	 * @param newGenerationChromosomes The value to add.
	 */
	protected void addNewGenerationChromosomes(Collection<H> newGenerationChromosomes) {
		for (H chromosome : newGenerationChromosomes) {
			addNewGenerationChromosome(chromosome);
		}
	}

	/**
	 * Gets the actually best chromosome in evolution.
	 * 
	 * @return The domain object of the actually best chromosome in evolution.
	 */
	public H getBestChromosome() {
		if (!chromosomes.isEmpty()) {
			return ObjectUtil.getFirst(chromosomes);
		}
		return null;
	}

	/**
	 * Gets the actually worst chromosome in evolution.
	 * 
	 * @return The domain object of the actually best chromosome in evolution.
	 */
	public H getWorstChromosome() {
		if (!chromosomes.isEmpty()) {
			return ObjectUtil.getLast(chromosomes);
		}
		return null;
	}

	/**
	 * Processes the renormalize fitness operation for all chromosomes with group increasing fitness.
	 */
	protected void renormalizeFitnessesGroupIncreasing() {
		double consecutiveGroupScore = Double.NEGATIVE_INFINITY;
		int order = 0;
		for (H chromosome : chromosomes) {
			if (chromosome.getScore() != consecutiveGroupScore) {
				consecutiveGroupScore = chromosome.getScore();
				order++;
			}
			chromosome.renormalizeFitnessScoreAdvanced(order, chromosomes.size());
		}
	}

	/**
	 * Processes the renormalize fitness operation for all chromosomes with group jump increasing fitness.
	 */
	protected void renormalizeFitnessesGroupJumpIncreasing() {
		double groupValue = Double.NEGATIVE_INFINITY;
		int order = 1;
		int groupOrder = 0;
		for (H chromosome : chromosomes) {
			if (chromosome.getScore() != groupValue) {
				groupValue = chromosome.getScore();
				groupOrder = order;
			}
			chromosome.renormalizeFitnessScoreAdvanced(groupOrder, chromosomes.size());
			order++;
		}
	}

	/**
	 * Processes the renormalize fitness operation for all chromosomes with strictly increasing fitness.
	 */
	protected void renormalizeFitnessesStrictlyIncreasing() {
		int order = 1;
		for (H chromosome : chromosomes) {
			chromosome.renormalizeFitnessScoreAdvanced(order, chromosomes.size());
			order++;
		}
	}

	/**
	 * Gets the sum of all fitnesses of chromosome in a population. 
	 * 
	 * @return The sum of all fitnesses of chromosome in a population.
	 */
	protected double getPopulationFitnessSum() {
		double sum = 0;
		for (H chromosome : chromosomes) {
			sum += chromosome.getFitness();
		}
		return sum;
	}

	/**
	 * Gets the mean score of chromosomes in the current population. 
	 * 
	 * @return The mean score of chromosomes in the current population.
	 */
	protected double getMeanScore() {
		double sum = 0;
		for (H chromosome : chromosomes) {
			sum += chromosome.getScore();
		}
		return sum / chromosomes.size();
	}

	/**
	 * Gets the mean fitness of chromosomes in the current population. 
	 * 
	 * @return The mean fitness of chromosomes in the current population.
	 */
	protected Double getMeanFitness() {
		Double sum = null;
		for (H chromosome : chromosomes) {
			if (chromosome.hasFitness()) {
				if (sum == null) {
					sum = 0d;
				}
				sum += chromosome.getFitness();
			}
		}
		return sum != null ? sum / chromosomes.size() : null;
	}

	/**
	 * Replaces the chromosome collection with newly evolved one.
	 */
	protected void refreshChromosomes() {
		chromosomes.clear();
		chromosomes.addAll(newGenerationChromosomes);
		newGenerationChromosomes.clear();
	}

	/**
	 * Sorts the chromosomes by their scores.
	 */
	protected void sortChromosomesByScore() {
		// shuffle first to make it more random in case all chromosomes have almost the same score
		Collections.shuffle(chromosomes);
		Comparator<Chromosome<?>> chromosomeComparator = new ChromosomeComparator();
		if (gaSetting.isMaxValueFlag()) {
			chromosomeComparator = new ReverseComparator<Chromosome<?>>(chromosomeComparator);
		}
		Collections.sort(chromosomes, chromosomeComparator);
	}

	/**
	 * Renormalizes the fitness of all chromosomes (if needed).
	 */
	private void renormalizeChromosomeFitnesses() {
		if (gaSetting.getFitnessRenormalizationType() == null) {
			return;
		}
		switch (gaSetting.getFitnessRenormalizationType()) {
			case StrictOrderIncreasing: renormalizeFitnessesStrictlyIncreasing(); break;
			case GroupOrderIncreasing: renormalizeFitnessesGroupIncreasing(); break;
			case GroupJumpOrderIncreasing: renormalizeFitnessesGroupJumpIncreasing(); break;
			default: throw new BndMathException("Fitness renormalization type '" + gaSetting.getFitnessRenormalizationType() + "' not recognized."); 
		}
	}

	/**
	 * Crosses over two parent chromosomes creating one or two new offsprings.
	 * 
	 * @param aParent1 The first parent chromosome to crossover.
	 * @param aParent2 The second parent chromosome to crossover.
	 * @param aBothFlag The flag indicating two children offspring.
	 */
	protected void crossOver(H parent1, H parent2, boolean twoOffspringsFlag) {
		Collection<H> offsprings = new ArrayList<H>();
		if (RandomUtil.nextDouble() <= gaSetting.getCrossOverProbability().doubleValue()) {
			offsprings = crossOverByType(parent1, parent2);
			if (!twoOffspringsFlag)
				offsprings = Collections.singleton(ObjectUtil.getFirst(offsprings));
			if (gaSetting.isConditionalCrossOverFlag()) {
				int offspringIndex = 1;
				evaluateChromosomes(offsprings);
				for (H offspring : offsprings) {
					H parent = offspringIndex == 1 ? parent1 : parent2;
					evaluateChromosome(offspring);
					if (isWorseScore(offspring, parent)) {
						offsprings.remove(offspring);
						offsprings.add(cloneChromosome(parent));
					}					
				}
			}
		} else {
			offsprings.add(cloneChromosome(parent1));
			offsprings.add(cloneChromosome(parent2));
		}
		addNewGenerationChromosomes(offsprings);
	}

	/**
	 *  Crosses over two chromosomes and produce two offsprings
	 */
	protected Collection<H> crossOverByType(H parent1, H parent2) {
		final int parent1Size = parent1.getCodeSize();
		final int parent2Size = parent2.getCodeSize();
		if (parent1Size != parent2Size)
			throw new BndMathException("Size of the first and second parent chromosome is not the same '" + parent1Size + "' vs '" + parent2Size + ".");
		final int crossNum = RandomUtil.nextInt(parent1Size + 1);

		switch (gaSetting.getCrossOverType()) {			
			case Split:
				return getChromManipulatorBO().crossOverSplit(parent1, parent2, crossNum);
			case Shuffle:
				return getChromManipulatorBO().crossOverShuffle(parent1, parent2, crossNum);
			default: throw new BndMathException("Crossover type '" + gaSetting.getCrossOverType() + "' not recognized.");
		}
	}

	protected abstract void crossOver();

	/**
	 * Mutates given chromosome.
	 * 
	 * @param aChromosomeDO The chromosome to mutate.
	 */
	protected void mutate(H chromosome) {
		if (new Random().nextDouble() < gaSetting.getMutationProbability().doubleValue()) {
			if (!gaSetting.isConditionalMutationFlag()) {
				mutateByType(chromosome);	
			} else {
				H originalChromosome = (H) chromosomeRF.clone(chromosome);
				mutateByType(chromosome);
				evaluateChromosome(chromosome);
				if (isWorseScore(chromosome.getScore(), originalChromosome.getScore())) {
					chromosome.setCode(originalChromosome.getCode());
				}						
			}
		}
	}

	/**
	 * Mutates the code of the chromosome by selected mutation type.
	 */
	protected void mutateByType(H chromosome) {
		switch (gaSetting.getMutationType()) {			
			case OneBit:
				getChromManipulatorBO().mutateOneBit(chromosome);
				break;
			case TwoBits:
				getChromManipulatorBO().mutateTwoBits(chromosome);
				break;
			case PerBit:
				getChromManipulatorBO().mutatePerBit(chromosome, gaSetting.getPerBitMutationProbability());
				break;
			case Exchange:
				getChromManipulatorBO().mutateBySwapping(chromosome);
				break;
			default: throw new BndMathException("Mutation type '" + gaSetting.getMutationType() + "' not recognized.");
		}
	}

	protected abstract void mutate();

	private boolean isWorseScore(double score1, double score2) {
		return gaSetting.isMaxValueFlag() ? score1 < score2 : score1 > score2;
	}

	private boolean isWorseScore(Chromosome<?> chromosome1, Chromosome<?> chromosome2) {
		return gaSetting.isMaxValueFlag() ? chromosome1.getScore() < chromosome2.getScore() : chromosome1.getScore() > chromosome2.getScore();
	}

	private H cloneChromosome(H chromosome) {
		return getChromManipulatorBO().cloneChromosome(chromosome);
	}

	public void evaluateChromosome(H chromosome) {
		evaluateChromosomes(Collections.singleton(chromosome));
	}

	public void evaluateChromosomes(Collection<H> chromosomes) {
		getFitnessEvaluatorBO().evaluateScoreAndFitness(chromosomes, testSamples);
	}
	
//	public void evaluateChromosomesInParallel() {
//		Parallelizer<H> parallelizer = new Parallelizer<H>(createEvaluateChromosomeRunnable(), 20, chromosomes);
//		parallelizer.run();
//	}
//
//	private RunnableWith<H> createEvaluateChromosomeRunnable() {
//		return new RunnableWith<H>() {
//
//			@Override
//			public void run(H chromosome) {
//				evaluateChromosome(chromosome);
//			}
//		};
//	}

	private void evaluateChromosomesWithSamples() {
		testSamples = getTestSampleGeneratorBO().createTestSamples();
		evaluateChromosomes(chromosomes);
		sortChromosomesByScore();
		renormalizeChromosomeFitnesses();
	}

	/**
	 * Store results (best chromosomes) and actual population if needed. 
	 */
	public void storeResults() {
		Population<C> localPopulation = createPopulationWithSpecifiedContent();
		if (autoSave) {
			// replace or delete last auto-save first
			replaceOrDeleteLastAutoSave();
			// save a current population with all chromosomes to DB
			persistFullPopulation();
		} else { 
			// save population locally
			if (localPopulation != null) {
				evoRun.addPopulation(localPopulation);
			}
		}
		lastLocalPopulation = localPopulation;
	}

	private void persistFullPopulation() {
		if (evoRun.getId() == null) {
			evoRun = autoSaveHandler.saveEvoRun(evoRun);			
		}
		// save full population
		Population<C> populationToAutoSave = createPopulationWithChromosomes();
		evoRun.addPopulation(populationToAutoSave);

		final Population<C> autoSavedPopulation = (Population<C>) autoSaveHandler.savePopulation(populationToAutoSave);
		evoRun.removePopulation(populationToAutoSave);
		evoRun.addPopulation(autoSavedPopulation);
	}

	/**
	 * Replace or delete previously autosaved population if needed.
	 */
	private void replaceOrDeleteLastAutoSave() {
		if (autoSaveHandler == null) {
			log.warn("Auto save requested, but no handler provided.");
			return;
		}
		Population<C> lastAutoSavedPopulation = ObjectUtil.getLast(evoRun.getPopulations());
		if (lastAutoSavedPopulation == null) {
			// nothing to replace
			return;
		}
		if (lastLocalPopulation != null) {
			if (storeOption != EvoPopulationContentStoreOption.Full) {
				autoSaveHandler.replacePopulation(lastAutoSavedPopulation.getId(), lastLocalPopulation);
			}
		} else {
			autoSaveHandler.removePopulation(lastAutoSavedPopulation.getId());
			evoRun.removePopulation(lastAutoSavedPopulation);
		}
	}

	private Population<C> createPopulationWithSpecifiedContent() {
		if (populationSelection == EvoPopulationSelection.Last && generation < gaSetting.getGenerationLimit()) {
			return null;
		}
		Population<C> population;
		switch (storeOption) {
			case ScoreFitness:
				population = createPopulationWithoutChromosomes();
				break;
			case BestChromosome:
				population = createPopulationWithBestChromosome();
				break;
			case Full:
				population = createPopulationWithChromosomes();
				break;
			default: throw new BndMathException("Population content store option '" + storeOption + "' not recognized.");
		}
		return population;
	}

	private Population<C> createPopulationWithoutChromosomes() {
		Population<C> population = new Population<C>();
		population.setGeneration(generation);

		final H bestChromosome = getBestChromosome();
		final H worstChromosome = getWorstChromosome();

		// explicit score
		population.setMinScore(worstChromosome.getScore());
		population.setMaxScore(bestChromosome.getScore());
		population.setMeanScore(getMeanScore());

		// explicit fitness
		population.setMinFitness(worstChromosome.getFitness());
		population.setMaxFitness(bestChromosome.getFitness());
		population.setMeanFitness(getMeanFitness());

		return population;
	}

	private Population<C> createPopulationWithChromosomes() {
		Population<C> population = new Population<C>();
		population.setGeneration(generation);
		population.addChromosomes((Collection<Chromosome<C>>) chromosomes);
		return population;
	}

	private Population<C> createPopulationWithBestChromosome() {
		Population<C> population = new Population<C>();
		population.setGeneration(generation);

		final H bestChromosome = getBestChromosome();
		final H worstChromosome = getWorstChromosome();

		// explicit score
		population.setMinScore(worstChromosome.getScore());
		population.setMeanScore(getMeanScore());

		// explicit fitness
		population.setMinFitness(worstChromosome.getFitness());
		population.setMeanFitness(getMeanFitness());

		population.setBestChromosome(bestChromosome);

		return population;
	}

	public EvoGaSetting getGaSetting() {
		return gaSetting;
	}

	protected EvoChromManipulatorBO<H, C> getChromManipulatorBO() {
		return evoTaskBO.getChromManipulator();
	}

	protected EvoFitnessEvaluator<H, T> getFitnessEvaluatorBO() {
		return evoTaskBO.getFitnessEvaluator();
	}

	protected EvoTestSampleGeneratorBO<T> getTestSampleGeneratorBO() {
		return evoTaskBO.getTestSampleGenerator();
	}

	public EvoRun<C> getEvoRun() {
		return evoRun;
	}
}