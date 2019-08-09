package com.bnd.chemistry.business;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.chemistry.domain.*;
import com.bnd.chemistry.domain.AcReaction.ReactionDirection;
import com.bnd.core.domain.ValueBound;
import com.bnd.core.util.ObjectUtil;
import com.bnd.core.util.ParseUtil;
import com.bnd.core.util.RandomUtil;

public class AcRateConstantUtil {

	private static final AcRateConstantUtil instance = new AcRateConstantUtil();

	private AcRateConstantUtil() {
		// no-op
	}

	public static AcRateConstantUtil getInstance() {
		return instance;
	}

	public Collection<ValueBound<Double>> getRateConstantBounds(
		AcCompartment compartment,
		Map<AcRateConstantType, ValueBound<Double>> rateConstantTypeBoundMap,
		ReactionDirection reactionDirection,
		Collection<AcReaction> reactionsToOmit,
		Collection<AcReactionGroup> reactionGroupsToOmit
	) {
		if (reactionsToOmit == null) {
			reactionsToOmit = new HashSet<AcReaction>();
		}
		if (reactionGroupsToOmit == null) {
			reactionGroupsToOmit = new HashSet<AcReactionGroup>();
		}
		final ValueBound<Double> permeabilityValueBound = rateConstantTypeBoundMap.get(AcRateConstantType.Permeability);
		Collection<ValueBound<Double>> bounds = new ArrayList<ValueBound<Double>>();
		final List<AcReactionSet> reactionSets = getReactionSets(compartment);
		for (AcReactionSet reactionSet : reactionSets) {
			bounds.addAll(getRateConstantBounds(reactionSet, rateConstantTypeBoundMap, reactionDirection, reactionsToOmit, reactionGroupsToOmit));
		}
		final List<AcCompartmentChannelGroup> subCompartmentChannelGroups = getSubCompartmentChannelGroups(compartment);
		final List<AcCompartmentChannel> compartmentChannels = getCompartmentChannels(compartment);
		for (AcCompartmentChannelGroup channelGroup : subCompartmentChannelGroups) {
			bounds.add(permeabilityValueBound);
			for (AcCompartmentChannel channel : channelGroup.getChannels()) {
				compartmentChannels.remove(channel);
			}
		}
		for (AcCompartmentChannel channel : compartmentChannels) {
			bounds.add(permeabilityValueBound);
		}
		return bounds;
	}

	public Collection<ValueBound<Double>> getRateConstantBounds(
		AcReactionSet reactionSet,
		Map<AcRateConstantType, ValueBound<Double>> rateConstantTypeBoundMap,
		ReactionDirection reactionDirection,
		Collection<AcReaction> reactionsToOmit,
		Collection<AcReactionGroup> reactionGroupsToOmit
	) {
		if (reactionsToOmit == null) {
			reactionsToOmit = new HashSet<AcReaction>();
		}
		if (reactionGroupsToOmit == null) {
			reactionGroupsToOmit = new HashSet<AcReactionGroup>();
		}
		if (!reactionSet.hasGroups()) {
			return getRateConstantBounds(reactionSet.getReactions(), rateConstantTypeBoundMap, reactionDirection, reactionsToOmit);
		}
		return getRateConstantBounds(
				getRepresentativeReactions(reactionSet, reactionGroupsToOmit),
				rateConstantTypeBoundMap, reactionDirection, Collections.EMPTY_LIST);
	}

	private Collection<ValueBound<Double>> getRateConstantBounds(
		Collection<AcReaction> reactions,
		Map<AcRateConstantType, ValueBound<Double>> rateConstantTypeBoundMap,
		ReactionDirection reactionDirection,
		Collection<AcReaction> reactionsToOmit
	) {
		Collection<ValueBound<Double>> rateBounds = new ArrayList<ValueBound<Double>>();
		for (AcReaction reaction : reactions)
			if (!reactionsToOmit.contains(reaction))
				rateBounds.addAll(getRateConstantBounds(reaction, rateConstantTypeBoundMap, reactionDirection));
		return rateBounds;
	}

