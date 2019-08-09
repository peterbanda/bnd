package com.bnd.chemistry.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bnd.function.BndFunctionException;
import com.bnd.function.business.JepExpressionEvaluator;
import org.apache.commons.lang.StringUtils;

import com.bnd.chemistry.domain.AcInteractionVariableAssignment;
import com.bnd.chemistry.domain.AcCollectiveSpeciesReactionAssociationType;
import com.bnd.chemistry.domain.AcCompartment;
import com.bnd.chemistry.domain.AcDNAStrandSpeciesSet;
import com.bnd.chemistry.domain.AcEvaluation;
import com.bnd.chemistry.domain.AcInteraction;
import com.bnd.chemistry.domain.AcInteractionSeries;
import com.bnd.chemistry.domain.AcInteractionVariable;
import com.bnd.chemistry.domain.AcParameter;
import com.bnd.chemistry.domain.AcParameterSet;
import com.bnd.chemistry.domain.AcReaction;
import com.bnd.chemistry.domain.AcReactionGroup;
import com.bnd.chemistry.domain.AcReactionSet;
import com.bnd.chemistry.domain.AcSpecies;
import com.bnd.chemistry.domain.AcSpeciesAssociationType;
import com.bnd.chemistry.domain.AcSpeciesInteraction;
import com.bnd.chemistry.domain.AcSpeciesReactionAssociation;
import com.bnd.chemistry.domain.AcSpeciesReactionAssociation.AcSpeciesReactionAssociationIndexComparator;
import com.bnd.chemistry.domain.AcSpeciesSet;
import com.bnd.chemistry.domain.AcTranslation;
import com.bnd.chemistry.domain.AcTranslationItem;
import com.bnd.chemistry.domain.AcTranslationSeries;
import com.bnd.chemistry.domain.AcTranslationVariable;
import com.bnd.chemistry.domain.AcVariable;
import com.bnd.chemistry.domain.AcVariable.AcVariableIndexComparator;
import com.bnd.chemistry.domain.AcVariable.AcVariableLabelComparator;
import com.bnd.chemistry.domain.AcVariableSetIF;
import com.bnd.core.BndRuntimeException;
import com.bnd.core.Pair;
import com.bnd.core.PairComparator;
import com.bnd.core.util.ObjectUtil;
import com.bnd.function.business.FunctionUtility;
import com.bnd.function.domain.Expression;
import com.bnd.function.domain.Function;

public class ArtificialChemistryUtil {

	private final AcReplicator acReplicator = AcReplicator.getInstance();
	private final FunctionUtility functionUtility = new FunctionUtility();
	private static final ArtificialChemistryUtil instance = new ArtificialChemistryUtil();
	public static final PairComparator<AcCompartment, AcSpecies> compartmentSpeciesComparator = new PairComparator<AcCompartment, AcSpecies>(
			new AcCompartmentDepthComparator(), new AcVariableLabelComparator<AcSpecies>());

	private ArtificialChemistryUtil() {
		// no-op
	}

	public static ArtificialChemistryUtil getInstance() {
		return instance;
	}

	public String replaceIndexPlaceholdersWithVariableLabels(
		Function<?, ?> function,
		AcVariableSetIF<?> variableSet1,
		AcVariableSetIF<?> variableSet2
	) {
		Collection<AcVariable<?>> acVariables = new ArrayList<AcVariable<?>>();
		if (variableSet1 != null) {
			acVariables.addAll(variableSet1.getOwnAndInheritedVariables());
		}
		if (variableSet2 != null) {
			acVariables.addAll(variableSet2.getOwnAndInheritedVariables());
		}
		return replaceIndexPlaceholdersWithVariableLabels(function, acVariables);
	}

	public String replaceIndexPlaceholdersWithVariableLabels(
		Function<?, ?> function,
		AcVariableSetIF<?> variableSet
	) {
		return replaceIndexPlaceholdersWithVariableLabels(function, variableSet.getOwnAndInheritedVariables());
	}

	public String replaceIndexPlaceholdersWithVariableLabels(
		Function<?, ?> function,
		Collection<? extends AcVariable<?>> acVariables
	) {
		Map<Integer, String> variableIndexLabelMap = new HashMap<Integer, String>();
		for (AcVariable<?> acVariable : acVariables) {
			variableIndexLabelMap.put(acVariable.getVariableIndex(), acVariable.getLabel());			
		}
		return functionUtility.getFormulaIndexPlaceholdersReplacedWithVariableNames(function, variableIndexLabelMap);
	}

	public String replaceVariableLabelsWithIndexPlaceholders(
		String formula,
		AcVariableSetIF<?> variableSet
	) {
		return replaceVariableLabelsWithIndexPlaceholders(formula,	getVariableIndeces(variableSet), variableSet.getOwnAndInheritedVariables());
	}

	public String replaceSpeciesAndParamaterLabelsWithIndexPlaceholders(
		String formula,
		AcSpeciesSet speciesSet
	) {
		AcParameterSet parameterSet = speciesSet.getParameterSet();
		Set<Integer> referencedVariableIndeces = new HashSet<Integer>();
		referencedVariableIndeces.addAll(getVariableIndeces(speciesSet));
		referencedVariableIndeces.addAll(getVariableIndeces(parameterSet));

		Collection<AcVariable<?>> acMagnitudes = new ArrayList<AcVariable<?>>();
		acMagnitudes.addAll(speciesSet.getOwnAndInheritedVariables());
		acMagnitudes.addAll(parameterSet.getOwnAndInheritedVariables());
		return replaceVariableLabelsWithIndexPlaceholders(formula, referencedVariableIndeces, acMagnitudes);
	}

	public String replaceVariableLabelsWithIndexPlaceholders(
		String formula,
		Set<Integer> referencedVariableIndeces,
		Collection<? extends AcVariable<?>> acVariables
	) {
		Map<Integer, String> variableIndexLabelMap = new HashMap<Integer, String>();
		for (AcVariable<?> acMagnitude : acVariables) {
			variableIndexLabelMap.put(acMagnitude.getVariableIndex(), acMagnitude.getLabel());
		}
		return functionUtility.getFormulaVariableNamesReplacedWithIndexPlaceholders(formula, referencedVariableIndeces, variableIndexLabelMap);
	}

