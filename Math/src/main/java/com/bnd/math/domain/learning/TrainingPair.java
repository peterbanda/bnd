package com.bnd.math.domain.learning;

import java.util.List;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class TrainingPair<T> {

	private List<T> input;
	private List<T> desiredOutput;

	public TrainingPair() {
		// nothing to do
	}

	public List<T> getInput() {
		return input;
	}

	public void setInput(List<T> input) {
		this.input = input;
	}

	public List<T> getDesiredOutput() {
		return desiredOutput;
	}

	public void setDesiredOutput(List<T> desiredOutput) {
		this.desiredOutput = desiredOutput;
	}
}