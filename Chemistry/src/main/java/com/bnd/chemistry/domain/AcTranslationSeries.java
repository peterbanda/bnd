package com.bnd.chemistry.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.bnd.core.domain.runnable.BaseManipulationSeries;

public class AcTranslationSeries extends BaseManipulationSeries implements AcVariableSetIF<AcTranslationVariable> {

	private AcSpeciesSet speciesSet;
	private Collection<AcTranslationVariable> variables = new HashSet<AcTranslationVariable>();
	private Integer varSequenceNum = 0;
	private boolean variablesReferenced;
	private Double defaultVariableValue = 0d;

	private Set<AcTranslation> translations = new HashSet<AcTranslation>();

	private List<AcTranslatedRun> translatedRuns = new LinkedList<AcTranslatedRun>();

	public AcTranslationSeries() {
		super();
	}

	public AcTranslationSeries(Long id) {
		super(id);
	}

	public AcSpeciesSet getSpeciesSet() {
		return speciesSet;
	}

	public void setSpeciesSet(AcSpeciesSet speciesSet) {
		this.speciesSet = speciesSet;
	}

	public Collection<AcSpecies> getSpecies() {
		return speciesSet.getOwnAndInheritedVariables();
	}

	public Set<AcTranslation> getTranslations() {
		return translations;
	}

	public void setTranslations(Set<AcTranslation> translations) {
		this.translations = translations;
	}

	public void addTranslation(AcTranslation translation) {
		translations.add(translation);
		translation.setTranslationSeries(this);
	}

	public void removeTranslation(AcTranslation translation) {
		translations.remove(translation);
		translation.setTranslationSeries(null);
	}

	public Integer getTranslationsCount() {
		return translations.size();
	}

	public List<AcTranslatedRun> getTranslatedRuns() {
		return translatedRuns;
	}

	protected void setTranslatedRuns(List<AcTranslatedRun> translatedRuns) {
		this.translatedRuns = translatedRuns;
	}

	public void addTranslatedRun(AcTranslatedRun translatedRun) {
		translatedRuns.add(0, translatedRun);
		translatedRun.setTranslationSeries(this);
	}

	public void removeTranslatedRun(AcTranslatedRun translatedRun) {
		translatedRuns.remove(translatedRun);
		translatedRun.setTranslationSeries(null);
	}

	public Set<String> getItemLabels() {
		Set<String> itemLabels = new HashSet<String>();
		for (AcTranslation translation : translations) {
			itemLabels.addAll(translation.getItemLabels());
		}
		return itemLabels;
	}

	public boolean isVariablesReferenced() {
		return variablesReferenced;
	}

	public void setVariablesReferenced(boolean variablesReferenced) {
		this.variablesReferenced = variablesReferenced;
	}

	/**
	 * Overridden for Hibernate mapping only.
	 */
	@Override
	public Integer getRepeatFromElement() {
		return super.getRepeatFromElement();
	}

	/**
	 * Overridden for Hibernate mapping only.
	 */
	@Override
	public void setRepeatFromElement(Integer repeatFromElement) {
		super.setRepeatFromElement(repeatFromElement);
	}

	public Integer getVarSequenceNum() {
		return varSequenceNum;
	}

	public void setVarSequenceNum(Integer sequenceNum) {
		this.varSequenceNum = sequenceNum;
	}

	@Override
	public Integer getNextVarSequenceNum() {
		return varSequenceNum++;
	}

	@Override
	public Collection<AcTranslationVariable> getVariables() {
		return variables;
	}

	@Override
	public void setVariables(Collection<AcTranslationVariable> variable) {
		this.variables = variable;
	}

	@Override
	public void addVariable(AcTranslationVariable variable) {
		if (variable.getVariableIndex() == null) {
			variable.setVariableIndex(getNextVarSequenceNum());
		}
		variables.add(variable);
		variable.setParentSetUnsafe(this);
	}

	@Override
	public void addVariables(Collection<AcTranslationVariable> variables) {
		for (AcTranslationVariable variable : variables) {
			addVariable(variable);
		}
	}

	@Override
	public void removeVariable(AcTranslationVariable variable) {
		variables.remove(variable);
		variable.setParentSet(null);
	}

	@Override
	public void initVariables() {
		this.variables = new HashSet<AcTranslationVariable>();
	}

	@Override
	public Collection<AcTranslationVariable> getOwnAndInheritedVariables() {
		// Inheritance not supported, hence return just variables
		return variables;
	}

	public Double getDefaultVariableValue() {
		return defaultVariableValue;
	}

	public void setDefaultVariableValue(Double defaultVariableValue) {
		this.defaultVariableValue = defaultVariableValue;
	}
}