	public <M extends AcVariable<?>> Collection<String> getItemsWithStoichiometry(AcVariableSetIF<M> magnitudeSet, Double[] stoichiometricVector) {
		Collection<String> itemsWithStoichiometry = new ArrayList<String>();
		Map<Integer, M> indexItemMap = getIndexVariableMap(magnitudeSet);

		for (int index = 0; index < stoichiometricVector.length; index++) {
			Double itemStoichiometry = stoichiometricVector[index];
			if (itemStoichiometry != null && itemStoichiometry > 0) {
				itemsWithStoichiometry.add(itemStoichiometry + " " + indexItemMap.get(index).getLabel());
			}
		}
		return itemsWithStoichiometry;
	}

	public <M extends AcVariable<?>> Collection<String> getSpeciesLabelsWithStoichiometry(Collection<AcSpeciesReactionAssociation> speciesAssociations) {
		Collection<String> itemsWithStoichiometry = new ArrayList<String>();
		for (AcSpeciesReactionAssociation speciesAssoc : speciesAssociations) {
			StringBuilder sb = new StringBuilder();
			if (!ObjectUtil.areObjectsEqual(speciesAssoc.getStoichiometricFactor(), new Double(1))) {
				sb.append(speciesAssoc.getStoichiometricFactor());
				sb.append(" ");
			}
			sb.append(speciesAssoc.getSpeciesLabel());
			itemsWithStoichiometry.add(sb.toString());
		}
		return itemsWithStoichiometry;
	}

	public <M extends AcVariable<?>> Collection<String> getSpeciesLabels(Collection<AcSpeciesReactionAssociation> speciesAssociations) {
		Collection<String> speciesLabels = new ArrayList<String>();
		for (AcSpeciesReactionAssociation speciesAssoc : speciesAssociations) {
			speciesLabels.add(speciesAssoc.getSpeciesLabel());
		}
		return speciesLabels;
	}

	public Set<AcSpeciesReactionAssociation> getSpeciesAssociations(AcSpeciesSet speciesSet, Double[] stoichiometricVector) {
		Set<AcSpeciesReactionAssociation> speciesAssociations = new HashSet<AcSpeciesReactionAssociation>();
		Map<Integer, AcSpecies> indexItemMap = getIndexVariableMap(speciesSet);

		for (int index = 0; index < stoichiometricVector.length; index++) {
			Double stoichiometricFactor = stoichiometricVector[index];
			if (stoichiometricFactor != null && stoichiometricFactor > 0) {
				speciesAssociations.add(new AcSpeciesReactionAssociation(indexItemMap.get(index), stoichiometricFactor));
			}
		}
		return speciesAssociations;
	}

	// TO / FROM STRING CONVERSIONS

	public String getFunctionAsString(Function<?, ?> function, AcReactionSet reactionSet) {
		return replaceIndexPlaceholdersWithVariableLabels(function, reactionSet.getSpeciesSet(), reactionSet.getParameterSet());
	}

	public String getRateFunctionAsString(AcReaction reaction, boolean forwardFlag) {
		return getFunctionAsString(reaction.getRateFunction(forwardFlag), reaction.getReactionSet());
	}

	public String getEvolFunctionAsString(AcParameter parameter) {
		return replaceIndexPlaceholdersWithVariableLabels(parameter.getEvolFunction(), parameter.getParentSet().getSpeciesSet(), parameter.getParentSet());
	}

	public String getEvaluationFunctionAsString(AcEvaluation evaluation) {
		return replaceIndexPlaceholdersWithVariableLabels(evaluation.getEvalFunction(), evaluation.getTranslationSeries());
	}

	private void validateExpression(String formula, Collection<String> recognizedVariables) throws BndFunctionException {
		Expression<Double, Double> expression = new Expression<Double, Double>(formula);
		JepExpressionEvaluator<Double, Double> jetExpressionEvaluator = new JepExpressionEvaluator<Double, Double>(expression, recognizedVariables.toArray(new String[0]));
		jetExpressionEvaluator.validate();
	}

	public void setRateFunctionFromString(String formula, AcReaction reaction, boolean forwardFlag) {
		final Map<Integer, String> variableIndexLabelMap = createZippedVariableIndexLabelMap(reaction.getSpeciesSet(), reaction.getSpeciesSet().getParameterSet());
		validateExpression(formula, variableIndexLabelMap.values());

		String convertedFormula = replaceSpeciesAndParamaterLabelsWithIndexPlaceholders(formula, reaction.getSpeciesSet());
		Expression<Double, Double> newRateFunction = functionUtility.getExpressionFunctionFromString(convertedFormula);
		Expression<Double, Double> rateFunction = (Expression<Double, Double>) reaction.getRateFunction(forwardFlag);
		if (newRateFunction == null || rateFunction == null) {
			reaction.setRateFunction(newRateFunction, forwardFlag);
		} else {
			rateFunction.setFormula(convertedFormula);				
		}
	}

	public String getSettingFunctionAsString(AcSpeciesInteraction speciesAction) {
		Map<Integer, String> variableIndexLabelMap = createZippedVariableIndexLabelMap(speciesAction.getSpeciesSet(), speciesAction.getAction().getActionSeries());
		return functionUtility.getFormulaIndexPlaceholdersReplacedWithVariableNames(speciesAction.getSettingFunction(), variableIndexLabelMap);
	}

	public void setSettingFunctionFromString(
		String formula,
		AcSpeciesInteraction speciesAction
	) {
		final Map<Integer, String> variableIndexLabelMap = createZippedVariableIndexLabelMap(speciesAction.getSpeciesSet(), speciesAction.getAction().getActionSeries());
		validateExpression(formula, variableIndexLabelMap.values());
		String convertedFormula = functionUtility.getFormulaVariableNamesReplacedWithIndexPlaceholders(formula, variableIndexLabelMap.keySet(), variableIndexLabelMap);
		functionUtility.setExpressionFunctionFromString(convertedFormula, speciesAction);
	}

	public String getSettingFunctionAsString(AcInteractionVariableAssignment variableAssignment) {
		Map<Integer, String> variableIndexLabelMap = createZippedVariableIndexLabelMap(variableAssignment.getSpeciesSet(), variableAssignment.getAction().getActionSeries());
		return functionUtility.getFormulaIndexPlaceholdersReplacedWithVariableNames(variableAssignment.getSettingFunction(), variableIndexLabelMap);
	}

	public void setSettingFunctionFromString(
		String formula,
		AcInteractionVariableAssignment variableAssignment
	) {
		final Map<Integer, String> variableIndexLabelMap = createZippedVariableIndexLabelMap(variableAssignment.getSpeciesSet(), variableAssignment.getAction().getActionSeries());
		validateExpression(formula, variableIndexLabelMap.values());
		String convertedFormula = functionUtility.getFormulaVariableNamesReplacedWithIndexPlaceholders(formula, variableIndexLabelMap.keySet(), variableIndexLabelMap);
		functionUtility.setExpressionFunctionFromString(convertedFormula, variableAssignment);
	}

