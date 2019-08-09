package com.bnd.math.domain.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class TrainingSet<T> {

	private List<TrainingPair<T>> trainingPairs = new ArrayList<TrainingPair<T>>();

	public TrainingSet() {
		// nothing to do
	}

	public List<TrainingPair<T>> getTrainingPairs() {
		return trainingPairs;
	}

	public void ListTrainingPairs(List<TrainingPair<T>> trainingPairs) {
		this.trainingPairs = trainingPairs;
	}

	public void addTrainingPair(TrainingPair<T> trainingPair) {
		trainingPairs.add(trainingPair);
	}

	public void removeTrainingPair(TrainingPair<T> trainingPair) {
		trainingPairs.remove(trainingPair);
	}

	public int getInputArity() {
		return !trainingPairs.isEmpty() ? trainingPairs.get(0).getInput().size() : 0;
	}

	public int getDesiredOutputArity() {
		return !trainingPairs.isEmpty() ? trainingPairs.get(0).getDesiredOutput().size() : 0;
	}
}