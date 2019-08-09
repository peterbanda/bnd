package com.bnd.chemistry.domain;

import java.io.Serializable;

public class AcReactionSetConstraints implements Serializable {

	private AcReactionToSpeciesConstraints reactionToSpeciesConstraints;
	private AcSpeciesToReactionConstraints speciesToReactionConstraints;
	private Double reactionsPerSpeciesRatio;
	private AcSpeciesAssociationType reactionsPerSpeciesRatioAssocType;   // null means for all

	public AcReactionSetConstraints(
		AcReactionToSpeciesConstraints reactionToSpeciesConstraints,
		AcSpeciesToReactionConstraints speciesToReactionConstraints,
		Double reactionsPerSpeciesRatio,
		AcSpeciesAssociationType reactionsPerSpeciesRatioAssocType
	) {
		this(reactionToSpeciesConstraints, speciesToReactionConstraints, reactionsPerSpeciesRatio);
		this.reactionsPerSpeciesRatioAssocType = reactionsPerSpeciesRatioAssocType;
	}

	public AcReactionSetConstraints(
		AcReactionToSpeciesConstraints reactionToSpeciesConstraints,
		AcSpeciesToReactionConstraints speciesToReactionConstraints,
		Double reactionsPerSpeciesRatio
	) {
		this();
		this.reactionToSpeciesConstraints = reactionToSpeciesConstraints;
		this.speciesToReactionConstraints = speciesToReactionConstraints;
		this.reactionsPerSpeciesRatio = reactionsPerSpeciesRatio;
	}

	public AcReactionSetConstraints() {
		// no-op
	}

	public AcReactionToSpeciesConstraints getReactionToSpeciesConstraints() {
		return reactionToSpeciesConstraints;
	}

	public void setReactionToSpeciesConstraints(AcReactionToSpeciesConstraints reactionToSpeciesConstraints) {
		this.reactionToSpeciesConstraints = reactionToSpeciesConstraints;
	}

	public AcSpeciesToReactionConstraints getSpeciesToReactionConstraints() {
		return speciesToReactionConstraints;
	}

	public void setSpeciesToReactionConstraints(AcSpeciesToReactionConstraints speciesToReactionConstraints) {
		this.speciesToReactionConstraints = speciesToReactionConstraints;
	}

	public Double getReactionsPerSpeciesRatio() {
		return reactionsPerSpeciesRatio;
	}

	public void setReactionsPerSpeciesRatio(Double reactionsPerSpeciesRatio) {
		this.reactionsPerSpeciesRatio = reactionsPerSpeciesRatio;
	}

	public AcSpeciesAssociationType getReactionsPerSpeciesRatioAssocType() {
		return reactionsPerSpeciesRatioAssocType;
	}

	public void setReactionsPerSpeciesRatioAssocType(AcSpeciesAssociationType reactionsPerSpeciesRatioAssocType) {
		this.reactionsPerSpeciesRatioAssocType = reactionsPerSpeciesRatioAssocType;
	}

	public Integer getReactionsNum(int speciesNum, AcSpeciesAssociationType assocType) {
		if (assocType == reactionsPerSpeciesRatioAssocType) {
			return (int) Math.floor(speciesNum * reactionsPerSpeciesRatio);
		}
		return 0;
	}

	public void copyFrom(AcReactionSetConstraints rsConstraints) {
		reactionToSpeciesConstraints = rsConstraints.reactionToSpeciesConstraints;
		speciesToReactionConstraints = rsConstraints.speciesToReactionConstraints;
		reactionsPerSpeciesRatio = rsConstraints.reactionsPerSpeciesRatio;
		reactionsPerSpeciesRatioAssocType = rsConstraints.reactionsPerSpeciesRatioAssocType;
	}

	// TODO: move to acUtil
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Reactions / Species Ratio :");
		sb.append(reactionsPerSpeciesRatio);
		if (reactionsPerSpeciesRatioAssocType != null) {
			sb.append(" (");			
			sb.append(reactionsPerSpeciesRatioAssocType);
			sb.append(")");
		}
		sb.append("\n----------------\n");
		sb.append("R-to-S:\n");
		sb.append(reactionToSpeciesConstraints.toString());
		sb.append("\nS-to-R:\n");
		sb.append(speciesToReactionConstraints.toString());
		return sb.toString();
	}
}