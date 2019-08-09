package com.bnd.chemistry.business;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.bnd.chemistry.domain.*;
import com.bnd.core.util.ObjectUtil;
import com.bnd.function.business.FunctionUtility;

class AcMichaelisMentenKineticsBO extends AcMassActionKineticsBO {

	private final FunctionUtility functionUtility = new FunctionUtility();
	private static final String NAME = "Michaelis-Menten";

	private final int catalystsNum;
	private final int inhibitorsNum;

	protected AcMichaelisMentenKineticsBO(AcReaction reaction, boolean forwardReactionFlag, Collection<AcParameter> substitutionParameters) {
		super(reaction, forwardReactionFlag, substitutionParameters);
		this.catalystsNum = getCatalystAssocs().size();
		this.inhibitorsNum = getInhibitorAssocs().size();
	}

	@Override
	public void validate() {
		super.validate();
		if ((catalystsNum + inhibitorsNum) > 0 && getReactantsNum() == 0) {
			// TODO: What if reaction has some catalysts / inhibitors, but no reactants?
			// throwMissingAttributeException("reactants");
		}
	}

	@Override
	public int getRequiredRateConstantsNum() {
		int requiredConstansNum = 1;
		if (catalystsNum > 0) {
			requiredConstansNum++;
			if (getReactantsNum() > 1) {
				requiredConstansNum += getReactantsNum();
			}
		}
		if (inhibitorsNum > 0) {
			requiredConstansNum++;
		}
		return requiredConstansNum;
	}

	@Override
	public Collection<AcRateConstantType> getRateConstantTypes() {
//		validate();
		Collection<AcRateConstantType> reactionRateConstantTypes = new ArrayList<AcRateConstantType>();
		reactionRateConstantTypes.add(AcRateConstantType.Global);
		if (catalystsNum > 0) {
			reactionRateConstantTypes.add(AcRateConstantType.CatalysisGeneral);
			int catalystTermsNum = 0;
			if (getReactantsNum() > 1) {
				catalystTermsNum = getReactantsNum();
			}
			for (int i = 0; i < catalystTermsNum; i++) {
				reactionRateConstantTypes.add(AcRateConstantType.CatalysisSubstrate);
			}
		}
		if (inhibitorsNum > 0) {
			reactionRateConstantTypes.add(AcRateConstantType.NoncooperativeInhibitition);
		}
		return reactionRateConstantTypes;
	}

	@Override
	public String createRateFunctionExpressionWithoutConstants() {
		validate();
		StringBuilder sb = new StringBuilder();
		if (catalystsNum == 0) {
			// no catalysts - mass-action kinetics is used
			sb.append(super.createRateFunctionExpressionWithoutConstants());
		} else {
			sb.append(createRateFunctionForMMCatalystsWithoutConstants());
		}
		if (inhibitorsNum > 0) {
			sb.append(createRateFunctionForLinearUncompetitiveInhibitorsWithoutConstants());
		}
		return sb.toString();
	}

	/**
	 * k0 * Sum [Ei] * Product [Si] / (k1 + Sum k_(i+1)[S_i] + Product [Si])
	 */
	private String createRateFunctionForMMCatalystsWithoutConstants() {
		// Reactants product
		String reactantsProductString = getAcUtil().createFoldOrderedExpression(getReactantAssocs(), " * ", false);

		// Nominator
		Collection<String> nominatorParts = new ArrayList<String>();
		nominatorParts.add("%s");
		nominatorParts.add(getCatalystsExpression());
		nominatorParts.add(reactantsProductString);
		final String nominator = StringUtils.join(nominatorParts, " * ");

		// Denominator 
		Collection<String> denominatorParts = new ArrayList<String>();
		denominatorParts.add("%s");		
		if (getReactantsNum() > 1) {
			for (AcSpeciesReactionAssociation reactantAssoc : getReactantAssocs()) {
				StringBuilder sb = new StringBuilder();
				sb.append("%s");
				sb.append(" * ");
				sb.append(getAcUtil().getVariablePlaceholder(reactantAssoc));
				denominatorParts.add(sb.toString());
			}
		}
		denominatorParts.add(reactantsProductString);
		final String denominator =  StringUtils.join(denominatorParts, " + ");
		return nominator + " / (" + denominator + ")";
	}

	/**
	 * .. 1 / (1 + km Sum [Ii])
	 */
	private String createRateFunctionForLinearUncompetitiveInhibitorsWithoutConstants() {
		StringBuilder sb = new StringBuilder();
		sb.append(" / (1 + ");
		sb.append("%s");
		sb.append(" * ");
		sb.append(getInhibitorsExpression());
		sb.append(")");
		return sb.toString();
	}

