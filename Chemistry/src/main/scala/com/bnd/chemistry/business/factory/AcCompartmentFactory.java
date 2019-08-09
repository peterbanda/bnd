package com.bnd.chemistry.business.factory;

import com.bnd.chemistry.domain.*;

public class AcCompartmentFactory {

	private final AcReactionSetFactory reactionSetFactory;
	private final AcSpeciesSetFactory speciesSetFactory;

	public AcCompartmentFactory(
		AcReactionSetFactory reactionSetFactory,
		AcSpeciesSetFactory speciesSetFactory
	) {
		this.reactionSetFactory = reactionSetFactory;
		this.speciesSetFactory = speciesSetFactory;
	}

	public AcCompartment createInstance(ArtificialChemistrySpec spec, AcSimulationConfig config) {
		AcSpeciesSet speciesSet = null;
		AcReactionSet reactionSet = null;

		if (spec instanceof AcSymmetricSpec) {
			AcSymmetricSpec acSymmetricSpec = (AcSymmetricSpec) spec;
			speciesSet = speciesSetFactory.createFixedOrder(acSymmetricSpec.getSpeciesNum());
			reactionSet = reactionSetFactory.createRandomReactionSymmetricRS(acSymmetricSpec, speciesSet);
		} else if (spec instanceof AcDNAStrandSpec) {
			AcDNAStrandSpec dnaStrandReactionSpec = (AcDNAStrandSpec) spec;
			speciesSet = speciesSetFactory.createDNAStrandSpeciesSet(dnaStrandReactionSpec);
			reactionSet = reactionSetFactory.createDNAStrandRS(dnaStrandReactionSpec, (AcDNAStrandSpeciesSet) speciesSet);
		}

		AcCompartment skin = new AcCompartment();
		skin.setReactionSet(reactionSet);
		skin.setGeneratedBySpec(spec);
		return skin;
	}
}