	public Collection<ValueBound<Double>> getRateConstantBounds(
		AcReaction reaction,
		Map<AcRateConstantType, ValueBound<Double>> rateConstantTypeBoundMap,
		ReactionDirection reactionDirection
	) {
		if (rateConstantTypeBoundMap == null) {
			rateConstantTypeBoundMap = AcRateConstantType.getDefaultRateConstantBoundMap();
		}
		Collection<ValueBound<Double>> reactionRateConstantValueBounds = new ArrayList<ValueBound<Double>>();
		Collection<AcRateConstantType> rateConstantTypes = new ArrayList<AcRateConstantType>();
		if (reactionDirection != ReactionDirection.Reverse) {
			rateConstantTypes.addAll(getReactionRateConstantType(reaction, true));
		}
		if (reactionDirection != ReactionDirection.Forward) {
			rateConstantTypes.addAll(getReactionRateConstantType(reaction, false));
		}
		for (AcRateConstantType rateConstantType : rateConstantTypes) {
			final ValueBound<Double> rateBound = rateConstantTypeBoundMap.get(rateConstantType);
			reactionRateConstantValueBounds.add(rateBound);
		}
		return reactionRateConstantValueBounds;
	}

	private Collection<AcRateConstantType> getReactionRateConstantType(
		AcReaction reaction, boolean forwardFlag
	) {
		Collection<AcRateConstantType> rateConstantTypes = new ArrayList<AcRateConstantType>();
		if (reaction.getRateFunction(forwardFlag) == null) {
			if (reaction.hasRateConstants(forwardFlag)) {
				rateConstantTypes.addAll(AcKineticsBO.createInstance(reaction, forwardFlag).getRateConstantTypes());
			}
		} else {
			for (int i = 0; i < reaction.getRateConstantsNum(forwardFlag); i++) {
				rateConstantTypes.add(AcRateConstantType.Global);
			}
		}
		return rateConstantTypes;
	}

	public void setRandomRateConstants(
		AcReactionSet reactionSet,
		Map<AcRateConstantType, ValueBound<Double>> rateConstantTypeBoundMap
	) {
		for (AcReaction reaction : reactionSet.getReactions()) {
			setRandomRateConstants(reaction, rateConstantTypeBoundMap, true);
			if (reaction.hasReverseRateConstants()) {
				setRandomRateConstants(reaction, rateConstantTypeBoundMap, false);
			}
		}
	}

	public void setRandomRateConstantsIfNotSet(
		AcReactionSet reactionSet,
		Map<AcRateConstantType, ValueBound<Double>> rateConstantTypeBoundMap,
		ReactionDirection reactionDirection
	) {
		for (AcReaction reaction : reactionSet.getReactions()) {
			if (reactionDirection != ReactionDirection.Reverse && !reaction.hasForwardRateConstants()) {
				setRandomRateConstants(reaction, rateConstantTypeBoundMap, true);
			}
			if (reactionDirection != ReactionDirection.Forward && !reaction.hasReverseRateConstants()) {
				setRandomRateConstants(reaction, rateConstantTypeBoundMap, false);
			}
		}
	}

	public void setRandomRateConstants(
		AcReaction reaction,
		Map<AcRateConstantType, ValueBound<Double>> rateConstantTypeBoundMap,
		boolean forwardFlag
	) {
		Collection<ValueBound<Double>> reactionRateConstantValueBounds = getRateConstantBounds(
				reaction, rateConstantTypeBoundMap,
				forwardFlag ? ReactionDirection.Forward : ReactionDirection.Reverse);
		Double[] rateConstants = new Double[reactionRateConstantValueBounds.size()];
		int i = 0;
		for (ValueBound<Double> rateValueBound : reactionRateConstantValueBounds) {
			rateConstants[i] = RandomUtil.next(Double.class, rateValueBound);
			i++;
		}
		reaction.setRateConstants(rateConstants, forwardFlag);
	}

	public Collection<AcReaction> getRepresentativeReactions(
		AcReactionSet reactionSet,
		Collection<AcReactionGroup> reactionGroupsToOmit
	) {
		Collection<AcReaction> representativeReactions = new ArrayList<AcReaction>();
		for (AcReactionGroup reactionGroup : reactionSet.getGroups()) {
			if (reactionGroup.getReactions().isEmpty()) {
				throw new BndChemistryException("Each reaction group must have at least one reaction.");
			}
			if (!reactionGroupsToOmit.contains(reactionGroup))
				representativeReactions.add(ObjectUtil.getFirst(reactionGroup.getReactions()));
		}
		return representativeReactions;
	}

