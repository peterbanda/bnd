package com.bnd.math.business.evo;

import com.bnd.math.domain.evo.EvoTaskType;

public class EvoChromManipulatorBOFactory {

	private static EvoChromManipulatorBOFactory instance = new EvoChromManipulatorBOFactory();

	private EvoChromManipulatorBOFactory() {
		// Nothing to do
	}

	public static EvoChromManipulatorBOFactory getInstance() {
		return instance;
	}

	public EvoChromManipulatorBO<?, ?> createInstance(EvoTaskType evoTaskType) {
		EvoChromManipulatorBO<?, ?> chromManipulatorBO = null;
//		switch (evoTaskType) {
//			case AcPerceptron:
//				chromManipulatorBO = new EvoUniformChromManipulatorBO<ArrayChromosomeDO<Double>, Double[], Double, AcInteractionSeries>(
//						ArrayChromosomeDO.class, 100, Double.class);
//		}
		return chromManipulatorBO;
	}
}
