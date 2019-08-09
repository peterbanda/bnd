package com.bnd.chemistry.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;

public abstract class AcReactionContainer extends TechnicalDomainObject {

	private String label;
	private Date createTime = new Date();
	private User createdBy;
	private List<AcReaction> reactions = new ArrayList<AcReaction>();

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public List<AcReaction> getReactions() {
		return reactions;
	}

	public int getReactionsNum() {
		return reactions != null ? reactions.size() : 0;
	}

	public void initReactions() {
		this.reactions = new ArrayList<AcReaction>();
	}

	public void setReactions(List<AcReaction> reactions) {
		this.reactions = reactions;
	}
	
	public void addReaction(AcReaction reaction) {
		reactions.add(reaction);
	}

	public void removeReaction(AcReaction reaction) {
		reactions.remove(reaction);
	}

	public void removeAllReactions() {
		Collection<AcReaction> reactionsCopy = new ArrayList<AcReaction>();
		reactionsCopy.addAll(reactions);
		for (AcReaction reaction : reactionsCopy) {
			removeReaction(reaction);
		}
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if (label != null) {
    		sb.append(label);
    		sb.append("/");
    	}
    	sb.append(reactions.size());
    	return sb.toString();
    }
}