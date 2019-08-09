package com.bnd.chemistry.domain;

import java.util.*;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;
import com.bnd.core.util.ObjectUtil;

public class AcCompartment extends TechnicalDomainObject {

	private String label;
	private Date createTime = new Date();
	private User createdBy;

	private AcReactionSet reactionSet;

	private List<AcCompartmentAssociation> parentCompartmentAssociations = new ArrayList<AcCompartmentAssociation>();
	private List<AcCompartmentAssociation> subCompartmentAssociations = new ArrayList<AcCompartmentAssociation>();
	private Collection<AcCompartmentChannel> channels = new ArrayList<AcCompartmentChannel>();
	private Collection<AcCompartmentChannelGroup> subChannelGroups = new ArrayList<AcCompartmentChannelGroup>();

	private ArtificialChemistrySpec generatedBySpec;

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

	public List<AcCompartmentAssociation> getSubCompartmentAssociations() {
		return subCompartmentAssociations;
	}

	public void setSubCompartmentAssociations(List<AcCompartmentAssociation> subCompartmentAssociations) {
		this.subCompartmentAssociations = subCompartmentAssociations;
	}

	public void removeSubCompartmentAssociation(AcCompartmentAssociation subCompartmentAssociation) {
		subCompartmentAssociations.remove(subCompartmentAssociation);
		subCompartmentAssociation.getSubCompartment().parentCompartmentAssociations.remove(subCompartmentAssociation);
	}

	private Integer getNextSubCompartmentOrder() {
		if (subCompartmentAssociations.isEmpty()) {
			return 0;
		}
		int maxOrder = 0;
		for (AcCompartmentAssociation subCompartmentAssociation : subCompartmentAssociations) {
			maxOrder = Math.max(subCompartmentAssociation.getOrder(), maxOrder);
		}
		return maxOrder + 1;
	}

	public List<AcCompartmentAssociation> getParentCompartmentAssociations() {
		return parentCompartmentAssociations;
	}

	public void setParentCompartmentAssociations(List<AcCompartmentAssociation> parentCompartmentAssociations) {
		this.parentCompartmentAssociations = parentCompartmentAssociations;
	}

	public Collection<AcCompartment> getSubCompartments() {
		Collection<AcCompartment> subCompartments = new ArrayList<AcCompartment>();
		Collections.sort(subCompartmentAssociations);
		for (AcCompartmentAssociation subCompartmentAssociation : subCompartmentAssociations) {
			subCompartments.add(subCompartmentAssociation.getSubCompartment());
		}
		return subCompartments;
	}

	public Collection<AcCompartment> getParentCompartments() {
		Collection<AcCompartment> parentCompartments = new ArrayList<AcCompartment>();
		for (AcCompartmentAssociation subCompartmentAssociation : parentCompartmentAssociations) {
			parentCompartments.add(subCompartmentAssociation.getParentCompartment());
		}
		return parentCompartments;
	}

	public Collection<AcSpecies> getParentCompartmentSpecies() {
		Collection<AcSpecies> parentSpecies = new ArrayList<AcSpecies>();
		for (AcCompartmentAssociation subCompartmentAssociation : parentCompartmentAssociations) {
			Collection<AcSpecies> newSpecies = subCompartmentAssociation.getParentCompartment().getSpecies();
			if (parentSpecies.isEmpty()) {
				parentSpecies.addAll(newSpecies);
			} else {
				parentSpecies.retainAll(newSpecies);
			}
		}
		return parentSpecies;
	}

	public void addSubCompartment(AcCompartment subCompartment) {
		AcCompartmentAssociation subCompartmentAssociation = new AcCompartmentAssociation();
		subCompartmentAssociation.setOrder(getNextSubCompartmentOrder());
		subCompartmentAssociation.setParentCompartment(this);
		subCompartmentAssociation.setSubCompartment(subCompartment);
		subCompartmentAssociations.add(subCompartmentAssociation);
		subCompartment.parentCompartmentAssociations.add(subCompartmentAssociation);
	}

	public void removeSubCompartment(AcCompartment subCompartment) {
		Collection<AcCompartmentAssociation> subCompartmentAssociationsCopy = new ArrayList<AcCompartmentAssociation>(subCompartmentAssociations);
		for (AcCompartmentAssociation subCompartmentAssociation : subCompartmentAssociationsCopy) {
			if (ObjectUtil.areObjectsEqual(subCompartmentAssociation.getSubCompartment(), subCompartment)) {
				subCompartmentAssociations.remove(subCompartmentAssociation);
				subCompartment.parentCompartmentAssociations.remove(subCompartmentAssociation);
			}
		}
	}

	public void removeParentCompartment(AcCompartment parentCompartment) {
		Collection<AcCompartmentAssociation> parentCompartmentAssociationsCopy = new ArrayList<AcCompartmentAssociation>(parentCompartmentAssociations);
		for (AcCompartmentAssociation parentCompartmentAssociation : parentCompartmentAssociationsCopy) {
			if (ObjectUtil.areObjectsEqual(parentCompartmentAssociation.getParentCompartment(), parentCompartment)) {
				parentCompartmentAssociations.remove(parentCompartmentAssociation);
				parentCompartment.subCompartmentAssociations.remove(parentCompartmentAssociation);
			}
		}
	}

	public AcReactionSet getReactionSet() {
		return reactionSet;
	}

	public void setReactionSet(AcReactionSet reactionSet) {
		this.reactionSet = reactionSet;
	}

	public AcSpeciesSet getSpeciesSet() {
		return reactionSet.getSpeciesSet();
	}

	public Collection<AcSpecies> getSpecies() {
		return reactionSet.getSpecies();
	}

	public int getSpeciesCount() {
		return getSpecies().size();
	}
	
	public Collection<AcParameter> getParameters() {
		return reactionSet.getParameters();
	}

	public int getParametersCount() {
		return getParameters().size();
	}
	
	public Collection<AcCompartmentChannel> getChannels() {
		return channels;
	}

	public void setChannels(Collection<AcCompartmentChannel> channels) {
		this.channels = channels;
	}

	public void addChannel(AcCompartmentChannel channel) {
		channels.add(channel);
		channel.setCompartment(this);
	}

	public void removeChannel(AcCompartmentChannel channel) {
		channels.remove(channel);
		channel.setCompartment(null);
	}
	
	public Collection<AcCompartmentChannelGroup> getSubChannelGroups() {
		return subChannelGroups;
	}

	public void setSubChannelGroups(Collection<AcCompartmentChannelGroup> subChannelGroups) {
		this.subChannelGroups = subChannelGroups;
	}

	public void addSubChannelGroup(AcCompartmentChannelGroup channelGroup) {
		subChannelGroups.add(channelGroup);
		channelGroup.setCompartment(this);
	}

	public void removeSubChannelGroup(AcCompartmentChannelGroup channelGroup) {
		subChannelGroups.remove(channelGroup);
		channelGroup.setCompartment(null);
	}

	public ArtificialChemistrySpec getGeneratedBySpec() {
		return generatedBySpec;
	}

	public void setGeneratedBySpec(ArtificialChemistrySpec generatedBySpec) {
		this.generatedBySpec = generatedBySpec;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if (label != null) {
    		sb.append(label);
    	} else {
    		sb.append("<no name>");
    	}
    	return sb.toString();
    }
}