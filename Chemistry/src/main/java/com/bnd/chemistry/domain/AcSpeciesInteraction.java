package com.bnd.chemistry.domain;

import java.util.Comparator;

import com.bnd.chemistry.domain.AcVariable.AcVariableLabelComparator;
import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.function.domain.Function;
import com.bnd.function.domain.FunctionHolder;

public class AcSpeciesInteraction extends TechnicalDomainObject implements FunctionHolder<Double, Double> {

	public static class AcSpeciesInteractionVariableComparator implements Comparator<AcSpeciesInteraction> {

		private AcVariableLabelComparator<AcSpecies> speciesComparator = new AcVariableLabelComparator<AcSpecies>();

		@Override
		public int compare(AcSpeciesInteraction si1, AcSpeciesInteraction si2) {
			return speciesComparator.compare(si1.getSpecies(), si2.getSpecies());
		}
	}

	private AcSpecies species;
	private AcInteraction action;
	private Function<Double, Double> settingFunction;

	public AcSpecies getSpecies() {
		return species;
	}

	public void setSpecies(AcSpecies species) {
		this.species = species;
	}

	public Function<Double, Double> getSettingFunction() {
		return settingFunction;
	}

	public void setSettingFunction(Function<Double, Double> settingFunction) {
		this.settingFunction = settingFunction;
	}

	public AcInteraction getAction() {
		return action;
	}

	protected void setAction(AcInteraction action) {
		this.action = action;
	}

	public AcSpeciesSet getSpeciesSet() {
		return action.getSpeciesSet();
	}

	public Long getSpeciesId() {
		return species != null ? species.getId() : null;
	}

	@Override
	public Function<Double, Double> getFunction() {
		return settingFunction;
	}

	@Override
	public void setFunction(Function<Double, Double> function) {
		this.settingFunction = function;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(species.getLabel());
    	if (settingFunction != null) {
    		sb.append(" / ");
        	sb.append(settingFunction);
    	}
    	return sb.toString();
    }
}