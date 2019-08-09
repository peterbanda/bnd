package com.bnd.chemistry.domain;

import java.util.*;

import com.bnd.chemistry.domain.AcVariable.AcVariableIndexComparator;

public class AcSpeciesSet extends AcVariableSet<AcSpecies> {

	private static final int MAX_HIERARCHY_LEVEL_ALLOWED = 5;

	// TODO: Thread-safe??
	private Integer varSequenceNum = 0;
	private AcSpeciesSet parentSpeciesSet;
	private AcParameterSet parameterSet;

	private Map<String, AcSpeciesGroup> speciesGroupMap = new HashMap<String, AcSpeciesGroup>();;

	public AcSpeciesSet() {
		for (AcSpeciesType type : AcSpeciesType.values()) {
			speciesGroupMap.put(type.toString(), new AcSpeciesGroup(type.toString()));
		}
	}

	public AcParameterSet getParameterSet() {
		return parameterSet;
	}

	public void setParameterSet(AcParameterSet parameterSet) {
		this.parameterSet = parameterSet;
	}

	public Integer getVarSequenceNum() {
		return varSequenceNum;
	}

	public void setVarSequenceNum(Integer sequenceNum) {
		this.varSequenceNum = sequenceNum;
	}

	public AcSpeciesSet getParentSpeciesSet() {
		return parentSpeciesSet;
	}

	public void setParentSpeciesSet(AcSpeciesSet parentSpeciesSet) {
		this.parentSpeciesSet = parentSpeciesSet;
	}

	public boolean hasParentSpeciesSet() {
		return parentSpeciesSet != null;
	}

	public int getLevel() {
		if (hasParentSpeciesSet()) {
			return 1 + parentSpeciesSet.getLevel();			
		}
		return 0;
	}

	public Map<String, AcSpeciesGroup> getSpeciesGroupMap() {
		return speciesGroupMap;
	}

	public void setSpeciesGroupMap(Map<String, AcSpeciesGroup> speciesGroupMap) {
		this.speciesGroupMap = speciesGroupMap;
	}

	@Override
	public Collection<AcSpecies> getOwnAndInheritedVariables() {
		Collection<AcSpecies> ownAndInheritesVariables = new ArrayList<AcSpecies>();
		ownAndInheritesVariables.addAll(getVariables());
		if (parentSpeciesSet != null) {
			ownAndInheritesVariables.addAll(parentSpeciesSet.getOwnAndInheritedVariables());
		}
		return ownAndInheritesVariables;
	}

	@Override
	public int getVariablesNumber() {
		return getOwnAndInheritedVariables().size();
	}

	public void initVarSequenceNum() {
		varSequenceNum = getLevel();
	}

	@Override
	public Integer getNextVarSequenceNum() {
		final Integer oldVarSequenceNum = varSequenceNum;
		varSequenceNum += MAX_HIERARCHY_LEVEL_ALLOWED;
		return oldVarSequenceNum;
	}

	public AcSpeciesGroup getSpeciesGroup(AcSpeciesType type) {
		return speciesGroupMap.get(type.toString());
	}

	public void addSpeciesToGroup(AcSpeciesType type, AcSpecies species) {
		final AcSpeciesGroup speciesGroup = getSpeciesGroup(type);
		speciesGroup.addVariable(species);
	}

	public Collection<AcSpecies> getSpecies(AcSpeciesType type) {
		AcSpeciesGroup group = speciesGroupMap.get(type.toString());
		return group != null ? group.getVariables() : new ArrayList<AcSpecies>();
	}

	public Collection<AcSpecies> getOwnAndInheritedSpecies(AcSpeciesType type) {
		Collection<AcSpecies> ownAndInheritedSpecies = new ArrayList<AcSpecies>();
		ownAndInheritedSpecies.addAll(getSpecies(type));
		if (parentSpeciesSet != null) {
			ownAndInheritedSpecies.addAll(parentSpeciesSet.getOwnAndInheritedSpecies(type));
		}
		return ownAndInheritedSpecies;
	}

	public int getSpeciesNumber(AcSpeciesType type) {
		return getSpecies(type).size();
	}

	public boolean hasSpecies(AcSpeciesType type) {
		return !getSpecies(type).isEmpty();
	}

	public Collection<AcSpecies> getNonInputFeedbackSpecies() {
		Collection<AcSpecies> nonInputSpecies = new HashSet<AcSpecies>();
		nonInputSpecies.addAll(getVariables());
		nonInputSpecies.removeAll(getSpecies(AcSpeciesType.Input));
		nonInputSpecies.removeAll(getSpecies(AcSpeciesType.Feedback));
		return nonInputSpecies;
	}

