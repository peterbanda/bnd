package com.bnd.chemistry.business;

import java.util.*;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.chemistry.domain.*;
import com.bnd.function.domain.Expression;
import com.bnd.function.domain.Function;

public abstract class AcKineticsBO {

	private final AcReaction reaction;
	private final Double[] rateConstants;
	private final Collection<AcSpeciesReactionAssociation> reactantAssocs;
	private final Collection<AcSpeciesReactionAssociation> productAssocs;
	private final Collection<AcSpeciesReactionAssociation> catalystAssocs;
	private final Collection<AcSpeciesReactionAssociation> inhibitorAssocs;
	private final int reactantsNum;
	private final int productsNum;
	private final Map<String, Integer> expressionParameterIndexMap;

	private final ArtificialChemistryUtil acUtil = ArtificialChemistryUtil.getInstance();

	protected AcKineticsBO(AcReaction reaction, boolean forwardReactionFlag, Collection<AcParameter> substitutionParameters) {
		this.reaction = reaction;
		if (reaction == null) {
			throw new BndChemistryException("No reaction defined for AC kinetics calculation!");
		}
		if (forwardReactionFlag) {
			this.rateConstants =  reaction.getForwardRateConstants();
			this.reactantAssocs = reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant);
			this.productAssocs = reaction.getSpeciesAssociations(AcSpeciesAssociationType.Product);
			this.catalystAssocs = reaction.getSpeciesAssociations(AcSpeciesAssociationType.Catalyst);
			this.inhibitorAssocs = reaction.getSpeciesAssociations(AcSpeciesAssociationType.Inhibitor);
		} else { 
			this.rateConstants = reaction.getReverseRateConstants();
			this.reactantAssocs = reaction.getSpeciesAssociations(AcSpeciesAssociationType.Product);
			this.productAssocs = reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant);
			 // TODO: Do we have catalysts or inhibitors if we are going reverse direction?
			this.catalystAssocs = new HashSet<AcSpeciesReactionAssociation>(); 
			this.inhibitorAssocs = new HashSet<AcSpeciesReactionAssociation>();
		}

		this.expressionParameterIndexMap = createExpressionParameterIndexMap(substitutionParameters);
		this.reactantsNum = reactantAssocs.size();
		this.productsNum = productAssocs.size();
	}

	public static Map<String, Integer> createExpressionParameterIndexMap(Collection<AcParameter> substitutionParameters) {
		Map<String, Integer> expressionParameterIndexMap = new HashMap<String, Integer>();
		if (substitutionParameters != null) {
			for (AcParameter parameter : substitutionParameters) {
				expressionParameterIndexMap.put(parameter.getEvolFunction().toString(), parameter.getVariableIndex());
			}
		}
		return expressionParameterIndexMap;
	}

	public void validate() {
		if (!hasRateConstantsNum()) {
			throwMissingAttributeException("rate constants");
		}
		int requiredRateConstantsNum = getRequiredRateConstantsNum();
		if (getRateConstantsNum() < requiredRateConstantsNum) {
			throw new BndChemistryException("Not enough rate constants  '" + getRateConstantsNum() + "' defined for reaction '" + reaction.getId() + "'.");
		}
		if (getRateConstantsNum() > requiredRateConstantsNum) {
			throw new BndChemistryException("Too many rate constants  '" + getRateConstantsNum() + "' defined for reaction '" + reaction.getId() + "'.");
		}
//		if (reactantsNum == 0 && productsNum == 0) {
//			throw new BndChemistryException("No reactants, nor products defined for reaction '" + reaction.getId() + "' but expected.");
//		}
	}

	public static AcKineticsBO createInstance(AcReaction reaction, boolean forwardReactionFlag) {
		return createInstance(reaction, forwardReactionFlag, null);
	}

	public static AcKineticsBO createInstance(AcReaction reaction, boolean forwardReactionFlag, Collection<AcParameter> substitutionParameters) {
		// AcKineticsBO acKineticsBO = new AcMassActionKineticsBO(reaction, forwardReactionFlag, substitutionParameters);
		return new AcMichaelisMentenKineticsBO(reaction, forwardReactionFlag, substitutionParameters);
	}

	public Function<Double, Double> createRateFunction() {
		return Expression.Double(createRateFunctionExpression());
	}

	public Function<Double, Double> createRateFunctionWithoutConstants() {
		return Expression.Double(createRateFunctionExpressionWithoutConstants());
	}

	public String createRateFunctionExpression() {
		String[] rateConstantStrings = new String[getRateConstants().length];
		for (int i = 0; i < rateConstantStrings.length; i++) {
			rateConstantStrings[i] = getRateConstants()[i].toString();
		}
		return String.format(createRateFunctionExpressionWithoutConstants(), rateConstantStrings);
	}

	public abstract int getRequiredRateConstantsNum();

	public abstract String createRateFunctionExpressionWithoutConstants();

	public abstract Collection<AcRateConstantType> getRateConstantTypes();

	public abstract String getKineticsName();

	public abstract Collection<Integer> getGuardVariableIndeces();

	protected Double getRateConstant(int index) {
		return rateConstants[index];
	}

	protected Double[] getRateConstants() {
		return rateConstants;
	}

	protected int getRateConstantsNum() {
		return rateConstants != null ? rateConstants.length : 0;
	}

	protected boolean hasRateConstantsNum() {
		return getRateConstantsNum() != 0;
	}

	protected Long getReactionId() {
		return reaction.getId();
	}

	protected AcReaction getReaction() {
		return reaction;
	}

	protected Collection<AcSpeciesReactionAssociation> getReactantAssocs() {
		return reactantAssocs;
	}

	protected Collection<AcSpeciesReactionAssociation> getProductAssocs() {
		return productAssocs;
	}

	protected Collection<AcSpeciesReactionAssociation> getCatalystAssocs() {
		return catalystAssocs;
	}

	protected Collection<AcSpeciesReactionAssociation> getInhibitorAssocs() {
		return inhibitorAssocs;
	}

	protected int getReactantsNum() {
		return reactantsNum;
	}

	protected int getProductsNum() {
		return productsNum;
	}

	protected Integer getParameterIndex(String expression) {
		return expressionParameterIndexMap.get(expression);
	}

	protected ArtificialChemistryUtil getAcUtil() {
		return acUtil;
	}

	protected void throwMissingAttributeException(String missingAttribute) {
		throw new BndChemistryException("No " + missingAttribute + " defined for reaction '" + getReactionId() + "' but expected. Problem found while evaluating the " + getKineticsName() + " kinetics.");
	}
}