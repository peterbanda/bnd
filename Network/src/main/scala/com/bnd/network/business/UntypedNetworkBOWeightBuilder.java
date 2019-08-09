package com.bnd.network.business;

import java.util.Iterator;

import com.bnd.network.domain.NetworkWeightSelection;
import com.bnd.network.domain.NetworkWeightSetting;

public interface UntypedNetworkBOWeightBuilder<T> {

	void setWeights(
		NetworkBO<T> networkBO,
		NetworkWeightSetting<T> weightSetting);

	void setIteratedWeights(
		NetworkBO<T> networkBO,
		Iterator<T> weightIterator,
		NetworkWeightSelection networkWeightSelection);
}