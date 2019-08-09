package com.bnd.chemistry.business;

import java.util.ArrayList;
import java.util.Collection;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.chemistry.domain.*;

public class AcMichaelisMentenToMassActionRSConverter extends AcReactionSetConverter {

	private static final double K_FAST = 1d;

	private final MassActionType massActionType;

	public AcMichaelisMentenToMassActionRSConverter(
		AcReactionSet inputReactionSet,
		MassActionType massActionType
	) {
		super(inputReactionSet);
		this.massActionType = massActionType;
	}

	@Override
	protected void validate(AcReaction inputReaction) {
		if (!inputReaction.hasSpeciesAssociations(AcSpeciesAssociationType.Catalyst)
				&& inputReaction.getCollectiveCatalysisType() == AcCollectiveSpeciesReactionAssociationType.AND) {
			throw new BndChemistryException(getClass().getName() + " cannot convert a catalytic reaction with AND (all needed) relation among its catalysts.");
		}
	}

	@Override
	protected boolean isConvertible(AcReaction inputReaction) {
		// it must be a OR catalysis
		return (!inputReaction.hasSpeciesAssociations(AcSpeciesAssociationType.Inhibitor)
				&& inputReaction.hasSpeciesAssociations(AcSpeciesAssociationType.Catalyst)
				&& inputReaction.getCollectiveCatalysisType() == AcCollectiveSpeciesReactionAssociationType.OR);
	}

	@Override
	// S1 + S2 = P1 + P2 (catalyst: E)      ->        E + S1 + S2 = ES → E + P1 + P2
	protected Collection<AcReaction> convert(AcReaction inputReaction) {
		Collection<AcReaction> newReactions = new ArrayList<AcReaction>();
		for (final AcSpeciesReactionAssociation catalystAssoc : inputReaction.getSpeciesAssociations(AcSpeciesAssociationType.Catalyst)) {
			newReactions.addAll(getCatalysisMassActionSubstitutionReactions(inputReaction, catalystAssoc.getSpecies()));
		}
		return newReactions;
	}

	private Collection<AcReaction> getCatalysisMassActionSubstitutionReactions(
		final AcReaction reaction,
		final AcSpecies catalyst
	) {
		final Collection<AcSpecies> reactants = acUtil.getSpecies(reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant));
		final Collection<AcSpecies> products = acUtil.getSpecies(reaction.getSpeciesAssociations(AcSpeciesAssociationType.Product));

		AcSpecies substrateCatalystComplex = new AcSpecies();
		substrateCatalystComplex.setLabel(getCatalystIntermediateComplexLabel(reactants, catalyst));
		AcSpecies productCatalystComplex = new AcSpecies();
		productCatalystComplex.setLabel(getCatalystIntermediateComplexLabel(products, catalyst));
		
		Collection<AcReaction> massActionReactions = new ArrayList<AcReaction>();
		switch (massActionType) {
			case NoIntermediates:
				massActionReactions.add(createSimpleCatalReaction(reaction, catalyst));
				break;
			case CatalystSubstrateComplex:
				massActionReactions.add(
						createAssocReaction(reaction, substrateCatalystComplex, catalyst));
				massActionReactions.add(
						createDisassocReaction(reaction, substrateCatalystComplex, catalyst));
				break;				
			case CatalystSubstrateAndCatalystProductComplex:
				massActionReactions.add(
						createAssocReaction(reaction, substrateCatalystComplex, catalyst));
				massActionReactions.add(
						createCatalComplexTransReaction(reaction, substrateCatalystComplex, productCatalystComplex));
				massActionReactions.add(
						createDisassocReaction(reaction, productCatalystComplex, catalyst));
				break;
			default: throw new BndChemistryException("Mass action type '" + massActionType + "' not recognized.");
		}
		return massActionReactions;
	}

	// E + S1 + S2 = E + P1 + P2
	private AcReaction createSimpleCatalReaction(
		final AcReaction reaction,
		final AcSpecies catalyst
	) {
		final Collection<AcSpecies> reactants = acUtil.getSpecies(reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant));
		final Collection<AcSpecies> products = acUtil.getSpecies(reaction.getSpeciesAssociations(AcSpeciesAssociationType.Product));
		final double k_cat = reaction.getForwardRateConstants()[0];

		AcReaction simpleCatalReaction = new AcReaction();
		simpleCatalReaction.setLabel(reaction.getLabel());
		simpleCatalReaction.setEnabled(true);
		simpleCatalReaction.addAssociationsForSpecies(reactants, AcSpeciesAssociationType.Reactant);
		simpleCatalReaction.addAssociationForSpecies(catalyst, AcSpeciesAssociationType.Reactant);
		simpleCatalReaction.addAssociationsForSpecies(products, AcSpeciesAssociationType.Product);
		simpleCatalReaction.addAssociationForSpecies(catalyst, AcSpeciesAssociationType.Product);
		simpleCatalReaction.setForwardRateConstant(k_cat);
