package com.bnd.math.business.evo;

import com.bnd.math.domain.evo.EvoRun;
import com.bnd.math.domain.evo.Population;

public interface GeneticAlgorithmBOAutoSaveHandler<C> {

	EvoRun<C> saveEvoRun(EvoRun<C> evoRun);

	Population<C> savePopulation(Population<C> population);

	void replacePopulation(Long populationId, Population<C> newPopulation);

	void removePopulation(Long populationId);
}