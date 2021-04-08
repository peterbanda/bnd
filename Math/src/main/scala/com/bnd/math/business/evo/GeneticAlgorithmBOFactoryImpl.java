package com.bnd.math.business.evo;

import com.bnd.core.reflection.ReflectionProvider;
import com.bnd.math.BndMathException;
import com.bnd.math.domain.evo.Chromosome;
import com.bnd.math.domain.evo.EvoGaSetting;
import com.bnd.math.domain.evo.SelectionType;
import com.bnd.math.task.EvoRunTask;

// TODO: changed to public temporary
public class GeneticAlgorithmBOFactoryImpl implements GeneticAlgorithmBOFactory {

	private ReflectionProvider<? extends Chromosome<?>> chromosomeRF;

	public GeneticAlgorithmBOFactoryImpl(ReflectionProvider<? extends Chromosome<?>> chromosomeRF) {
		this.chromosomeRF = chromosomeRF;
	}

	public <H extends Chromosome<C>, C, T> GeneticAlgorithmBO<H, C, T> createInstance(
		EvoRunTask task,
		EvoTaskBO<H, C, T> evoTaskBO
	) {
		GeneticAlgorithmBO<H, C, T> ga = null;
		final EvoGaSetting gaSetting = task.getGaSetting();
		final SelectionType selectionType = gaSetting.getSelectionType();
		switch (selectionType) {
			case Elite:
				ga = new EliteGeneticAlgorithmBO<H, C ,T>(task, evoTaskBO, (ReflectionProvider<Chromosome<C>>) chromosomeRF);
				break;
			case Roulette:
				ga = new RouletteGeneticAlgorithmBO<H, C, T>(task, evoTaskBO, (ReflectionProvider<Chromosome<C>>) chromosomeRF);
				break;
			default: throw new BndMathException("GA type '" + selectionType + "' not recognized.");
		}
		return ga;
	}
}