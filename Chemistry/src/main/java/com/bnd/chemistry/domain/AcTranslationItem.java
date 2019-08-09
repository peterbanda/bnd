package com.bnd.chemistry.domain;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.function.domain.Function;
import com.bnd.function.domain.FunctionHolder;

public class AcTranslationItem<T> extends TechnicalDomainObject implements FunctionHolder<T, Double> {

	private AcTranslationVariable variable;
	private AcTranslation translation;
	private Function<T, Double> translationFunction;

	public AcTranslationVariable getVariable() {
		return variable;
	}

	public void setVariable(AcTranslationVariable variable) {
		this.variable = variable;
	}

	public String getLabel() {
		return variable != null ? variable.getLabel() : null;
	}

	public Function<T, Double> getTranslationFunction() {
		return translationFunction;
	}

	public void setTranslationFunction(Function<T, Double> settingFunction) {
		this.translationFunction = settingFunction;
	}

	public AcTranslation getTranslation() {
		return translation;
	}

	protected void setTranslation(AcTranslation translation) {
		this.translation = translation;
	}

	@Override
	public Function<T, Double> getFunction() {
		return translationFunction;
	}

	@Override
	public void setFunction(Function<T, Double> function) {
		this.translationFunction = function;
	}

	public AcTranslationSeries getTranslationSeries() {
		return translation != null ? translation.getTranslationSeries() : null;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getLabel());
		sb.append(" / ");
		sb.append(translationFunction);
		return sb.toString();
	}
}