	/**
	 * .. 1 / (1 + km Sum [Ii])
	 */
	@Deprecated
	private String createRateFunctionForLinearUncompetitiveInhibitors() {
		int requiredRateConstantsNum = getRequiredRateConstantsNum();
		StringBuilder sb = new StringBuilder();
		sb.append(" / (1 + ");
		sb.append(getRateConstant(requiredRateConstantsNum - 1));
		sb.append(" * ");
		sb.append(getInhibitorsExpression());
		sb.append(")");
		return sb.toString();
	}


	/**
	 * k0 * Sum [Ei] * Product [Si] / (k1 + Sum k_(i+1)[S_i] + Product [Si])
	 */
	@Deprecated
	private String createRateFunctionForMMCatalysts() {
		final Double[] rateConstants = getRateConstants();

		// Reactants product
		String reactantsProductString = getAcUtil().createFoldOrderedExpression(getReactantAssocs(), " * ", false);

		// Nominator
		Collection<String> nominatorParts = new ArrayList<String>();
		nominatorParts.add(rateConstants[0].toString());
		nominatorParts.add(getCatalystsExpression());
		nominatorParts.add(reactantsProductString);
		final String nominator = StringUtils.join(nominatorParts, " * ");

		// Denominator 
		Collection<String> denominatorParts = new ArrayList<String>();
		denominatorParts.add(rateConstants[1].toString());		
		int rateConstantsIndex = 2;
		if (getReactantsNum() > 1) {
			for (AcSpeciesReactionAssociation reactantAssoc : getReactantAssocs()) {
				StringBuilder sb = new StringBuilder();
				sb.append(rateConstants[rateConstantsIndex++]);
				sb.append(" * ");
				sb.append(getAcUtil().getVariablePlaceholder(reactantAssoc));
				denominatorParts.add(sb.toString());
			}
		}
		denominatorParts.add(reactantsProductString);
		final String denominator =  StringUtils.join(denominatorParts, " + ");
		return nominator + " / (" + denominator + ")";
	}

	@Override
	public Collection<Integer> getGuardVariableIndeces() {
		Collection<Integer> guardVariableIndeces = super.getGuardVariableIndeces();
		if (getCatalystAssocs().size() == 1) {
			guardVariableIndeces.add(ObjectUtil.getFirst(getCatalystAssocs()).getSpeciesIndex());
		} else if (getCatalystAssocs().size() > 1) {
			if (getReaction().getCollectiveCatalysisType() == AcCollectiveSpeciesReactionAssociationType.AND) {
				//  AND relation catalysts
				for (AcSpeciesReactionAssociation catalystAssoc : getCatalystAssocs()) {
					guardVariableIndeces.add(catalystAssoc.getSpeciesIndex());
				}
			} else {
				// OR relation catalysts - used if there is dedicated parameter for underlaying expression
				String catalystExpression = getAcUtil().createFoldExpression(getCatalystAssocs(), AcCollectiveSpeciesReactionAssociationType.OR, false);
				Integer paramIndex = getParameterIndex(catalystExpression);
				if (paramIndex != null) {
					guardVariableIndeces.add(paramIndex);
				}
			}
		}  
		return guardVariableIndeces;
	}

	private String getCatalystsExpression() {
		return getCollectiveExpression(getReaction().getCollectiveCatalysisType(), getCatalystAssocs());
	}

	private String getInhibitorsExpression() {
		return getCollectiveExpression(getReaction().getCollectiveInhibitionType(), getInhibitorAssocs());
	}

	private String getCollectiveExpression(
		AcCollectiveSpeciesReactionAssociationType collectiveType,
		Collection<AcSpeciesReactionAssociation> speciesAssocs
	) {
		String collectiveExpression = getAcUtil().createFoldExpression(speciesAssocs, collectiveType, false);
		if (speciesAssocs.size() > 1 && collectiveType == AcCollectiveSpeciesReactionAssociationType.OR) {
			// we check whether some parameter can substitute given sum expression
			Integer paramIndex = getParameterIndex(collectiveExpression);
			if (paramIndex != null) {
				return functionUtility.getVariablePlaceHolder(paramIndex);
			}
		}
		return getAcUtil().wrapIfNeeded(collectiveExpression, getInhibitorAssocs());
	}

	protected int getCatalystsNum() {
		return catalystsNum;
	}

	protected int getInhibitorsNum() {
		return inhibitorsNum;
	}

	@Override
	public String getKineticsName() {
		return NAME;
	}
}