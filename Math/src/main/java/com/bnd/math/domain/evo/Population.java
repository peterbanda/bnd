package com.bnd.math.domain.evo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.util.ObjectUtil;
import com.bnd.math.domain.Stats;

/**
 * The domain object for population.
 */
public class Population<C> extends TechnicalDomainObject implements Comparable<Population<?>> {

	private Date timeCreated;
	private Integer generation;
	private EvoRun<C> evolutionRun;

	private Chromosome<C> bestChromosome;
	private Collection<Chromosome<C>> chromosomes = new ArrayList<Chromosome<C>>();

	private Double minScore;
	private Double meanScore;
	private Double maxScore;
	private Double minFitness;
	private Double meanFitness;
	private Double maxFitness;

	public Population() {
		super();
		timeCreated = new Date();
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public Integer getGeneration() {
		return generation;
	}

	public void setGeneration(Integer generation) {
		this.generation = generation;
	}

	public EvoRun<C> getEvolutionRun() {
		return evolutionRun;
	}

	protected void setEvolutionRun(EvoRun<C> evolutionRun) {
		this.evolutionRun = evolutionRun;
	}

	public Chromosome<C> getBestChromosome() {
		return bestChromosome;
	}

	public void setBestChromosome(Chromosome<C> bestChromosome) {
		this.bestChromosome = bestChromosome;
	}

	public boolean hasBestChromosome() {
		return bestChromosome != null;
	}

	public Chromosome<C> getBestOrLastChromosome() {
		if (bestChromosome != null)
			return bestChromosome;
		else {
			boolean maxFlag = evolutionRun.getEvoTask().getGaSetting().isMaxValueFlag();
			return (maxFlag) ? ObjectUtil.getLast(chromosomes) : ObjectUtil.getFirst(chromosomes);
		}
	}

	public Collection<Chromosome<C>> getChromosomes() {
		return chromosomes;
	}

	public void setChromosomes(Collection<Chromosome<C>> chromosomes) {
		this.chromosomes = chromosomes;
	}

	public boolean hasChromosomes() {
		return chromosomes != null && !chromosomes.isEmpty();
	}

	public void addChromosomes(Collection<Chromosome<C>> chromosomes) {
		for (Chromosome<C> chromosome : chromosomes) {
			addChromosome(chromosome);
		}
	}

	public void addChromosome(Chromosome<C> chromosome) {
		chromosome.setPopulation(this);
		chromosomes.add(chromosome);
	}

	public void removeChromosome(Chromosome<C> chromosome) {
		chromosome.setPopulation(null);
		chromosomes.remove(chromosome);
	}

	public void removeAllChromosomes() {
		Collection<Chromosome<C>> chromosomesCopy = new ArrayList<Chromosome<C>>(chromosomes);
		for (Chromosome<C> chromosome : chromosomesCopy) {
			removeChromosome(chromosome);
		}
	}

	public Double getMinScore() {
		return minScore;
	}

	public void setMinScore(Double minScore) {
		this.minScore = minScore;
	}

	public Double getMeanScore() {
		return meanScore;
	}

	public void setMeanScore(Double meanScore) {
		this.meanScore = meanScore;
	}

	public Double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Double maxScore) {
		this.maxScore = maxScore;
	}

	public Double getMinFitness() {
		return minFitness;
	}

	public void setMinFitness(Double minFitness) {
		this.minFitness = minFitness;
	}

	public Double getMeanFitness() {
		return meanFitness;
	}

	public void setMeanFitness(Double meanFitness) {
		this.meanFitness = meanFitness;
	}

	public Double getMaxFitness() {
		return maxFitness;
	}

	public void setMaxFitness(Double maxFitness) {
		this.maxFitness = maxFitness;
	}

	@Override
	public int compareTo(Population<?> population) {
		return ObjectUtil.compareObjects(this, population);
	}

	public Stats getFitnessStats() {
		Stats stats = new Stats();
		stats.setPos(generation.doubleValue());
		stats.setMin(minFitness);
		stats.setMean(meanFitness);
		if (maxFitness != null) {
			stats.setMax(maxFitness);
		} else {
			stats.setMax(getBestOrLastChromosome().getFitness());
		}
		return stats;
	}
}