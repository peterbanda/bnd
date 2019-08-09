package com.bnd.math.domain.evo;

import java.util.Comparator;

import com.bnd.core.domain.DomainObject;
import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.util.ObjectUtil;

/**
 * The domain object for chromosome.
 */
public abstract class Chromosome<C> extends TechnicalDomainObject {

	public static class ChromosomeComparator implements Comparator<Chromosome<?>> {

		@Override
		public int compare(Chromosome<?> chromosome1, Chromosome<?> chromosome2) {
			return ObjectUtil.compareObjects(chromosome1.getScore(), chromosome2.getScore());
		}		
	}

	/**
	 * The minimal value of the function.
	 */
	private static final double F_MIN = 0;

	/**
	 * The maximal value of the function.
	 */
	private static final double F_MAX = 2;
	
	/**
	 * The renormalization constant.
	 */
	private static final double E = 0.01;

	protected C code;
	private Double score;
	private Double fitness;
	private Population<C> population;

	private transient double error; 

	protected Chromosome() {
		super();
	}

	public C getCode() {
		return code;
	}

	public void setCode(C code) {
		this.code = code;
	}

	public boolean hasFitness() {
		return fitness != null;
	}

	public Double getFitness() {
		return fitness;
	}

	public void setFitness(Double fitness) {
		this.fitness = fitness;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public Population<C> getPopulation() {
		return population;
	}

	protected void setPopulation(Population<C> population) {
		this.population = population;
	}

	public abstract int getCodeSize();

	/**
	 * Nulls fitness score.
	 */
	public void nullScore() {
		score = 0d;
		error = 0;
	}

	/**
	 * Adds the additional fitness score to actual fitness score.
	 * 
	 * @param additionalScore The score to add.
	 */
	public void addToScore(double additionalScore) {
		this.score += additionalScore;
	}

	/**
	 * Adds the additional error actual fitness error.
	 * 
	 * @param additionalError The error to add.
	 */
	public void addToError(double additionalError) {
		this.error += additionalError;
	}

	/**
	 * Processes fitness renormalization operation.
	 */
	public void renormalizeFitnessScore() {
		this.fitness = ((1 - E) * fitness + F_MIN * E - F_MAX) / (F_MIN - F_MAX);
	}

	/**
	 * Processes fitness advanced renormalization operation.
	 * 
	 * @param order The order of fitness score to renormalize.
	 * @param populationSize The size of population.
	 */
	public void renormalizeFitnessScoreAdvanced(int order, int populationSize) {
		this.fitness = ((1 - E) * order + E - populationSize) / (1 - populationSize);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer theSB = new StringBuffer();
		theSB.append(getScore());
		theSB.append("/");
		theSB.append(getFitness());
		return theSB.toString();
	}
	
	/**
	 * Converts to standard String representation with additional information about code.
	 */
	public String toStringWithCode() {
		return toString() + "/" + getCode();
	}

	/**
	 * @see TechnicalDomainObject#copyFrom(TechnicalDomainObject)
	 */
	@Override
	public void copyFrom(DomainObject<Long> domainObject) {
		if (domainObject == null || !(domainObject instanceof Chromosome)) {
			return;
		}
		Chromosome<C> chromosomeDO = (Chromosome<C>) domainObject;
		super.copyFrom(chromosomeDO);
		setScore(chromosomeDO.getScore());
		setFitness(chromosomeDO.getFitness());
		setCode(chromosomeDO.getCode());
	}
}