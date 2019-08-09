package com.bnd.chemistry.domain;

import java.util.Date;
import java.util.List;

public class AcReactionGroup extends AcReactionContainer {

	private AcReactionSet reactionSet;

	public AcReactionSet getReactionSet() {
		return reactionSet;
	}

	protected void setReactionSet(AcReactionSet reactionSet) {
		this.reactionSet = reactionSet;
	}

	public boolean containsReaction(AcReaction reaction) {
		return getReactions().contains(reaction);
	}

	@Override
	public void addReaction(AcReaction reaction) {
		super.addReaction(reaction);
		reaction.setGroup(this);
	}

	@Override
	public void removeReaction(AcReaction reaction) {
		super.removeReaction(reaction);
		reaction.setGroup(null);
	}

	public List<AcReaction> getAllSetReactions() {
		return reactionSet.getReactions();
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