package com.bnd.chemistry.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;

import com.bnd.chemistry.domain.AcParameter;
import com.bnd.chemistry.domain.AcReaction;
import com.bnd.chemistry.domain.AcRateConstantType;
import com.bnd.chemistry.domain.AcSpeciesReactionAssociation;

class AcMassActionKineticsBO extends AcKineticsBO {

	private static final String NAME = "Mass-Action";

	protected AcMassActionKineticsBO(AcReaction reaction, boolean forwardReactionFlag, Collection<AcParameter> substitutionParameters) {
		super(reaction, forwardReactionFlag, substitutionParameters);
	}

	@Override
	public void validate() {
		super.validate();
	}

	@Override
	public int getRequiredRateConstantsNum() {
		return 1;
	}

	/**
	 * k * Product [Si]
	 */
	@Override
	public String createRateFunctionExpressionWithoutConstants() {
		validate();
		Collection<String> expressionParts = new ArrayList<String>();
		expressionParts.add("%s");
		for (AcSpeciesReactionAssociation reactantAssoc : getReactantAssocs()) {
			StringBuilder sb = new StringBuilder();
			sb.append(getAcUtil().getVariablePlaceholder(reactantAssoc));
			if (reactantAssoc.getStoichiometricFactor() != 1) {
				sb.append("^");
				sb.append(reactantAssoc.getStoichiometricFactor());
			}
			expressionParts.add(sb.toString());
		}
		return StringUtils.join(expressionParts, " * ");
	}

	@Override
	public Collection<AcRateConstantType> getRateConstantTypes() {
//		validate();
		return Collections.singleton(AcRateConstantType.Global);
	}

	@Override
	public Collection<Integer> getGuardVariableIndeces() {
		Collection<Integer> guardVariableIndeces = new HashSet<Integer>();
		for (AcSpeciesReactionAssociation reactantAssoc : getReactantAssocs()) {
			guardVariableIndeces.add(reactantAssoc.getSpeciesIndex());
		}
		return guardVariableIndeces;
	}

	@Override
	public String getKineticsName() {
		return NAME;
	}
}