	public void setRateConstants(
		AcCompartment compartment,
		Double[] rateAndPermeabilityConstants,
		ReactionDirection direction,
		Collection<AcReaction> reactionsToOmit,
		Collection<AcReactionGroup> reactionGroupsToOmit
	) {
		if (reactionsToOmit == null) {
			reactionsToOmit = new HashSet<AcReaction>();
		}
		if (reactionGroupsToOmit == null) {
			reactionGroupsToOmit = new HashSet<AcReactionGroup>();
		}
		final List<AcReactionSet> reactionSets = getReactionSets(compartment);
		int rateConstantIndex = 0;
		for (AcReactionSet reactionSet : reactionSets) {
			rateConstantIndex = setRateConstants(reactionSet, rateAndPermeabilityConstants, rateConstantIndex, direction, reactionsToOmit, reactionGroupsToOmit);
		}

		final List<AcCompartmentChannelGroup> subCompartmentChannelGroups = getSubCompartmentChannelGroups(compartment);
		final List<AcCompartmentChannel> compartmentChannels = getCompartmentChannels(compartment);
		for (AcCompartmentChannelGroup channelGroup : subCompartmentChannelGroups) {
			final Double permeability = rateAndPermeabilityConstants[rateConstantIndex];
			rateConstantIndex++;
			for (AcCompartmentChannel channel : channelGroup.getChannels()) {
				channel.setPermeability(permeability);
				compartmentChannels.remove(channel);
			}
		}

		for (AcCompartmentChannel channel : compartmentChannels) {
			channel.setPermeability(rateAndPermeabilityConstants[rateConstantIndex]);
			rateConstantIndex++;
		}

		if (rateConstantIndex < rateAndPermeabilityConstants.length) {
			throw new BndChemistryException("Too many rate & permeability rate constants provided : '" + rateAndPermeabilityConstants.length + "' vs. '" + rateConstantIndex + "'.");
		}
	}

	private List<AcReactionSet> getReactionSets(AcCompartment compartment) {
		List<AcReactionSet> reactionSets = new ArrayList<AcReactionSet>();
		reactionSets.add(compartment.getReactionSet());
		for (AcCompartment subCompartment : compartment.getSubCompartments()) {
			reactionSets.addAll(getReactionSets(subCompartment));
		}
		return new ArrayList<AcReactionSet>(new LinkedHashSet<AcReactionSet>(reactionSets));
	}

	private List<AcCompartmentChannel> getCompartmentChannels(AcCompartment compartment) {
		List<AcCompartmentChannel> compartmentChannels = new ArrayList<AcCompartmentChannel>();
		List<AcCompartment> compartments = getCompartments(compartment);
		compartments.remove(compartment);
		for (AcCompartment oneCompartment : compartments) {
			compartmentChannels.addAll(oneCompartment.getChannels());
		}
		return compartmentChannels;
	}

	private List<AcCompartmentChannelGroup> getSubCompartmentChannelGroups(AcCompartment compartment) {
		List<AcCompartmentChannelGroup> compartmentChannelGroups = new ArrayList<AcCompartmentChannelGroup>();
		for (AcCompartment oneCompartment : getCompartments(compartment)) {
			compartmentChannelGroups.addAll(oneCompartment.getSubChannelGroups());
		}
		return compartmentChannelGroups;
	}

	private List<AcCompartment> getCompartments(AcCompartment compartment) {
		List<AcCompartment> compartments = new ArrayList<AcCompartment>();
		compartments.add(compartment);
		for (AcCompartment subCompartment : compartment.getSubCompartments()) {
			compartments.addAll(getCompartments(subCompartment));
		}
		return new ArrayList<AcCompartment>(new LinkedHashSet<AcCompartment>(compartments));
	}

	public void setRateConstants(
		AcReactionSet reactionSet,
		Double[] rateConstants,
		ReactionDirection direction,
		Collection<AcReaction> reactionsToOmit,
		Collection<AcReactionGroup> reactionGroupsToOmit
	) {
		if (reactionsToOmit == null) {
			reactionsToOmit = new HashSet<AcReaction>();
		}
		if (reactionGroupsToOmit == null) {
			reactionGroupsToOmit = new HashSet<AcReactionGroup>();
		}
		int rateConstantIndex = setRateConstants(reactionSet, rateConstants, 0, direction, reactionsToOmit, reactionGroupsToOmit);
		if (rateConstantIndex < rateConstants.length) {
			throw new BndChemistryException("The number of rate constants provided '" + rateConstants.length + "' is too large.");
		}
	}

