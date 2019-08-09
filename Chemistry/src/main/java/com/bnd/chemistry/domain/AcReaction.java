package com.bnd.chemistry.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.function.domain.Function;
import com.bnd.function.domain.FunctionHolder;
import com.bnd.chemistry.domain.AcSpeciesReactionAssociation.AcSpeciesReactionAssociationOrderComparator;

public class AcReaction extends TechnicalDomainObject implements FunctionHolder<Double, Double> {

	public enum ReactionDirection {
		Forward, Reverse, Both
	}

	private String label;
	private Integer priority;
	private Integer sortOrder;
	private Set<AcSpeciesReactionAssociation> speciesAssociations = new HashSet<AcSpeciesReactionAssociation>();
	private AcCollectiveSpeciesReactionAssociationType collectiveCatalysisType;
	private AcCollectiveSpeciesReactionAssociationType collectiveInhibitionType;
	private Function<Double, Double> forwardRateFunction;
	private Function<Double, Double> reverseRateFunction;
	private Double[] forwardRateConstants = new Double[0];
	private Double[] reverseRateConstants = new Double[0];
	private boolean enabled = true;

	private AcReactionSet reactionSet;
	private AcReactionGroup group;

	private Integer index;

    public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Set<AcSpeciesReactionAssociation> getSpeciesAssociations() {
		return speciesAssociations;
	}

	public void setSpeciesAssociations(Set<AcSpeciesReactionAssociation> speciesAssociations) {
		this.speciesAssociations = speciesAssociations;
	}

	public void initSpeciesAssociations() {
		this.speciesAssociations = new HashSet<AcSpeciesReactionAssociation>();
	}

	public void addAssociationForSpecies(AcSpecies species, AcSpeciesAssociationType type) {
		addSpeciesAssociation(new AcSpeciesReactionAssociation(species, type));
	}

	public void addSpeciesAssociation(AcSpeciesReactionAssociation specAssoc) {
		if (specAssoc.getOrder() == null) {
			final Integer maxOrder = getMaxSpeciesAssociationOrder(specAssoc.getType());
			if (maxOrder == null)
				specAssoc.setOrder(0);
			else 
				specAssoc.setOrder(maxOrder + 1);
		}
		speciesAssociations.add(specAssoc);
		specAssoc.setReaction(this);
	}

	private Integer getMaxSpeciesAssociationOrder(AcSpeciesAssociationType type) {
		Integer maxOrder = null;
		for (AcSpeciesReactionAssociation assoc : AcSpeciesReactionAssociation.filterType(speciesAssociations, type)) {
			if (assoc.getOrder() != null)
				if (maxOrder == null)
					maxOrder = assoc.getOrder();
				else
					maxOrder = Math.max(maxOrder, assoc.getOrder());
		}
		return maxOrder;		
	}

	public void addSpeciesAssociations(Collection<AcSpeciesReactionAssociation> specAssocs) {
		for (AcSpeciesReactionAssociation specAssoc : specAssocs) {
			addSpeciesAssociation(specAssoc);
		}
	}

	public void addAssociationsForSpecies(Collection<AcSpecies> species, AcSpeciesAssociationType type) {
		for (AcSpecies oneSpecies : species) {
			addAssociationForSpecies(oneSpecies, type);
		}
	}

	public void removeSpeciesAssociation(AcSpeciesReactionAssociation specAssoc) {
		speciesAssociations.remove(specAssoc);
		specAssoc.setReaction(null);
	}

	public void removeSpeciesAssociations(Collection<AcSpeciesReactionAssociation> specAssocs) {
		for (AcSpeciesReactionAssociation specAssoc : specAssocs) {
			removeSpeciesAssociation(specAssoc);
		}
	}

