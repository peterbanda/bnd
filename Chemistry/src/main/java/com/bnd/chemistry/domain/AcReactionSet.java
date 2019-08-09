package com.bnd.chemistry.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class AcReactionSet extends AcReactionContainer {

	private AcSpeciesSet speciesSet;
	private List<AcReactionGroup> groups = new ArrayList<AcReactionGroup>();

	public void addReaction(AcReaction reaction) {
		super.addReaction(reaction);
		if (reaction.getSortOrder() == null) {
			reaction.setSortOrder(getReactionsNum() - 1);
		}
		reaction.setReactionSet(this);
	}

	public void addReactions(Collection<AcReaction> reactions) {
		for (final AcReaction reaction : reactions) {
			addReaction(reaction);
		}
	}

	public void removeReaction(AcReaction reaction) {
		super.removeReaction(reaction);
		reaction.setReactionSet(null);
	}

    public AcSpeciesSet getSpeciesSet() {
		return speciesSet;
	}
   
	public void setSpeciesSet(AcSpeciesSet speciesSet) {
		this.speciesSet = speciesSet;
	}

	public AcParameterSet getParameterSet() {
		return speciesSet.getParameterSet();
	}

	public Collection<AcSpecies> getSpecies() {
		return speciesSet.getOwnAndInheritedVariables();
	}

	public Collection<AcParameter> getParameters() {
		return getParameterSet().getOwnAndInheritedVariables();
	}

	public List<AcReactionGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<AcReactionGroup> groups) {
		this.groups = groups;
	}

	public boolean hasGroups() {
		return !groups.isEmpty();
	}

	public void initGroups() {
		this.groups = new ArrayList<AcReactionGroup>();
	}

	public void addGroup(AcReactionGroup group) {
		groups.add(group);
		group.setReactionSet(this);
	}

	public void removeGroup(AcReactionGroup group) {
		groups.remove(group);
		group.setReactionSet(null);
	}

	// kept for grails so can be detected it as a property hook
	@Override
	public String getLabel() {
		return super.getLabel();
	}

	// kept for grails so can be detected it as a property hook
	@Override
	public void setLabel(String label) {
		super.setLabel(label);
	}

	// kept for grails so can be detected it as a property hook
	@Override
	public Date getCreateTime() {
		return super.getCreateTime(); 
	}

	// kept for grails so can be detected it as a property hook
	@Override
	public void setCreateTime(Date createTime) {
		super.setCreateTime(createTime);
	}
}