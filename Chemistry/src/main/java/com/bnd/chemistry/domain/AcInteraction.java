package com.bnd.chemistry.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.bnd.core.domain.runnable.StateAction;

public class AcInteraction extends StateAction {

	private Set<AcSpeciesInteraction> speciesActions = new HashSet<AcSpeciesInteraction>();
	private Set<AcInteractionVariableAssignment> variableAssignments = new HashSet<AcInteractionVariableAssignment>();
	private AcInteractionSeries actionSeries;

	public Set<AcSpeciesInteraction> getSpeciesActions() {
		return speciesActions;
	}

	public void initSpeciesActions() {
		this.speciesActions = new HashSet<AcSpeciesInteraction>();
	}

	protected void setSpeciesActions(Set<AcSpeciesInteraction> speciesActions) {
		this.speciesActions = speciesActions;
	}

	public void addToSpeciesActions(AcSpeciesInteraction speciesAction) {
		speciesActions.add(speciesAction);
		speciesAction.setAction(this);
	}

	public void addSpeciesActions(Collection<AcSpeciesInteraction> speciesActions) {
		for (AcSpeciesInteraction speciesAction : speciesActions) {
			addToSpeciesActions(speciesAction);
		}
	}

	public void removeFromSpeciesActions(AcSpeciesInteraction speciesAction) {
		speciesActions.remove(speciesAction);
		speciesAction.setAction(null);
	}

	public void removeAllSpeciesActions() {
		Collection<AcSpeciesInteraction> speciesActionsCopy = new ArrayList<AcSpeciesInteraction>();
		speciesActionsCopy.addAll(speciesActions);
		for (AcSpeciesInteraction speciesAction : speciesActionsCopy) {
			removeFromSpeciesActions(speciesAction);
		}
	}

	public Set<AcInteractionVariableAssignment> getVariableAssignments() {
		return variableAssignments;
	}

	public void setVariableAssignments(Set<AcInteractionVariableAssignment> variableAssignments) {
		this.variableAssignments = variableAssignments;
	}

	public void addVariableAssignment(AcInteractionVariableAssignment variableAssignment) {
		variableAssignments.add(variableAssignment);
		variableAssignment.setAction(this);
	}

	public void addVariableAssignments(Collection<AcInteractionVariableAssignment> variableAssignments) {
		for (AcInteractionVariableAssignment variableAssignment : variableAssignments) {
			addVariableAssignment(variableAssignment);
		}
	}

	public void removeVariableAssignment(AcInteractionVariableAssignment variableAssignment) {
		variableAssignments.remove(variableAssignment);
		variableAssignment.setAction(null);
	}

	public void removeAllVariableAssignments() {
		Collection<AcInteractionVariableAssignment> variableAssignmentsCopy = new ArrayList<AcInteractionVariableAssignment>();
		variableAssignmentsCopy.addAll(variableAssignments);
		for (AcInteractionVariableAssignment variableAssignment : variableAssignmentsCopy) {
			removeVariableAssignment(variableAssignment);
		}
	}

	public AcInteractionSeries getActionSeries() {
		return actionSeries;
	}

	protected void setActionSeries(AcInteractionSeries actionSeries) {
		this.actionSeries = actionSeries;
	}

	public AcSpeciesSet getSpeciesSet() {
		return actionSeries != null ? actionSeries.getSpeciesSet() : null;
	}

	public Collection<AcSpecies> getSpecies() {
		final AcSpeciesSet speciesSet = getSpeciesSet();
		return speciesSet != null ? speciesSet.getOwnAndInheritedVariables() : null;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if (actionSeries != null) {
        	sb.append(actionSeries.getName());
        	sb.append("/");
    	}
    	sb.append(getStartTime());
    	return sb.toString();
    }
}