//		if (reaction.hasReverseRateConstants()) {
//			simpleCatalReaction.setReverseRateConstants(replicator.clone(reaction.getReverseRateConstants()));
//		}
		return simpleCatalReaction;
	}

	// E + S1 + S2 = COMPLEX
	private AcReaction createAssocReaction(
		final AcReaction reaction,
		final AcSpecies catalystComplex,
		final AcSpecies catalyst
	) {
		final Collection<AcSpecies> reactants = acUtil.getSpecies(reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant));
		final double k_cat = reaction.getForwardRateConstants()[0];
		final double K_d = reaction.getForwardRateConstants()[1];
		final double k_r = k_cat * 2;
		final double k_f = k_r / K_d;

		AcReaction catalAssocReaction = new AcReaction();
		catalAssocReaction.setLabel(reaction.getLabel() + "_A");
		catalAssocReaction.setEnabled(true);
		catalAssocReaction.addAssociationsForSpecies(reactants, AcSpeciesAssociationType.Reactant);
		catalAssocReaction.addAssociationForSpecies(catalyst, AcSpeciesAssociationType.Reactant);
		catalAssocReaction.addAssociationForSpecies(catalystComplex, AcSpeciesAssociationType.Product);
		// k_f
		catalAssocReaction.setForwardRateConstant(k_f);
		// k_r
		catalAssocReaction.setReverseRateConstant(k_r);	
		return catalAssocReaction;
	}

	// COMPLEX_ES = COMPLEX_EP
	private AcReaction createCatalComplexTransReaction(
		final AcReaction reaction,
		final AcSpecies substrateCatalystComplex,
		final AcSpecies productCatalystComplex
	) {
		AcReaction catalComplexTransReaction = new AcReaction();
		catalComplexTransReaction.setLabel(reaction.getLabel() + "_A-D");
		catalComplexTransReaction.setEnabled(true);
		catalComplexTransReaction.addAssociationForSpecies(substrateCatalystComplex, AcSpeciesAssociationType.Reactant);
		catalComplexTransReaction.addAssociationForSpecies(productCatalystComplex, AcSpeciesAssociationType.Product);
		// k_fast
		catalComplexTransReaction.setForwardRateConstant(K_FAST);
		return catalComplexTransReaction;
	}

	// COMPLEX = E + P1 + P2
	private AcReaction createDisassocReaction(
		final AcReaction reaction,
		final AcSpecies catalystComplex,
		final AcSpecies catalyst
	) {
		final Collection<AcSpecies> products = acUtil.getSpecies(reaction.getSpeciesAssociations(AcSpeciesAssociationType.Product));
		final double k_cat = reaction.getForwardRateConstants()[0];

		AcReaction catalDisassocReaction = new AcReaction();
		catalDisassocReaction.setLabel(reaction.getLabel() + "_D");
		catalDisassocReaction.setEnabled(true);
		catalDisassocReaction.addAssociationForSpecies(catalystComplex, AcSpeciesAssociationType.Reactant);
		catalDisassocReaction.addAssociationsForSpecies(products, AcSpeciesAssociationType.Product);
		catalDisassocReaction.addAssociationForSpecies(catalyst, AcSpeciesAssociationType.Product);
		// k_cat
		catalDisassocReaction.setForwardRateConstant(k_cat);
		return catalDisassocReaction;
	}

	private String getCatalystIntermediateComplexLabel(Collection<AcSpecies> species, AcSpecies catalyst) {
		Collection<AcSpecies> speciesAndCatalyst = new ArrayList<AcSpecies>();
		speciesAndCatalyst.addAll(species);
		speciesAndCatalyst.add(catalyst);
		return acUtil.getVariableLabelsAsString(speciesAndCatalyst, "");
	}
}