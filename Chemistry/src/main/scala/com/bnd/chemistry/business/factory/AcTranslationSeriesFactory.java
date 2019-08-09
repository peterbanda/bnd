package com.bnd.chemistry.business.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.chemistry.business.ArtificialChemistryUtil;
import com.bnd.chemistry.domain.AcConcentrationLevel;
import com.bnd.chemistry.domain.AcInteraction;
import com.bnd.chemistry.domain.AcInteractionSeries;
import com.bnd.chemistry.domain.AcSpecies;
import com.bnd.chemistry.domain.AcSpeciesSet;
import com.bnd.chemistry.domain.AcSpeciesType;
import com.bnd.chemistry.domain.AcTranslation;
import com.bnd.chemistry.domain.AcTranslationItem;
import com.bnd.chemistry.domain.AcTranslationSeries;
import com.bnd.chemistry.domain.AcTranslationVariable;
import com.bnd.core.util.ObjectUtil;
import com.bnd.function.business.FunctionFactory;
import com.bnd.function.domain.AggregateFunction;

public class AcTranslationSeriesFactory {

	public enum DefaultTransItemLabel {
		Input("X"), Output("Y"), Feedback("D"), Internal("T");

		private final String label;

		DefaultTransItemLabel(String label) {
			this.label = label;
		}

		public static DefaultTransItemLabel fromLabel(String label) {
			for (DefaultTransItemLabel transItemLabel : DefaultTransItemLabel.values()) {
				if (label.startsWith(transItemLabel.label)) {
					return transItemLabel;
				}
			}
			return null;
		}

		public String toString() {
			return label;
		}
	}

	private static AcTranslationSeriesFactory instance = new AcTranslationSeriesFactory();

	private final FunctionFactory functionFactory = new FunctionFactory();
	private final ArtificialChemistryUtil acUtil = ArtificialChemistryUtil.getInstance();

	private AcTranslationSeriesFactory() {
		// Nothing to do
	}

	public static AcTranslationSeriesFactory getInstance() {
		return instance;
	}

	public AcTranslationSeries createSimplePeriodicFullTS(
		AggregateFunction inputSpeciesStateTransFunction,
		AggregateFunction outputSpeciesStateTransFunction,
		AggregateFunction feedbackSpeciesStateTransFunction,
		AcConcentrationLevel inputConcentrationLevel,
		AcConcentrationLevel outputConcentrationLevel,
		AcConcentrationLevel feedbackConcentrationLevel,
		AggregateFunction internalSpeciesStateTransFunction,
		AcInteractionSeries quasiPeriodicActionSeries
	) {
		AcTranslationSeries translationSeries = createSimplePeriodicInputOutputFeedbackTS(
			inputSpeciesStateTransFunction,
			outputSpeciesStateTransFunction,
			feedbackSpeciesStateTransFunction,
			inputConcentrationLevel,
			outputConcentrationLevel,
			feedbackConcentrationLevel,
			quasiPeriodicActionSeries);

		AcTranslation rangeTranslation = ObjectUtil.getFirst(translationSeries.getTranslations());
		createAndAddTranslationVariablesWithItems(
			rangeTranslation,
			translationSeries.getSpeciesSet().getInternalSpecies(),
			internalSpeciesStateTransFunction,
			DefaultTransItemLabel.Internal.toString());
		return translationSeries;
	}

	public AcTranslationSeries createSimplePeriodicInputOutputFeedbackTS(
		AggregateFunction inputSpeciesStateTransFunction,
		AggregateFunction outputSpeciesStateTransFunction,
		AggregateFunction feedbackSpeciesStateTransFunction,
		AcConcentrationLevel inputConcentrationLevel,
		AcConcentrationLevel outputConcentrationLevel,
		AcConcentrationLevel feedbackConcentrationLevel,
		AcInteractionSeries quasiPeriodicActionSeries
	) {
		AcSpeciesSet speciesSet = quasiPeriodicActionSeries.getSpeciesSet();
		if (speciesSet == null) {
			throw new BndChemistryException("Species set of interaction series must be set.");
		}
		AcTranslationSeries translationSeries = new AcTranslationSeries();
		AcTranslation rangeTranslation = new AcTranslation();
		translationSeries.addTranslation(rangeTranslation);

		// create translation variables and translation items
		createAndAddTranslationVariableWithItems(
			rangeTranslation, speciesSet.getSpecies(AcSpeciesType.Input), inputSpeciesStateTransFunction, inputConcentrationLevel,
			DefaultTransItemLabel.Input.toString());
		createAndAddTranslationVariableWithItems(
			rangeTranslation, speciesSet.getSpecies(AcSpeciesType.Output), outputSpeciesStateTransFunction, outputConcentrationLevel,
			DefaultTransItemLabel.Output.toString());
		if (speciesSet.hasSpecies(AcSpeciesType.Feedback) && feedbackConcentrationLevel != null) {
			createAndAddTranslationVariableWithItems(
				rangeTranslation, speciesSet.getSpecies(AcSpeciesType.Feedback), feedbackSpeciesStateTransFunction, feedbackConcentrationLevel,
				DefaultTransItemLabel.Feedback.toString());
		}

		List<AcInteraction> sortedActions = new ArrayList<AcInteraction>();
		sortedActions.addAll(quasiPeriodicActionSeries.getActions());
		Collections.sort(sortedActions);
		AcInteraction initAction = sortedActions.remove(0);
		if (initAction.getStartTime() != 0) {
			throw new BndChemistryException("First action of interaction series must start at the time step 0.");
		}
		Integer startingTime = sortedActions.remove(0).getStartTime();
		Integer actionInterval = getActionInterval(sortedActions, startingTime);

		translationSeries.setPeriodicity(actionInterval.intValue());
		translationSeries.setRepeatFromElement(0);
		translationSeries.setSpeciesSet(speciesSet);
		rangeTranslation.setFromTime(startingTime);
		rangeTranslation.setToTime(startingTime + actionInterval - 1);
		return translationSeries;
	}
	