	public String getTranslationFunctionAsString(AcTranslationItem translationItem) {
		final Map<Integer, String> variableIndexLabelMap = createVariableIndexLabelMap(translationItem);
		return functionUtility.getFormulaIndexPlaceholdersReplacedWithVariableNames(translationItem.getTranslationFunction(), variableIndexLabelMap);
	}

	public Collection<AcSpecies> getTranslationFunctionReferencedSpecies(AcTranslationItem translationItem) {
		final AcTranslationSeries translationSeries = translationItem.getTranslationSeries();
		final Map<Integer, AcSpecies> speciesIndexMap = createVariableIndexMap(translationSeries.getSpeciesSet(), 2, 0);
		return getReferencedVariables(translationItem.getTranslationFunction(), speciesIndexMap);
	}

	public Collection<AcTranslationVariable> getTranslationFunctionReferencedVariables(AcTranslationItem translationItem) {
		final Map<Integer, AcTranslationVariable> variableIndexMap = createVariableIndexMap(translationItem.getTranslationSeries(), 2, 1);
		return getReferencedVariables(translationItem.getTranslationFunction(), variableIndexMap);
	}

	private <T> Collection<T> getReferencedVariables(Function<?, ?> function, Map<Integer, T> variableIndexMap) {
		Collection<T> variables = new ArrayList<T>();
		for (Integer variableIndex : function.getReferencedVariableIndeces()) {
			final T variable = variableIndexMap.get(variableIndex);
			if (variable != null) variables.add(variable);
		}
		return variables;
	}

	public void setTranslationFunctionFromString(
		String formula,
		AcTranslationItem translationItem
	) {
		final Map<Integer, String> variableIndexLabelMap = createVariableIndexLabelMap(translationItem);
		validateExpression(formula, variableIndexLabelMap.values());
		String convertedFormula = functionUtility.getFormulaVariableNamesReplacedWithIndexPlaceholders(formula, variableIndexLabelMap.keySet(), variableIndexLabelMap);
		functionUtility.setExpressionFunctionFromString(convertedFormula, translationItem);
	}

	private Map<Integer, String> createVariableIndexLabelMap(AcTranslationItem translationItem) {
		final AcTranslationSeries translationSeries = translationItem.getTranslationSeries();
		if (translationSeries.isVariablesReferenced()) {
			return createZippedVariableIndexLabelMap(translationSeries.getSpeciesSet(), translationSeries);
		}
		return createVariableIndexLabelMap(translationSeries.getSpeciesSet(), 2);
	}

	private Map<Integer, String> createVariableIndexLabelMap(AcVariableSetIF<?> variableSet) {
		return createVariableIndexLabelMap(variableSet, 1);
	}

	private Map<Integer, String> createVariableIndexLabelMap(AcVariableSetIF<?> variableSet, int multConst) {
		Map<Integer, String> variableIndexLabelMap = new HashMap<Integer, String>();
		for (AcVariable<?> item : variableSet.getOwnAndInheritedVariables()) {
			variableIndexLabelMap.put(multConst * item.getVariableIndex(), item.getLabel());
		}
		return variableIndexLabelMap;
	}

	private <M extends AcVariable<?>> Map<Integer, M> createVariableIndexMap(AcVariableSetIF<M> variableSet) {
		return createVariableIndexMap(variableSet, 1, 0);
	}

	private <M extends AcVariable<?>> Map<Integer, M> createVariableIndexMap(
		AcVariableSetIF<M> variableSet, int multConst, int addConst
	) {
		Map<Integer, M> variableIndexLabelMap = new HashMap<Integer, M>();
		for (M item : variableSet.getOwnAndInheritedVariables()) {
			variableIndexLabelMap.put(multConst * item.getVariableIndex() + addConst, item);
		}
		return variableIndexLabelMap;
	}

	private Map<Integer, String> createZippedVariableIndexLabelMap(
		AcVariableSetIF<? extends AcVariable<?>> variableSet1,
		AcVariableSetIF<? extends AcVariable<?>> variableSet2
	) {
		Map<Integer, String> variableIndexLabelMap = new HashMap<Integer, String>();

		for (AcVariable<?> variable : variableSet1.getOwnAndInheritedVariables()) {
			variableIndexLabelMap.put(2 * variable.getVariableIndex(), variable.getLabel());
		}

		for (AcVariable<?> variable : variableSet2.getOwnAndInheritedVariables()) {
			variableIndexLabelMap.put(2 * variable.getVariableIndex() + 1, variable.getLabel());
		}
		return variableIndexLabelMap;
	}

	public void setEvolFunctionFromString(String formula, AcParameter parameter) {
		// TODO: optimize this
		Collection<String> labels = new ArrayList<String>();
		labels.addAll(getVariableLabels(parameter.getParentSet().getSpeciesSet()));
		labels.addAll(getVariableLabels(parameter.getParentSet()));
		validateExpression(formula, labels);

		String convertedFormula = replaceSpeciesAndParamaterLabelsWithIndexPlaceholders(formula, parameter.getParentSet().getSpeciesSet());
		functionUtility.setExpressionFunctionFromString(convertedFormula, parameter);
	}

	public void setEvaluationFunctionFromString(String formula, AcEvaluation evaluation) {
		// TODO: optimize this
		validateExpression(formula, getVariableLabels(evaluation.getTranslationSeries()));
		String convertedFormula = replaceVariableLabelsWithIndexPlaceholders(formula, evaluation.getTranslationSeries());
		functionUtility.setExpressionFunctionFromString(convertedFormula, evaluation);
	}

