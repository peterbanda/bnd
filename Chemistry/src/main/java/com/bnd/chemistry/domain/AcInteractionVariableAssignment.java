package com.bnd.chemistry.domain;

import java.util.Comparator;

import com.bnd.chemistry.domain.AcVariable.AcVariableLabelComparator;
import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.function.domain.Function;
import com.bnd.function.domain.FunctionHolder;

public class AcInteractionVariableAssignment extends TechnicalDomainObject implements FunctionHolder<Double, Double> {

	public static class AcInteractionVariableComparator implements Comparator<AcInteractionVariableAssignment> {

		private AcVariableLabelComparator<AcInteractionVariable> variableComparator = new AcVariableLabelComparator<AcInteractionVariable>();

		@Override
		public int compare(AcInteractionVariableAssignment cw1, AcInteractionVariableAssignment cw2) {
			return variableComparator.compare(cw1.getVariable(), cw2.getVariable());
		}
	}

	private AcInteractionVariable variable;
	private AcInteraction action;
	private Function<Double, Double> settingFunction;

	public AcInteractionVariableAssignment() {
		super();
	}

	public AcInteractionVariableAssignment(AcInteractionVariable variable) {
		this();
		this.variable = variable;
	}

	public AcInteractionVariable getVariable() {
		return variable;
	}

	public void setVariable(AcInteractionVariable variable) {
		this.variable = variable;
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

	@Override
	public Function<Double, Double> getFunction() {
		return settingFunction;
	}

	@Override
	public void setFunction(Function<Double, Double> function) {
		this.settingFunction = function;
	}
}