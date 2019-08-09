package com.bnd.chemistry.domain;

import java.util.*;

import com.bnd.core.domain.runnable.BaseManipulationSeries;

public class AcInteractionSeries extends BaseManipulationSeries implements AcVariableSetIF<AcInteractionVariable> {

	private AcSpeciesSet speciesSet;
	private Collection<AcInteractionVariable> variables = new HashSet<AcInteractionVariable>();
	private Integer varSequenceNum = 0;
	private Collection<AcSpecies> immutableSpecies = new HashSet<AcSpecies>();

	private Set<AcInteraction> actions = new HashSet<AcInteraction>();

	private AcInteractionSeries parent;
	private Collection<AcInteractionSeries> subActionSeries = new ArrayList<AcInteractionSeries>();

	public AcInteractionSeries() {
		super();
	}

	public AcInteractionSeries(Long id) {
		super(id);
	}

	public AcSpeciesSet getSpeciesSet() {
		return speciesSet;
	}

	public void setSpeciesSet(AcSpeciesSet speciesSet) {
		this.speciesSet = speciesSet;
	}

	public Collection<AcSpecies> getSpecies() {
		return speciesSet.getOwnAndInheritedVariables();
	}

	public Set<AcInteraction> getActions() {
		return actions;
	}

	public void initActions() {
		this.actions = new HashSet<AcInteraction>();
	}

	protected void setActions(Set<AcInteraction> actions) {
		this.actions = actions;
	}

	public void addAction(AcInteraction action) {
		actions.add(action);
		action.setActionSeries(this);
	}

	public void addActions(Collection<AcInteraction> actions) {
		for (AcInteraction action : actions) {
			addAction(action);
		}
	}

	public void removeAction(AcInteraction action) {
		actions.remove(action);
		action.setActionSeries(null);
	}

	public void removeAllActions() {
		Collection<AcInteraction> actionsCopy = new ArrayList<AcInteraction>();
		actionsCopy.addAll(actions);
		for (AcInteraction action : actionsCopy) {
			removeAction(action);
		}
	}

	public Collection<AcSpecies> getImmutableSpecies() {
		return immutableSpecies;
	}

	public void setImmutableSpecies(Collection<AcSpecies> immutableSpecies) {
		this.immutableSpecies = immutableSpecies;
	}

	public void addImmutableSpecies(AcSpecies species) {
		immutableSpecies.add(species);
	}

	public void removeImmutableSpecies(AcSpecies species) {
		immutableSpecies.remove(species);
	}

	public AcInteractionSeries getParent() {
		return parent;
	}

	public void setParent(AcInteractionSeries parent) {
		this.parent = parent;
	}

	public Collection<AcInteractionSeries> getSubActionSeries() {
		return subActionSeries;
	}

	public void setSubActionSeries(Collection<AcInteractionSeries> subActionSeries) {
		this.subActionSeries = subActionSeries;
	}

	public void addSubActionSeries(AcInteractionSeries subActionSeries) {
		this.subActionSeries.add(subActionSeries);
		subActionSeries.setParent(this);
	}

	public void removeSubActionSeries(AcInteractionSeries subActionSeries) {
		this.subActionSeries.remove(subActionSeries);
		subActionSeries.setParent(null);
	}

	public void removeAllSubActionSeries() {
		Collection<AcInteractionSeries> subInteractionSeriesCopy = new ArrayList<AcInteractionSeries>();
		subInteractionSeriesCopy.addAll(subActionSeries);
		for (AcInteractionSeries subInteractionSeries : subInteractionSeriesCopy) {
			removeSubActionSeries(subInteractionSeries);
		}
	}

	/**
	 * Overridden for Hibernate mapping only.
	 */
	@Override
	public Integer getRepeatFromElement() {
		return super.getRepeatFromElement();
	}

	/**
	 * Overridden for Hibernate mapping only.
	 */
	@Override
	public void setRepeatFromElement(Integer repeatFromElement) {
		super.setRepeatFromElement(repeatFromElement);
	}

	public Integer getVarSequenceNum() {
		return varSequenceNum;
	}

	public void setVarSequenceNum(Integer sequenceNum) {
		this.varSequenceNum = sequenceNum;
	}

	@Override
	public Integer getNextVarSequenceNum() {
		return varSequenceNum++;
	}

	@Override
	public Collection<AcInteractionVariable> getVariables() {
		return variables;
	}

	@Override
	public void setVariables(Collection<AcInteractionVariable> variable) {
		this.variables = variable;
	}

	@Override
	public void addVariable(AcInteractionVariable variable) {
		if (variable.getVariableIndex() == null) {
			variable.setVariableIndex(getNextVarSequenceNum());
		}
		variables.add(variable);
		variable.setParentSetUnsafe(this);
	}

	public void addVariable(String variableLabel) {
		addVariable(new AcInteractionVariable(variableLabel));
	}

	@Override
	public void addVariables(Collection<AcInteractionVariable> variables) {
		for (AcInteractionVariable variable : variables) {
			addVariable(variable);
		}
	}

	@Override
	public void removeVariable(AcInteractionVariable variable) {
		variables.remove(variable);
		variable.setParentSet(null);
	}

	@Override
	public void initVariables() {
		this.variables = new HashSet<AcInteractionVariable>();
	}

	@Override
	public Collection<AcInteractionVariable> getOwnAndInheritedVariables() {
		// Inheritance not supported, hence return just variables
		return variables;
	}
}