	public Collection<String> getReactantsAsString(AcReaction reaction) {
		return getSpeciesLabelsWithStoichiometry(
				reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant));
	}

	public Collection<String> getProductsAsString(AcReaction reaction) {
		return getSpeciesLabelsWithStoichiometry(
				reaction.getSpeciesAssociations(AcSpeciesAssociationType.Product));
	}

	public String getDNAStrandSpeciesSetAsString(AcDNAStrandSpeciesSet speciesSet, String separator) {
		StringBuilder sb = new StringBuilder();

		sb.append("Upper Strands:\n");
		sb.append(getSpeciesAsString(speciesSet.getUpperStrands(), separator));
		sb.append("\n");

		sb.append("Lower Strands:\n");
		sb.append(getSpeciesAsString(speciesSet.getLowerStrands(), separator));
		sb.append("\n");

		sb.append("Full Double Strands:\n");
		sb.append(getSpeciesAsString(speciesSet.getFullDoubleStrands(), separator));
		sb.append("\n");

		sb.append("Partial Double Strands:\n");
		sb.append(getSpeciesAsString(speciesSet.getPartialDoubleStrands(), separator));
		sb.append("\n");

		return sb.toString();
	}

	public String getSpeciesAsString(Collection<? extends AcSpecies> species, String separator) {
		return StringUtils.join(getVariableLabels(species), separator);
	}

	public void setSpeciesAssociationsFromString(
		String speciesAssociationsString,
		AcSpeciesAssociationType assocType,
		AcReaction reaction
	) {
		List<AcSpeciesReactionAssociation> speciesAssociations = getSpeciesAssociations(speciesAssociationsString, reaction.getSpeciesSet());
		reaction.setSpeciesAssociations(speciesAssociations, assocType);
	}

	public void updateSpeciesAssociations(
		String speciesAssociationsString,
		AcSpeciesAssociationType assocType,
		AcReaction reaction
	) {
		List<AcSpeciesReactionAssociation> newSpeciesAssociations = getSpeciesAssociations(speciesAssociationsString, reaction.getSpeciesSet());
		updateSpeciesAssociations(newSpeciesAssociations, assocType, reaction);
	}

	// it is assumed that all new species associations are of the given type
	public void updateSpeciesAssociations(
		Collection<AcSpeciesReactionAssociation> newSpeciesAssociations,
		AcSpeciesAssociationType assocType,
		AcReaction reaction
	) {
		int order = 0;
		for (AcSpeciesReactionAssociation newSpeciesAssoc : newSpeciesAssociations) {
			newSpeciesAssoc.setOrder(order);
			newSpeciesAssoc.setType(assocType);
			order++;
		}
		Collection<AcSpeciesReactionAssociation> oldSpeciesAssociations = reaction.getSpeciesAssociations(assocType);
		Map<AcSpecies, AcSpeciesReactionAssociation> newSpeciesAssociationsMap = createSpeciesAssociationsMap(newSpeciesAssociations);
		for (AcSpeciesReactionAssociation oldSpeciesAssoc : oldSpeciesAssociations) {
			AcSpeciesReactionAssociation matchedNewSpeciesAssoc = newSpeciesAssociationsMap.remove(oldSpeciesAssoc.getSpecies());
			if (matchedNewSpeciesAssoc == null) {
				// remove
				reaction.removeSpeciesAssociation(oldSpeciesAssoc);
			} else {
				// update
				oldSpeciesAssoc.setOrder(matchedNewSpeciesAssoc.getOrder());
				if (assocType == AcSpeciesAssociationType.Reactant || assocType == AcSpeciesAssociationType.Product) {
					oldSpeciesAssoc.setStoichiometricFactor(matchedNewSpeciesAssoc.getStoichiometricFactor());
				} else {
					// for catalyst or inhibitor stoichiometric factor is not used, hence no update
				}
			}
		}
		// add remaining new associations
		for (AcSpeciesReactionAssociation newAssoc : newSpeciesAssociations)
			if (newSpeciesAssociationsMap.containsKey(newAssoc.getSpecies()))
				reaction.addSpeciesAssociation(newAssoc);
	}

	private List<AcSpeciesReactionAssociation> getSpeciesAssociations(
		String stoichiometricVectorWithSpeciesLabels,
		AcSpeciesSet speciesSet
	) throws BndChemistryValidationException {
		List<AcSpeciesReactionAssociation> speciesAssociations = new ArrayList<AcSpeciesReactionAssociation>();
		Map<String, AcSpecies> speciesLabelIndexMap = getLabelVariableMap(speciesSet);
		String[] speciesLabelsWithStoichiometry = stoichiometricVectorWithSpeciesLabels.split(",|\\+");
		for (String speciesLabelWithStoichiometry : speciesLabelsWithStoichiometry) {
			speciesLabelWithStoichiometry = speciesLabelWithStoichiometry.trim();
			if (!speciesLabelWithStoichiometry.isEmpty()) {
                String stoichiometry = "1";
                String speciesLabel = "";
                if (Character.isDigit(speciesLabelWithStoichiometry.charAt(0))) {
                    final Pattern p = Pattern.compile("\\d+\\.\\d+|\\d+"); // .\d+|\d+
                    final Matcher m = p.matcher(speciesLabelWithStoichiometry);
                    m.find();
                    stoichiometry = m.group();
                    speciesLabel = speciesLabelWithStoichiometry.substring(stoichiometry.length()).trim();
                } else {
                    speciesLabel = speciesLabelWithStoichiometry;
                }

				final AcSpecies species = speciesLabelIndexMap.get(speciesLabel);
				if (species != null) {
					try {
						speciesAssociations.add(new AcSpeciesReactionAssociation(species, Double.parseDouble(stoichiometry)));
					} catch (NumberFormatException e) {
						throw new BndChemistryValidationException("Stoichiometry '" + stoichiometry + "' is not a number.");
					}
				} else throw new BndChemistryValidationException("Species label '" + speciesLabel + "' undefined.");
			}
		}
		return speciesAssociations;
	}

	private Map<AcSpecies, AcSpeciesReactionAssociation> createSpeciesAssociationsMap(Collection<AcSpeciesReactionAssociation> speciesAssociations) {
		Map<AcSpecies, AcSpeciesReactionAssociation> speciesAssociationsMap = new HashMap<AcSpecies, AcSpeciesReactionAssociation>();
		for (AcSpeciesReactionAssociation speciesAssoc : speciesAssociations) {
			speciesAssociationsMap.put(speciesAssoc.getSpecies(), speciesAssoc);
		}
		return speciesAssociationsMap;
	}

	private String getLatexFormattedSpeciesLabel(AcSpecies species) {
		StringBuilder sb = new StringBuilder();
		String label = species.getLabel();
		int underscoreIndex = StringUtils.indexOfAny(label, "_");
		if (underscoreIndex < 0) {
			int numberIndex = StringUtils.indexOfAny(label, "0123456789");
			if (numberIndex < 0) {
				return label;
			}
			sb.append(StringUtils.substring(label, 0, numberIndex));
			sb.append("_{");
			String rest = StringUtils.substring(label, numberIndex);
			int letterIndex = StringUtils.indexOfAnyBut(rest, "0123456789");
			if (letterIndex < 0) {
				sb.append(rest);
				sb.append("}");				
			} else {
				sb.append(StringUtils.substring(label, numberIndex, numberIndex + letterIndex));
				sb.append("}");
				sb.append(StringUtils.substring(rest, letterIndex));
			}
		} else {
			sb.append(StringUtils.substring(label, 0, underscoreIndex + 1));
			sb.append("{");
			sb.append(StringUtils.substring(label, underscoreIndex + 1));
			sb.append("}");
		}
		return sb.toString();
	}

	private String getSpeciesAssocFormattedLatex(AcReaction reaction, AcSpeciesAssociationType assocType, String delimeter) {
		Collection<String> latexLabels = new ArrayList<String>();
		for (AcSpeciesReactionAssociation assoc : reaction.getSpeciesAssociations(assocType)) {
			latexLabels.add(getLatexFormattedSpeciesLabel(assoc.getSpecies()));
		}
		return StringUtils.join(latexLabels, delimeter);
	}

	public String getLatexTable(AcReactionSet reactionSet, boolean includeRates) {
		boolean includeReverseRateConstants = false; 
		for (AcReaction reaction : reactionSet.getReactions()) {
			if (reaction.hasReverseRateConstants()) {
				includeReverseRateConstants = true;
				break;
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\\begin{tabular}{l");
		if (includeRates) {
			sb.append("l");
			if (includeReverseRateConstants)
				sb.append("l");
		}
		sb.append("}\n");
		sb.append("Reaction     ");

		if (includeRates) {
			sb.append("& Forward Rates");
			if (includeReverseRateConstants)
				sb.append(" & Reverse Rates");
		}
		sb.append("\\\\\n");
		sb.append("\\hline\n");
		for (AcReaction reaction : reactionSet.getReactions()) {
//			AcKineticsBO kineticsBO = AcKineticsBO.createInstance(reaction, true);
//			Function<Double, Double> rateFunction = kineticsBO.createRateFunction();
//			String rateFunctionString = getFunctionAsString(rateFunction, reaction.getReactionSet());
			String catalystsString = getSpeciesAssocFormattedLatex(reaction, AcSpeciesAssociationType.Catalyst, ", ");
			String inhibitorsString = getSpeciesAssocFormattedLatex(reaction, AcSpeciesAssociationType.Inhibitor, ", ");
			
			sb.append("$");
			sb.append(getSpeciesAssocFormattedLatex(reaction, AcSpeciesAssociationType.Reactant, " + "));
			
			String arrowSymbol = null;
			boolean catOrInh = (catalystsString != null && !catalystsString.isEmpty()) || (inhibitorsString != null && !inhibitorsString.isEmpty());
			if (reaction.hasReverseRateConstants()) {
				if (catOrInh) {
					arrowSymbol = "\\xleftrightarrow";
				} else {
					arrowSymbol = "\\leftrightarrow";
				}
			} else {
				if (catOrInh) {
					arrowSymbol = "\\xrightarrow";
				} else {
					arrowSymbol = "\\rightarrow";
				}				
			}

			sb.append(" ");
			sb.append(arrowSymbol);
			if (catalystsString != null && !catalystsString.isEmpty()) {
				sb.append("{");
				sb.append(catalystsString);
				sb.append("}");
			} else if (inhibitorsString != null && !inhibitorsString.isEmpty()) {
				sb.append(arrowSymbol);
				sb.append("[");
				sb.append(inhibitorsString);
				sb.append("]");
			}
			sb.append(" ");
			sb.append(getSpeciesAssocFormattedLatex(reaction, AcSpeciesAssociationType.Product, " + "));
			sb.append("$");
//			sb.append("$ & ");
//			if (!catalystsString.isEmpty()) {
//				sb.append("$");
//				sb.append(catalystsString);
//				sb.append("$ ");
//			}
//			sb.append("& ");
//			if (!inhibitorsString.isEmpty()) {
//				sb.append("$");
//				sb.append(inhibitorsString);
//				sb.append("$ ");
//			}
			if (includeRates) {
				sb.append(" & $");
				sb.append(StringUtils.join(reaction.getForwardRateConstants(), ","));
				sb.append("$");
				if (includeReverseRateConstants) {
					sb.append(" & $");
					sb.append(StringUtils.join(reaction.getReverseRateConstants(), ","));
					sb.append("$");
				}
			}
			sb.append("\\\\\n");
		}
		sb.append("\\end{tabular}");
		return sb.toString();
	}

// AC VARIABLE (SET) FUNCTIONS

	public <M extends AcVariable<?>> Map<String, M> getLabelVariableMap(AcVariableSetIF<M> variableSet) {
		Map<String, M> labelItemMap = new HashMap<String, M>();
		for (M item : variableSet.getOwnAndInheritedVariables()) {
			labelItemMap.put(item.getLabel(), item);
		}
		return labelItemMap;
	}

	public <M extends AcVariable<?>> Map<Integer, M> getIndexVariableMap(AcVariableSetIF<M> variableSet) {
		Map<Integer, M> indexItemMap = new HashMap<Integer, M>();
		for (M item : variableSet.getOwnAndInheritedVariables()) {
			indexItemMap.put(item.getVariableIndex(), item);
		}
		return indexItemMap;
	}

	public Map<Integer, String> getOrderedVariableLabelMap(AcVariableSetIF<?> variableSet) {
		List<AcVariable<?>> sortedItems = new ArrayList<AcVariable<?>>();
		sortedItems.addAll(variableSet.getOwnAndInheritedVariables());
		Collections.sort(sortedItems, new AcVariableIndexComparator());
		Map<Integer, String> indexItemLabelMap = new HashMap<Integer, String>();
		int order = 0;
		for (AcVariable<?> item : sortedItems) {
			indexItemLabelMap.put(order, item.getLabel());
			order++;
		}
		return indexItemLabelMap;
	}

	public Set<Integer> getVariableIndeces(AcVariableSetIF<? extends AcVariable<?>> variableSet) {
		return getVariableIndeces(variableSet.getOwnAndInheritedVariables());
	}

	public Set<Integer> getVariableIndeces(Collection<? extends AcVariable<?>> variables) {
		Set<Integer> itemIndeces = new HashSet<Integer>();
		for (AcVariable<?> item : variables) {
			itemIndeces.add(item.getVariableIndex());
		}
		return itemIndeces;
	}

	public Set<String> getVariableLabels(AcVariableSetIF<? extends AcVariable<?>> variableSet) {
		return getVariableLabels(variableSet.getOwnAndInheritedVariables());
	}

	public Set<String> getVariableLabels(Collection<? extends AcVariable<?>> variables) {
		Set<String> itemLabels = new HashSet<String>();
		for (AcVariable<?> item : variables) {
			itemLabels.add(item.getLabel());
		}
		return itemLabels;
	}

	public String getVariableLabelsAsString(Collection<? extends AcVariable<?>> variables) {
		return getVariableLabelsAsString(variables, ",");
	}

	public String getVariableLabelsAsString(Collection<? extends AcVariable<?>> variables, String separator) {
		List<String> labels = new ArrayList<String>();
		labels.addAll(getVariableLabels(variables));
		Collections.sort(labels);
		return StringUtils.join(labels, separator);
	}

	public String createFoldExpression(
		Collection<AcSpeciesReactionAssociation> speciesAssociations,
		AcCollectiveSpeciesReactionAssociationType  collectiveSpeciesReactionAssociationType,
		boolean wrapIfNeeded
	) {
		return createFoldOrderedExpression(speciesAssociations, collectiveSpeciesReactionAssociationType.getOperator(), wrapIfNeeded);
	}

	public String createFoldOrderedExpression(
		Collection<AcSpeciesReactionAssociation> speciesAssociations,
		String operation,
		boolean wrapIfNeeded
	) {
		Collection<String> parts = new ArrayList<String>();
		List<AcSpeciesReactionAssociation> orderedSpeciesAssociations = new ArrayList<AcSpeciesReactionAssociation>();
		orderedSpeciesAssociations.addAll(speciesAssociations);
		Collections.sort(orderedSpeciesAssociations, new AcSpeciesReactionAssociationIndexComparator());
		for (AcSpeciesReactionAssociation speciesAssoc : orderedSpeciesAssociations) {
			parts.add(getVariablePlaceholder(speciesAssoc));
		}
		String speciesFoldExpression = StringUtils.join(parts, operation);
		if (wrapIfNeeded) {
			speciesFoldExpression = wrapIfNeeded(speciesFoldExpression, orderedSpeciesAssociations);
		}
		return speciesFoldExpression;
	}

	public String wrapIfNeeded(String expression, Collection<AcSpeciesReactionAssociation> speciesAssociations) {
		if (speciesAssociations.size() > 1) {
			// wrapping is needed
			return "(" + expression + ")";
		}
		return expression;
	}

	public Set<Integer> getReferencedSpeciesIndeces(AcTranslationSeries translationSeries) {
		Set<Integer> speciesIndeces = new HashSet<Integer>();
		for (AcTranslation rangeTranslation : translationSeries.getTranslations()) {
			speciesIndeces.addAll(rangeTranslation.getReferencedSpeciesIndeces());
		}
		return speciesIndeces;		
	}

	public Set<AcSpecies> getReferencedSpecies(AcTranslation rangeTranslation) {
		Map<Integer, AcSpecies> indexSpeciesMap = getIndexVariableMap(rangeTranslation.getSpeciesSet());
		Set<AcSpecies> referencedSpecies = new HashSet<AcSpecies>();
		for (Integer speciesIndex : rangeTranslation.getReferencedSpeciesIndeces()) {
			referencedSpecies.add(indexSpeciesMap.get(speciesIndex));
		}
		return referencedSpecies;
	}

	public Set<AcSpecies> getReferencedSpecies(AcTranslationSeries translationSeries) {
		Map<Integer, AcSpecies> indexSpeciesMap = getIndexVariableMap(translationSeries.getSpeciesSet());
		Set<AcSpecies> referencedSpecies = new HashSet<AcSpecies>();
		for (Integer speciesIndex : getReferencedSpeciesIndeces(translationSeries)) {
			referencedSpecies.add(indexSpeciesMap.get(speciesIndex));
		}
		return referencedSpecies;
	}

	public Set<AcSpecies> getReferencedSpecies(AcReactionSet reactionSet) {
		Set<AcSpecies> refSpecies = new HashSet<AcSpecies>();
		for (AcReaction reaction : reactionSet.getReactions()) {
			for (AcSpeciesReactionAssociation assoc : reaction.getSpeciesAssociations()) {
				refSpecies.add(assoc.getSpecies());
			}
		}
		return refSpecies;
	}

	public Map<String, AcReaction> getLabelReactionMap(AcReactionSet reactionSet) {
		Map<String, AcReaction> labelReactionMap = new HashMap<String, AcReaction>();
		for (AcReaction item : reactionSet.getReactions()) {
			labelReactionMap.put(item.getLabel(), item);
		}
		return labelReactionMap;
	}

	public String getVariablePlaceholder(AcSpeciesReactionAssociation speciesAssoc) {
		return functionUtility.getVariablePlaceHolder(speciesAssoc.getSpeciesIndex());
	}

	public void addReactions(
		AcReactionSet reactionSet,
		AcReactionSet additionalReactionSet
	) throws BndChemistryValidationException {
		final Map<String, AcSpecies> speciesLabelMap = getLabelVariableMap(reactionSet.getSpeciesSet());

		for (AcReactionGroup reactionGroup : additionalReactionSet.getGroups()) {
			final AcReactionGroup newReactionGroup = acReplicator.cloneReactionGroup(reactionGroup);
			newReactionGroup.initReactions();
			ObjectUtil.nullIdAndVersion(newReactionGroup);
			newReactionGroup.setLabel("X" + newReactionGroup.getLabel());
			reactionSet.addGroup(newReactionGroup);
			// assuming each reaction is part of max one group
			for (AcReaction reaction : reactionGroup.getReactions()) {
				final AcReaction newReaction = cloneReaction(reaction, speciesLabelMap);
				newReactionGroup.addReaction(newReaction);
				reactionSet.addReaction(newReaction);
			}
		}

		// reactions without group
		for (AcReaction reaction : additionalReactionSet.getReactions()) {
			if (!reaction.hasGroup()) {
				reactionSet.addReaction(cloneReaction(reaction, speciesLabelMap));
			}
		}
	}

	private AcReaction cloneReaction(
		AcReaction reaction,
		Map<String, AcSpecies> speciesLabelMap
	) {
		final AcReaction newReaction = acReplicator.cloneReaction(reaction);
		acReplicator.nullIdAndVersion(newReaction);
		newReaction.setLabel("X" + newReaction.getLabel());
		for (AcSpeciesReactionAssociation speciesAssoc : newReaction.getSpeciesAssociations()) {
			final String label = speciesAssoc.getSpecies().getLabel();
			AcSpecies species = speciesLabelMap.get(label);
			if (species == null) {
				throw new BndRuntimeException("Species '" + label + "' not found.");
			}
			speciesAssoc.setSpecies(species);
		}
		return newReaction;
	}

	public Collection<AcSpecies> getNewSpecies(
		AcReactionSet reactionSet,
		AcReactionSet additionalReactionSet
	) throws BndChemistryValidationException {
		final Map<String, AcSpecies> newSpeciesLabelMap = new HashMap<String, AcSpecies>();
		final Map<String, AcSpecies> speciesLabelMap = getLabelVariableMap(reactionSet.getSpeciesSet());

		for (AcReaction reaction : additionalReactionSet.getReactions()) {
			for (AcSpeciesReactionAssociation speciesAssoc : reaction.getSpeciesAssociations()) {
				final String label = speciesAssoc.getSpecies().getLabel();
				if (!speciesLabelMap.containsKey(label) && !newSpeciesLabelMap.containsKey(label)) {
					final AcSpecies newSpecies = new AcSpecies();
					newSpecies.setLabel(label);
					newSpeciesLabelMap.put(label, newSpecies);
				}
			}
		}
		return newSpeciesLabelMap.values();
	}

	public void replaceSpeciesSet(
		AcReactionSet reactionSet,
		AcSpeciesSet newSpeciesSet
	) throws BndChemistryValidationException {
		final Map<String, AcSpecies> newSpeciesLabelMap = getLabelVariableMap(newSpeciesSet);

		for (AcReaction reaction : reactionSet.getReactions()) {
			for (AcSpeciesReactionAssociation speciesAssoc : reaction.getSpeciesAssociations()) {
				final AcSpecies newSpecies = newSpeciesLabelMap.get(speciesAssoc.getSpecies().getLabel());
				if (newSpecies == null)
					throw new BndChemistryValidationException("New species set does not contain species '" + speciesAssoc.getSpecies().getLabel() + "'.");
				speciesAssoc.setSpecies(newSpecies);
			}
		}
		reactionSet.setSpeciesSet(newSpeciesSet);
	}

	public void replaceSpeciesSet(
		AcInteractionSeries interactionSeries,
		AcSpeciesSet newSpeciesSet
	) throws BndChemistryValidationException {
		final AcSpeciesSet oldSpeciesSet = interactionSeries.getSpeciesSet();
		final Map<String, AcSpecies> newSpeciesLabelMap = getLabelVariableMap(newSpeciesSet);

		for (AcInteraction interaction : interactionSeries.getActions()) {
			for (AcSpeciesInteraction speciesInteraction : interaction.getSpeciesActions()) {
				final AcSpecies newSpecies = newSpeciesLabelMap.get(speciesInteraction.getSpecies().getLabel());
				if (newSpecies == null) 
					throw new BndChemistryValidationException("New species set does not contain species '" + speciesInteraction.getSpecies().getLabel() + "'.");
				speciesInteraction.setSpecies(newSpecies);

				interactionSeries.setSpeciesSet(oldSpeciesSet);
				final String settingFunctionFormula = getSettingFunctionAsString(speciesInteraction);
				interactionSeries.setSpeciesSet(newSpeciesSet);
				setSettingFunctionFromString(settingFunctionFormula, speciesInteraction);
			}

			for (AcInteractionVariableAssignment variableAssignment : interaction.getVariableAssignments()) {
				interactionSeries.setSpeciesSet(oldSpeciesSet);
				final String settingFunctionFormula = getSettingFunctionAsString(variableAssignment);
				interactionSeries.setSpeciesSet(newSpeciesSet);
				setSettingFunctionFromString(settingFunctionFormula, variableAssignment);
			}
		}
		Collection<AcSpecies> newImSpecies = new ArrayList<AcSpecies>();
		for (AcSpecies imSpecies : interactionSeries.getImmutableSpecies()) {
			final AcSpecies newSpecies = newSpeciesLabelMap.get(imSpecies.getLabel());
			if (newSpecies == null) 
				throw new BndChemistryValidationException("New species set does not contain species '" + imSpecies.getLabel() + "'.");
			newImSpecies.add(imSpecies);
		}
		interactionSeries.setImmutableSpecies(newImSpecies);
		interactionSeries.setSpeciesSet(newSpeciesSet);
	}

	public void addInteractions(
		AcInteractionSeries targetInteractionSeries,
		AcInteractionSeries sourceInteractionSeries
	) throws BndChemistryValidationException {
		final Map<String, AcSpecies> allSpeciesLabelMap = getLabelVariableMap(targetInteractionSeries.getSpeciesSet());
		final Map<String, AcInteractionVariable> allVariableLabelMap = getLabelVariableMap(targetInteractionSeries);

		final Map<Integer, AcInteraction> startTimeInteractionMap = new HashMap<Integer, AcInteraction>();
		for (AcInteraction interaction : targetInteractionSeries.getActions())
			startTimeInteractionMap.put(interaction.getStartTime(), interaction);

		for (AcInteraction sourceInteraction : sourceInteractionSeries.getActions()) {
			AcInteraction interaction = startTimeInteractionMap.get(sourceInteraction.getStartTime());
			if (interaction == null) {
				interaction = new AcInteraction();
				interaction.setStartTime(sourceInteraction.getStartTime());
				interaction.setTimeLength(sourceInteraction.getTimeLength());
				interaction.setAlternationType(sourceInteraction.getAlternationType());
				targetInteractionSeries.addAction(interaction);
			}

			// collect already referenced species and variables
			Map<String, AcSpeciesInteraction> refLabelSpeciesActionMap = new HashMap<String, AcSpeciesInteraction>();
			Map<String, AcInteractionVariableAssignment> refLabelVariableAssignmentMap = new HashMap<String, AcInteractionVariableAssignment>();
			for (AcSpeciesInteraction speciesInteraction : interaction.getSpeciesActions())
				refLabelSpeciesActionMap.put(speciesInteraction.getSpecies().getLabel(), speciesInteraction);

			for (AcInteractionVariableAssignment assignment : interaction.getVariableAssignments())
				refLabelVariableAssignmentMap.put(assignment.getVariable().getLabel(), assignment);

			// add new cache writes
			for (AcInteractionVariableAssignment sourceAssignment : sourceInteraction.getVariableAssignments()) {
				final String variableLabel = sourceAssignment.getVariable().getLabel();
				AcInteractionVariable variable = allVariableLabelMap.get(variableLabel);

				// proceed only if the cache write for a given variable label is new
				if (!refLabelVariableAssignmentMap.containsKey(variableLabel)) {
					if (variable == null) {
						// assume all variables have been added before, i.e. this part should never happen
						variable = new AcInteractionVariable();
						variable.setLabel(variableLabel);
						targetInteractionSeries.addVariable(variable);
						allVariableLabelMap.put(variableLabel, variable);
					}
					final AcInteractionVariableAssignment newVariableAssignment = addVariableAssignment(interaction, variable, sourceAssignment);
					refLabelVariableAssignmentMap.put(variableLabel, newVariableAssignment);
				}
			}

			// add new species actions
			for (AcSpeciesInteraction sourceSpeciesInteraction : sourceInteraction.getSpeciesActions()) {
				final String speciesLabel = sourceSpeciesInteraction.getSpecies().getLabel();
				AcSpecies species = allSpeciesLabelMap.get(speciesLabel);
				if (species != null) {
					// proceed only if species action for given label is new
					if (!refLabelSpeciesActionMap.containsKey(speciesLabel)) {
						final AcSpeciesInteraction newSpeciesInteraction = addSpeciesInteraction(interaction, species, sourceSpeciesInteraction);
						refLabelSpeciesActionMap.put(speciesLabel, newSpeciesInteraction);
					}
				}
			}
		}
	}

	// override is assumed
	public Pair<Collection<AcSpeciesInteraction>, Collection<AcInteractionVariableAssignment>> getSpeciesAndInteractionVariablesToRemove(
		AcInteractionSeries interactionSeries,
		AcInteractionSeries sourceInteractionSeries
	) {
		final Collection<AcSpeciesInteraction> speciesInteractionsToRemove = new ArrayList<AcSpeciesInteraction>();
		final Collection<AcInteractionVariableAssignment> assignmentsToRemove = new ArrayList<AcInteractionVariableAssignment>();
		final Map<Integer, AcInteraction> startTimeInteractionMap = new HashMap<Integer, AcInteraction>();
		for (AcInteraction interaction : interactionSeries.getActions())
			startTimeInteractionMap.put(interaction.getStartTime(), interaction);

		for (AcInteraction sourceInteraction : sourceInteractionSeries.getActions()) {
			AcInteraction interaction = startTimeInteractionMap.get(sourceInteraction.getStartTime());
			if (interaction != null) {
				// collect already referenced species and variables
				Map<String, AcSpeciesInteraction> refLabelSpeciesActionMap = new HashMap<String, AcSpeciesInteraction>();
				Map<String, AcInteractionVariableAssignment> refLabelVariableAssignmentMap = new HashMap<String, AcInteractionVariableAssignment>();
				for (AcSpeciesInteraction speciesInteraction : interaction.getSpeciesActions())
					refLabelSpeciesActionMap.put(speciesInteraction.getSpecies().getLabel(), speciesInteraction);

				for (AcInteractionVariableAssignment assignment : interaction.getVariableAssignments())
					refLabelVariableAssignmentMap.put(assignment.getVariable().getLabel(), assignment);

				// handle cache writes
				for (AcInteractionVariableAssignment sourceAssignment : sourceInteraction.getVariableAssignments()) {
					final String variableLabel = sourceAssignment.getVariable().getLabel();
					final AcInteractionVariableAssignment assignment = refLabelVariableAssignmentMap.get(variableLabel);
					// proceed only if cache write for given variable label is new or override is on
					if (assignment != null) {
						assignmentsToRemove.add(assignment);
					}
				}

				// handle species actions
				for (AcSpeciesInteraction sourceSpeciesInteraction : sourceInteraction.getSpeciesActions()) {
					final String speciesLabel = sourceSpeciesInteraction.getSpecies().getLabel();
					final AcSpeciesInteraction speciesInteraction = refLabelSpeciesActionMap.get(speciesLabel);

					if (speciesInteraction != null) {								
						speciesInteractionsToRemove.add(speciesInteraction);
					}
				}
			}
		}

		return new Pair<Collection<AcSpeciesInteraction>, Collection<AcInteractionVariableAssignment>>(speciesInteractionsToRemove, assignmentsToRemove);
	}

	private AcInteractionVariableAssignment addVariableAssignment(
		AcInteraction interaction,
		AcInteractionVariable variable,
		AcInteractionVariableAssignment sourceAssignment
	) {
		AcInteractionVariableAssignment newAssignment = new AcInteractionVariableAssignment();
		newAssignment.setVariable(variable);
		interaction.addVariableAssignment(newAssignment);

		final String settingFunctionFormula = getSettingFunctionAsString(sourceAssignment);
		setSettingFunctionFromString(settingFunctionFormula, newAssignment);
		return newAssignment;
	}

	private AcSpeciesInteraction addSpeciesInteraction(
		AcInteraction interaction,
		AcSpecies species,
		AcSpeciesInteraction sourceSpeciesInteraction
	) {
		AcSpeciesInteraction newSpeciesInteraction = new AcSpeciesInteraction();
		newSpeciesInteraction.setSpecies(species);
		interaction.addToSpeciesActions(newSpeciesInteraction);

		final String settingFunctionFormula = getSettingFunctionAsString(sourceSpeciesInteraction);
		setSettingFunctionFromString(settingFunctionFormula, newSpeciesInteraction);
		return newSpeciesInteraction;
	}

	public Collection<AcInteractionVariable> getNewInteractionVariables(
		AcInteractionSeries interactionSeries,
		AcInteractionSeries sourceInteractionSeries
	) {
		Collection<AcInteractionVariable> newVariables = new ArrayList<AcInteractionVariable>();
		final Set<String> variableLabels = getVariableLabels(interactionSeries);

		for (AcInteractionVariable variable : sourceInteractionSeries.getVariables()) {
			final String variableLabel = variable.getLabel();
			if (!variableLabels.contains(variableLabel)) {
				AcInteractionVariable newVariable = new AcInteractionVariable();
				newVariable.setLabel(variableLabel);
				newVariables.add(newVariable);
			}
		}
		return newVariables;
	}

	public Collection<AcSpecies> getSpecies(Collection<AcSpeciesReactionAssociation> assocs) {
		Collection<AcSpecies> species = new ArrayList<AcSpecies>();
		for (AcSpeciesReactionAssociation assoc : assocs) {
			species.add(assoc.getSpecies());
		}
		return species;
	}

	public Set<AcSpecies> filterOutSpecies(
		Collection<AcReaction> reactions,
		Collection<AcSpecies> species,
		AcSpeciesAssociationType type
	) {
		Set<AcSpecies> filteredSpecies = new HashSet<AcSpecies>();
		for (AcReaction reaction : reactions) {
			filteredSpecies.addAll(getSpecies(reaction.getSpeciesAssociations(type)));
		}
		Set<AcSpecies> filteredOutSpecies = new HashSet<AcSpecies>(species);
		filteredOutSpecies.removeAll(filteredSpecies);
		return filteredOutSpecies;
	}
}