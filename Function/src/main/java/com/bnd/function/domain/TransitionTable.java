package com.bnd.function.domain;

import java.util.ArrayList;
import java.util.List;

import com.bnd.core.util.ParseUtil;
import com.bnd.function.BndFunctionException;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class TransitionTable<IN, OUT> extends AbstractFunction<IN, OUT> {

	private static final Class<Boolean> DEFAULT_INPUT_CLASS = Boolean.class;
	private static final Class<Boolean> DEFAULT_OUTPUT_CLASS = Boolean.class;

	private List<OUT> outputs = new ArrayList<OUT>();
	private IN rangeFrom;
	private IN rangeTo;

	public TransitionTable() {
		super();
		setInputClazz((Class<IN>) DEFAULT_INPUT_CLASS);
		setOutputClazz((Class<OUT>) DEFAULT_OUTPUT_CLASS);
	}

	public List<OUT> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<OUT> outputs) {
		this.outputs = outputs;
	}

	public void addOutput(OUT output) {
		outputs.add(output);
	}

	public IN getRangeFrom() {
		return rangeFrom;
	}

	public void setRangeFrom(IN rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	public IN getRangeTo() {
		return rangeTo;
	}

	public void setRangeTo(IN rangeTo) {
		this.rangeTo = rangeTo;
	}

	// for hibernate mapping
	public List<String> getStringOutputs() {
		List<String> stringOutputs = new ArrayList<String>();
		for (OUT output : getOutputs()) {
			stringOutputs.add(output.toString());
		}
		return stringOutputs;
	}

	// for hibernate mapping
	public void setStringOutputs(List<String> stringOutputs) {
		List<OUT> outputs = new ArrayList<OUT>();
		for (String stringOutput : stringOutputs) {
			if (getOutputClazz() == null) {
				throw new BndFunctionException("Output class not specified for the transition table " + getId());
			}
			outputs.add(ParseUtil.parse(stringOutput, getOutputClazz()));
		}
		setOutputs(outputs);
	}
}