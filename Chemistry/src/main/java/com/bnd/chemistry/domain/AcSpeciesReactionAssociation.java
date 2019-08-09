package com.bnd.chemistry.domain;

import java.util.*;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.util.ObjectUtil;

public class AcSpeciesReactionAssociation extends TechnicalDomainObject {

	public static class AcSpeciesReactionAssociationIndexComparator implements Comparator<AcSpeciesReactionAssociation> {

		@Override
		public int compare(AcSpeciesReactionAssociation var1, AcSpeciesReactionAssociation var2) {
			return ObjectUtil.compareObjects(var1.getSpeciesIndex(), var2.getSpeciesIndex());
		}
	}

	public static class AcSpeciesReactionAssociationLabelComparator implements Comparator<AcSpeciesReactionAssociation> {

		@Override
		public int compare(AcSpeciesReactionAssociation var1, AcSpeciesReactionAssociation var2) {
			return ObjectUtil.compareObjects(var1.getSpeciesLabel(), var2.getSpeciesLabel());
		}
	}

	public static class AcSpeciesReactionAssociationOrderComparator implements Comparator<AcSpeciesReactionAssociation> {

		@Override
		public int compare(AcSpeciesReactionAssociation var1, AcSpeciesReactionAssociation var2) {
			if (var1.getOrder() == null || var2.getOrder() == null)
				return ObjectUtil.compareObjects(var1.getSpeciesLabel(), var2.getSpeciesLabel());
			return ObjectUtil.compareObjects(var1.getOrder(), var2.getOrder());
		}
	}

	private AcSpecies species;
	private AcReaction reaction;
	private Double stoichiometricFactor;
	private AcSpeciesAssociationType type;
	private Integer order;

	public AcSpeciesReactionAssociation() {
		super();
	}

	public AcSpeciesReactionAssociation(AcSpecies species, AcSpeciesAssociationType type) {
		this(species, type, type.isReactantOrProduct() ? 1.0 : null);
	}

	public AcSpeciesReactionAssociation(AcSpecies species, Double stoichiometricFactor) {
		this(species, null, stoichiometricFactor);
	}

	public AcSpeciesReactionAssociation(AcSpecies species, AcSpeciesAssociationType type, Double stoichiometricFactor) {
		super();
		this.species = species;
		this.stoichiometricFactor = stoichiometricFactor;
		this.type = type;
	}

	public AcSpecies getSpecies() {
		return species;
	}

	public void setSpecies(AcSpecies species) {
		this.species = species;
	}

	public Double getStoichiometricFactor() {
		return stoichiometricFactor;
	}

	public void setStoichiometricFactor(Double stoichiometricFactor) {
		this.stoichiometricFactor = stoichiometricFactor;
	}

	public AcSpeciesAssociationType getType() {
		return type;
	}

	public void setType(AcSpeciesAssociationType type) {
		this.type = type;
	}

	public AcReaction getReaction() {
		return reaction;
	}

	protected void setReaction(AcReaction reaction) {
		this.reaction = reaction;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getSpeciesLabel() {
		return species.getLabel();
	}

	public Integer getSpeciesIndex() {
		return species.getVariableIndex();
	}

	public Long getSpeciesId() {
		return species.getId();
	}

	public AcSpeciesAssociation getSpeciesAssociation() {
		return new AcSpeciesAssociation(species, type);
	}

	public static void setType(Collection<AcSpeciesReactionAssociation> speciesAssociations, AcSpeciesAssociationType assocType) {
		for (AcSpeciesReactionAssociation speciesAssoc : speciesAssociations) {
			speciesAssoc.setType(assocType);
		}
	}

	public static Set<AcSpeciesReactionAssociation> filterType(Set<AcSpeciesReactionAssociation> speciesAssociations, AcSpeciesAssociationType assocType) {
		Set<AcSpeciesReactionAssociation> selectedSpeciesAssocs = new HashSet<AcSpeciesReactionAssociation>();
		for (AcSpeciesReactionAssociation speciesAssoc : speciesAssociations) {
			if (assocType == null || speciesAssoc.getType() == assocType) {
				selectedSpeciesAssocs.add(speciesAssoc);
			}
		}
		return selectedSpeciesAssocs;
	}

	public static Collection<AcSpeciesReactionAssociation> createCollection(Collection<AcSpecies> species, AcSpeciesAssociationType assocType) {
		Collection<AcSpeciesReactionAssociation> speciesAssocs = new ArrayList<AcSpeciesReactionAssociation>();
		for (AcSpecies oneSpecies : species) {
			speciesAssocs.add(new AcSpeciesReactionAssociation(oneSpecies, assocType));
		}
		return speciesAssocs;
	}

	@Override
	public String toString() {
		return species.getLabel();
	}
}