	public Collection<AcSpecies> getNonInternalSpecies() {
		Collection<AcSpecies> nonInputSpecies = new HashSet<AcSpecies>();
		nonInputSpecies.addAll(getSpecies(AcSpeciesType.Input));
		nonInputSpecies.addAll(getSpecies(AcSpeciesType.Output));
		nonInputSpecies.addAll(getSpecies(AcSpeciesType.Feedback));
		nonInputSpecies.addAll(getSpecies(AcSpeciesType.Functional));
		return nonInputSpecies;
	}

	public Collection<AcSpecies> getInternalSpecies() {
		Collection<AcSpecies> nonInputOutputSpecies = getNonInputFeedbackSpecies();
		nonInputOutputSpecies.removeAll(getSpecies(AcSpeciesType.Output));
		nonInputOutputSpecies.removeAll(getSpecies(AcSpeciesType.Functional));
		return nonInputOutputSpecies;
	}

	private Collection<AcSpecies> sortSpecies(Collection<AcSpecies> species) {
		List<AcSpecies> sortedSpecies = new ArrayList<AcSpecies>();
		sortedSpecies.addAll(species);
		Collections.sort(sortedSpecies, new AcVariableIndexComparator());
		return sortedSpecies;
	}

	public static void main(String[] args) {
		AcSpeciesSet parentSpeciesSet = new AcSpeciesSet();
		AcParameterSet parentParamSet = new AcParameterSet();
		parentSpeciesSet.initVarSequenceNum();
		parentSpeciesSet.setParameterSet(parentParamSet);
		parentParamSet.setSpeciesSet(parentSpeciesSet);

		AcSpeciesSet speciesSet = new AcSpeciesSet();
		AcParameterSet paramSet = new AcParameterSet();
		speciesSet.setParameterSet(paramSet);
		paramSet.setSpeciesSet(speciesSet);

		AcSpeciesSet childSpeciesSet = new AcSpeciesSet();
		AcParameterSet childParamSet = new AcParameterSet();
		childSpeciesSet.setParameterSet(childParamSet);
		childParamSet.setSpeciesSet(childSpeciesSet);

		speciesSet.setParentSpeciesSet(parentSpeciesSet);
		childSpeciesSet.setParentSpeciesSet(speciesSet);
		speciesSet.initVarSequenceNum();
		childSpeciesSet.initVarSequenceNum();

		AcSpecies species1 = new AcSpecies();
		AcSpecies species2 = new AcSpecies();
		AcParameter param1 = new AcParameter();
		parentSpeciesSet.addVariable(species1);
		parentSpeciesSet.addVariable(species2);
		parentParamSet.addVariable(param1);

		AcSpecies species3 = new AcSpecies();
		AcSpecies species4 = new AcSpecies();
		AcSpecies species5 = new AcSpecies();
		speciesSet.addVariable(species3);
		speciesSet.addVariable(species4);
		speciesSet.addVariable(species5);

		AcParameter param3 = new AcParameter();
		AcParameter param4 = new AcParameter();
		childParamSet.addVariable(param3);
		childParamSet.addVariable(param4);

		System.out.println("Species set 1:");
		for (AcVariable<?> var : parentSpeciesSet.getVariables()) {
			System.out.print(var.getVariableIndex() + " ");
		}
		System.out.println("");
		System.out.println("");
		System.out.println("Parameter set 1:");
		for (AcVariable<?> var : parentParamSet.getVariables()) {
			System.out.print(var.getVariableIndex() + " ");
		}
		System.out.println("");
		System.out.println("");

		System.out.println("Species set 2:");
		for (AcVariable<?> var : speciesSet.getVariables()) {
			System.out.print(var.getVariableIndex() + " ");
		}
		System.out.println("");
		System.out.println("");
		System.out.println("Parameter set 2:");
		for (AcVariable<?> var : paramSet.getVariables()) {
			System.out.print(var.getVariableIndex() + " ");
		}
		System.out.println("");
		System.out.println("");

		System.out.println("Species set 3:");
		for (AcVariable<?> var : childSpeciesSet.getVariables()) {
			System.out.print(var.getVariableIndex() + " ");
		}
		System.out.println("");
		System.out.println("");
		System.out.println("Parameter set 3:");
		for (AcVariable<?> var : childParamSet.getVariables()) {
			System.out.print(var.getVariableIndex() + " ");
		}
		System.out.println("");
		System.out.println("");
	}
}