	private void createAndAddTranslationVariableWithItems(
		AcTranslation rangeTranslation,
		Collection<AcSpecies> species,
		AggregateFunction speciesStateTransFunction,
		AcConcentrationLevel concentrationLevel,
		String label
	) {
		AcTranslationSeries translationSeries = rangeTranslation.getTranslationSeries();
		AcTranslationVariable translationVariable = createAndAddTranslationVariable(label, translationSeries);
		translationSeries.addVariable(translationVariable);
		final Set<Integer> speciesIndeces = getSpeciesIndecesAndValidate(species, speciesStateTransFunction, concentrationLevel);
		AcTranslationItem translationItem = createTranslationItem(
				speciesIndeces, speciesStateTransFunction, concentrationLevel);
		translationItem.setVariable(translationVariable);
		rangeTranslation.addTranslationItem(translationItem);
	}

	private void createAndAddTranslationVariablesWithItems(
		AcTranslation rangeTranslation,
		Collection<AcSpecies> species,
		AggregateFunction speciesStateTransFunction,
		String labelPrefix
	) {
		AcTranslationSeries translationSeries = rangeTranslation.getTranslationSeries();
		Collection<AcTranslationVariable> translationVariables = new ArrayList<AcTranslationVariable>();
		final int speciesNum = species.size();
		for (int transIndex = 1; transIndex <= speciesNum; transIndex++) {
			translationVariables.add(createAndAddTranslationVariable(labelPrefix + transIndex, translationSeries));
		}
		// add translation items
		final Iterator<Integer> speciesIndexIterator = acUtil.getVariableIndeces(species).iterator();
		for (AcTranslationVariable translationVariable : translationVariables) {
			AcTranslationItem translationItem = new AcTranslationItem();
			translationItem.setTranslationFunction(functionFactory.createAggregateFunctionFormula(
					speciesStateTransFunction,
					speciesIndexIterator.next()));
			translationItem.setVariable(translationVariable);
			rangeTranslation.addTranslationItem(translationItem);			
		}
	}

	private AcTranslationItem createTranslationItem(
		Set<Integer> speciesIndeces,
		AggregateFunction speciesStateTransFunction,
		AcConcentrationLevel concentrationLevel
	) {
		AcTranslationItem translationItem = new AcTranslationItem();
		if (concentrationLevel.isSingleValue()) {
			translationItem.setTranslationFunction(functionFactory.createAggregateGreaterThanFunctionFormula(
					speciesStateTransFunction,
					ObjectUtil.getFirst(speciesIndeces),
					ObjectUtil.getSecond(speciesIndeces)));
		} else {
			translationItem.setTranslationFunction(functionFactory.createAggregateGreaterThanFunctionFormula(
					speciesStateTransFunction,
					ObjectUtil.getFirst(speciesIndeces),
					concentrationLevel.getMiddleValue()));			
		}
		return translationItem;
	}

	private Set<Integer> getSpeciesIndecesAndValidate(
		Collection<AcSpecies> species,
		AggregateFunction speciesStateTransFunction,
		AcConcentrationLevel concentrationLevel
	) {
		final int speciesNum = species.size();
		if (concentrationLevel.isSingleValue()) {
			if (speciesNum != 2) {
				throw new BndChemistryException("Two species expected, but got '" + speciesNum + "'.");
			}
		} else if (speciesNum != 1) {
			throw new BndChemistryException("One species expected, but got '" + speciesNum + "'.");
		}
		return acUtil.getVariableIndeces(species);
	}

	private Integer getActionInterval(Collection<AcInteraction> sortedActions, int startingTime) {
		int previousActionTime = startingTime;
		Integer actionInterval = null;
		for (AcInteraction action : sortedActions) {
			final int currentActionInterval = action.getStartTime() - previousActionTime;
			if (actionInterval != null) {
				if (!ObjectUtil.areObjectsEqual(currentActionInterval, actionInterval)) {
					throw new BndChemistryException("Uniform periodic interaction series expected, but found two intervals: '" + actionInterval + "' and '" + currentActionInterval + "'.");
				}
			} else {
				actionInterval = currentActionInterval;
			}
			previousActionTime = action.getStartTime();
		}
		return actionInterval;
	}

	private AcTranslationVariable createAndAddTranslationVariable(
		String label,
		AcTranslationSeries translationSeries
	) {
		AcTranslationVariable translationVariable = new AcTranslationVariable();
		translationVariable.setLabel(label);
		translationSeries.addVariable(translationVariable);
		return translationVariable;
	}
}