package com.bnd.math.business.evo;

import com.bnd.math.domain.evo.Chromosome;
import com.bnd.math.task.EvoRunTask;

public interface GeneticAlgorithmBOFactory {
	
	public <H extends Chromosome<C>, C, T> GeneticAlgorithmBO<H, C, T> createInstance(
		EvoRunTask task,
		EvoTaskBO<H, C, T> evoTaskBO
	);
}