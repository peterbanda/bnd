package com.bnd.math.business.evo

import java.{lang => jl}
import com.bnd.math.domain.evo.Chromosome
import com.bnd.math.domain.evo.Population
import com.bnd.math.BndMathException
import scala.collection.JavaConversions._
import com.bnd.math.business.MathUtil._

object EvolutionUtil {

	def getBestScore(population : Population[_]) : jl.Double =
		if (population.getMaxScore != null)
			population.getMaxScore()
		else if (population.hasBestChromosome)
			population.getBestChromosome.getScore
		else if (population.hasChromosomes)
		    bestScore(population)
		else
			throw new BndMathException("No chromosomes nor scores defined but expected for population '" + population.getId + "'.")

	def getBestFitness(population : Population[_]) : jl.Double =
		if (population.getMaxFitness != null)
			population.getMaxFitness
		else if (population.hasBestChromosome)
			population.getBestChromosome.getFitness
		else if (population.hasChromosomes)
		    bestFitness(population)
		else
			throw new BndMathException("No chromosomes nor scores defined but expected for population '" + population.getId + "'.")

	def getWorstScore(population : Population[_]) : jl.Double =
		if (population.getMinScore != null)
			population.getMinScore
		else if (population.hasChromosomes)
			worstScore(population)
		else
			throw new BndMathException("No chromosomes nor scores defined but expected for population '" + population.getId + "'.")

	def getWorstFitness(population : Population[_]) : jl.Double =
		if (population.getMinFitness != null)
			population.getMinFitness
		else if (population.hasChromosomes)
			worstFitness(population)
		else
			throw new BndMathException("No chromosomes nor scores defined but expected for population '" + population.getId + "'.")

	def getMeanScore(population : Population[_]) : jl.Double =
		if (population.getMeanScore != null)
			population.getMeanScore
		else if (population.hasChromosomes)
			calcMean(population.getChromosomes.map(_.getScore))
		else
			throw new BndMathException("No chromosomes nor scores defined but expected for population '" + population.getId + "'.")

	def getMeanFitness(population : Population[_]) : jl.Double =
		if (population.getMeanFitness != null)
			population.getMeanFitness
		else if (population.hasChromosomes)
		    if (population.getChromosomes.head.getFitness == null)
		        null
		    else
		    	calcMean(population.getChromosomes.map(_.getFitness))
		else
			throw new BndMathException("No chromosomes nor scores defined but expected for population '" + population.getId + "'.")

	private def min(population : Population[_], proj : Chromosome[_] => jl.Double) = proj(population.getChromosomes.minBy(proj(_)))
	private def max(population : Population[_], proj : Chromosome[_] => jl.Double) = proj(population.getChromosomes.maxBy(proj(_)))

	private def bestScore(population : Population[_]) = {
	    val maxFlag = population.getEvolutionRun.getEvoTask.getGaSetting.isMaxValueFlag
	    val proj = {chromosome : Chromosome[_] => chromosome.getScore}
	    if (maxFlag)
	        max(population, proj)
	    else
	        min(population, proj)
	}

	private def bestFitness(population : Population[_]) =
	    if (population.getChromosomes.head.getFitness == null)
	        null
	    else {
	    	val maxFlag = population.getEvolutionRun.getEvoTask.getGaSetting.isMaxValueFlag
	    	val proj = {chromosome : Chromosome[_] => chromosome.getFitness}
	    	if (maxFlag)
	    		max(population, proj)
	    	else
	    		min(population, proj)
	    }

	private def worstScore(population : Population[_]) = {
	    val maxFlag = population.getEvolutionRun.getEvoTask.getGaSetting.isMaxValueFlag
	    val proj = {chromosome : Chromosome[_] => chromosome.getScore}
	    if (maxFlag)
	        min(population, proj)
	    else
	        max(population, proj)
	}

	private def worstFitness(population : Population[_]) =
		if (population.getChromosomes.head.getFitness == null)
	        null
	    else {
	    	val maxFlag = population.getEvolutionRun.getEvoTask.getGaSetting.isMaxValueFlag
	    	val proj = {chromosome : Chromosome[_] => chromosome.getFitness}
	    	if (maxFlag)
	    		min(population, proj)
	    	else
	    		max(population, proj)
	    }
}