	public boolean hasSpeciesAssociations(AcSpeciesAssociationType assocType) {
		return !getSpeciesAssociations(assocType).isEmpty();
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public AcReactionSet getReactionSet() {
		return reactionSet;
	}

	protected void setReactionSet(AcReactionSet reactionSet) {
		this.reactionSet = reactionSet;
	}

	public boolean hasReactionSet() {
		return reactionSet != null;
	}

	public void removeFromReactionSet() {
		if (hasReactionSet()) {
			reactionSet.removeReaction(this);
		}
	}

	public AcReactionGroup getGroup() {
		return group;
	}

	protected void setGroup(AcReactionGroup group) {
		this.group = group;
	}

	public boolean hasGroup() {
		return group != null;
	}

	public void removeFromGroup() {
		if (hasGroup()) {
			group.removeReaction(this);
		}
	}

	public Function<Double, Double> getForwardRateFunction() {
		return forwardRateFunction;
	}

	public void setForwardRateFunction(Function<Double, Double> forwardRateFunction) {
		this.forwardRateFunction = forwardRateFunction;
	}

	public Double[] getForwardRateConstants() {
		return forwardRateConstants;
	}

	public Double[] getForwardRateConstantsSafe() {
		return forwardRateConstants != null ? forwardRateConstants : new Double[0];
	}

	public void setForwardRateConstants(Double[] forwardRateConstants) {
		this.forwardRateConstants = forwardRateConstants;
	}

	public boolean hasForwardRateConstants() {
		return forwardRateConstants != null && forwardRateConstants.length > 0;
	}

	public int getForwardRateConstantsNum() {
		return hasForwardRateConstants() ? forwardRateConstants.length : 0;
	}

	public void setForwardRateConstant(Double forwardRateConstant) {
		this.forwardRateConstants = new Double[] {forwardRateConstant};
	}

	public Function<Double, Double> getReverseRateFunction() {
		return reverseRateFunction;
	}

	public void setReverseRateFunction(Function<Double, Double> reverseRateFunction) {
		this.reverseRateFunction = reverseRateFunction;
	}

	public boolean hasReverseRateFunction() {
		return reverseRateFunction != null;
	}

	public Double[] getReverseRateConstants() {
		return reverseRateConstants;
	}

	public Double[] getReverseRateConstantsSafe() {
		return reverseRateConstants != null ? reverseRateConstants : new Double[0];
	}

	public void setReverseRateConstants(Double[] reverseRateConstants) {
		this.reverseRateConstants = reverseRateConstants;
	}

	public boolean hasReverseRateConstants() {
		return reverseRateConstants != null && reverseRateConstants.length > 0;
	}

	public int getReverseRateConstantsNum() {
		return hasReverseRateConstants() ? reverseRateConstants.length : 0;
	}

	public void setReverseRateConstant(Double reverseRateConstant) {
		this.reverseRateConstants = new Double[] {reverseRateConstant};
	}

	public Double[] getRateConstants(boolean forwardFlag) {
		return forwardFlag ? forwardRateConstants : reverseRateConstants;
	}

	public boolean hasRateConstants(boolean forwardFlag) {
		return forwardFlag ? hasForwardRateConstants() : hasReverseRateConstants();
	}

	public void setRateConstants(Double[] rateConstants, boolean forwardFlag) {
		if (forwardFlag) {
			setForwardRateConstants(rateConstants);
		} else {
			setReverseRateConstants(rateConstants);
		}
	}

	public int getRateConstantsNum(boolean forwardFlag) {
		return forwardFlag ? getForwardRateConstantsNum() : getReverseRateConstantsNum();
	}

	public Function<Double, Double> getRateFunction(boolean forwardFlag) {
		return forwardFlag ? forwardRateFunction : reverseRateFunction;
	}

	public void setRateFunction(Function<Double, Double> rateFunction, boolean forwardFlag) {
		if (forwardFlag) {
			setForwardRateFunction(rateFunction);
		} else {
			setReverseRateFunction(rateFunction);
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}	

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

    public AcSpeciesSet getSpeciesSet() {
		return reactionSet.getSpeciesSet();
	}

	public AcParameterSet getParameterSet() {
		return reactionSet.getParameterSet();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public AcCollectiveSpeciesReactionAssociationType getCollectiveCatalysisType() {
		if (collectiveCatalysisType == null) {
			return AcCollectiveSpeciesReactionAssociationType.OR;
		}
		return collectiveCatalysisType;
	}

	public void setCollectiveCatalysisType(AcCollectiveSpeciesReactionAssociationType collectiveCatalysisType) {
		this.collectiveCatalysisType = collectiveCatalysisType;
	}

	public AcCollectiveSpeciesReactionAssociationType getCollectiveInhibitionType() {
		if (collectiveInhibitionType == null) {
			return AcCollectiveSpeciesReactionAssociationType.OR;
		}
		return collectiveInhibitionType;
	}

	public void setCollectiveInhibitionType(AcCollectiveSpeciesReactionAssociationType collectiveInhibitionType) {
		this.collectiveInhibitionType = collectiveInhibitionType;
	}

	@Override
	public Function<Double, Double> getFunction() {
		return forwardRateFunction;
	}

	@Override
	public void setFunction(Function<Double, Double> function) {
		this.forwardRateFunction = function;
	}

	public List<AcSpeciesReactionAssociation> getSpeciesAssociations(AcSpeciesAssociationType assocType) {
		List<AcSpeciesReactionAssociation> orderedAssocs = new ArrayList<AcSpeciesReactionAssociation>();
		orderedAssocs.addAll(AcSpeciesReactionAssociation.filterType(speciesAssociations, assocType));
		Collections.sort(orderedAssocs, new AcSpeciesReactionAssociationOrderComparator());
		return orderedAssocs;
	}

	public void addSpeciesAssociation(AcSpeciesReactionAssociation specAssociation, AcSpeciesAssociationType assocType) {
		specAssociation.setType(assocType);
		if (!assocType.isReactantOrProduct()) {
			specAssociation.setStoichiometricFactor(null);
		}
		addSpeciesAssociation(specAssociation);
	}

	public void addSpeciesAssociations(Collection<AcSpeciesReactionAssociation> specAssociations, AcSpeciesAssociationType assocType) {
		AcSpeciesReactionAssociation.setType(specAssociations, assocType);
		if (!assocType.isReactantOrProduct()) {
			nullStoichiometricFactor(specAssociations);
		}
		addSpeciesAssociations(specAssociations);
	}

	public void setSpeciesAssociations(Collection<AcSpeciesReactionAssociation> specAssociations, AcSpeciesAssociationType assocType) {
		removeSpeciesAssociations(getSpeciesAssociations(assocType));
		addSpeciesAssociations(specAssociations, assocType);
	}

	private void nullStoichiometricFactor(Collection<AcSpeciesReactionAssociation> speciesAssociations) {
		for (AcSpeciesReactionAssociation speciesAssoc : speciesAssociations) {
			speciesAssoc.setStoichiometricFactor(null);
		}
	}

	public Integer getSpeciesAssociationsNum(AcSpeciesAssociationType assocType) {
		return getSpeciesAssociations(assocType).size();
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(label);
    	return sb.toString();
    }
}