	private int setRateConstants(
		AcReactionSet reactionSet,
		Double[] rateConstants,
		int rateConstantIndex,
		ReactionDirection direction,
		Collection<AcReaction> reactionsToOmit,
		Collection<AcReactionGroup> reactionGroupsToOmit
	) {
		if (reactionSet.hasGroups()) {
			return setRateConstantsWithGroups(reactionSet, rateConstants,rateConstantIndex, direction, reactionGroupsToOmit);
		}
		for (AcReaction reaction : reactionSet.getReactions())
			if (!reactionsToOmit.contains(reaction))
				rateConstantIndex = copyReactionRates(reaction, rateConstants,rateConstantIndex, direction);

		return rateConstantIndex;
	}

	private int setRateConstantsWithGroups(
		AcReactionSet reactionSet,
		Double[] rateConstants, int rateConstantIndex,
		ReactionDirection direction,
		Collection<AcReactionGroup> reactionGroupsToOmit
	) {
		for (final AcReactionGroup reactionGroup : reactionSet.getGroups())
			if (!reactionGroupsToOmit.contains(reactionGroup)) {
				Integer newRateConstantIndex = null;
				for (final AcReaction reaction : reactionGroup.getReactions()) {
					int tempRateConstantIndex = copyReactionRates(reaction, rateConstants, rateConstantIndex, direction);
					if (newRateConstantIndex == null) {
						newRateConstantIndex = tempRateConstantIndex;
					} else if (newRateConstantIndex != tempRateConstantIndex) {
						throw new BndChemistryException("Reactions in the group '" + reactionGroup.getId() + "' do not share the same number of constants.");
					}
				}
				rateConstantIndex = newRateConstantIndex;
				if (rateConstantIndex > rateConstants.length) {
					throw new BndChemistryException("The number of rate constants provided '" + rateConstants.length + "' is not sufficient.");
				}
			}

		return rateConstantIndex;
	}

	private int copyReactionRates(
		AcReaction reaction,
		Double[] newRateConstants,
		int rateConstantIndex,
		ReactionDirection direction
	) {
		if (direction != ReactionDirection.Reverse && reaction.hasForwardRateConstants()) {
			System.arraycopy(newRateConstants, rateConstantIndex, reaction.getForwardRateConstants(), 0, reaction.getForwardRateConstantsNum());
			rateConstantIndex += reaction.getForwardRateConstantsNum();
		}
		if (direction != ReactionDirection.Forward && reaction.hasReverseRateConstants()) {
			System.arraycopy(newRateConstants, rateConstantIndex, reaction.getReverseRateConstants(), 0, reaction.getReverseRateConstantsNum());
			rateConstantIndex += reaction.getReverseRateConstantsNum();
		}
		return rateConstantIndex;
	}

	// string parsing functions

	public void setRateConstantsFromString(
		String rateConstantsString,
		AcCompartment compartment,
		ReactionDirection reactionDirection,
		Collection<AcReaction> reactionsToOmit,
		Collection<AcReactionGroup> reactionGroupsToOmit
	) {
		if (StringUtils.isBlank(rateConstantsString)) {
			return;
		}
		Collection<Double> rateConstants = ParseUtil.parseArray(rateConstantsString, Double.class, "Rate constants", ",");
		setRateConstants(compartment, rateConstants.toArray(new Double[0]), reactionDirection, reactionsToOmit, reactionGroupsToOmit);
	}

	public void setRateConstantsFromString(
		String rateConstantsString,
		AcReactionSet reactionSet,
		ReactionDirection reactionDirection,
		Collection<AcReaction> reactionsToOmit,
		Collection<AcReactionGroup> reactionGroupsToOmit
	) {
		if (StringUtils.isBlank(rateConstantsString)) {
			return;
		}
		Collection<Double> rateConstants = ParseUtil.parseArray(rateConstantsString, Double.class, "Rate constants", ",");
		setRateConstants(reactionSet, rateConstants.toArray(new Double[0]), reactionDirection, reactionsToOmit, reactionGroupsToOmit);
	}

	public void setRateConstantsFromString(String rateConstantsString, AcReaction reaction, ReactionDirection reactionDirection) {
		boolean forwardFlag = reactionDirection == ReactionDirection.Forward;
		if (StringUtils.isBlank(rateConstantsString)) {
			reaction.setRateConstants(new Double[0], forwardFlag);
			return;
		}
		Collection<Double> rateConstants = ParseUtil.parseArray(rateConstantsString, Double.class, "Rate constants", ",");
		reaction.setRateConstants(rateConstants.toArray(new Double[0]), forwardFlag);
	}

}