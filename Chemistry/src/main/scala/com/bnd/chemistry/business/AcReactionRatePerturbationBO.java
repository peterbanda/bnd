package com.bnd.chemistry.business;

import java.util.Collection;

import com.bnd.chemistry.domain.AcReaction;
import com.bnd.chemistry.domain.AcReactionSet;
import com.bnd.core.util.RandomUtil;


public class AcReactionRatePerturbationBO {

	private final Collection<AcReaction> reactions;
	private final double perturbationStrength;

	public AcReactionRatePerturbationBO(AcReactionSet reactionSet, double pertrubationStrength) {
		this(reactionSet.getReactions(), pertrubationStrength);
	}

	public AcReactionRatePerturbationBO(Collection<AcReaction> reactions, double pertrubationStrength) {
		this.reactions = reactions;
		this.perturbationStrength = pertrubationStrength;
	}

	public void perturbate() {
		for (AcReaction reaction : reactions) { 
			for (int i = 0; i < reaction.getForwardRateConstantsNum(); i++) {
				reaction.getForwardRateConstants()[i] = RandomUtil.perturbate(reaction.getForwardRateConstants()[i], perturbationStrength, 0d, null);
			}
			for (int i = 0; i < reaction.getReverseRateConstantsNum(); i++) {
				reaction.getReverseRateConstants()[i] = RandomUtil.perturbate(reaction.getReverseRateConstants()[i], perturbationStrength, 0d, null);
			}
		}
	}

	public Collection<AcReaction> getReactions() {
		return reactions;
	}
}