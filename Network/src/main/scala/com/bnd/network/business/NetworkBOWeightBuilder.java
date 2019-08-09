package com.bnd.network.business;

import java.util.Iterator;

import com.bnd.network.domain.FixedNetworkWeightSetting;
import com.bnd.network.domain.LayeredNetworkWeightSetting;
import com.bnd.network.domain.NetworkWeightSelection;
import com.bnd.network.domain.TemplateNetworkWeightSetting;

public interface NetworkBOWeightBuilder<N extends NetworkBO<T>, T> {

	void setTemplateWeights(
		N networkBO,
		TemplateNetworkWeightSetting<T> templateWeightSetting);

	void setLayeredWeights(
		N networkBO,
		LayeredNetworkWeightSetting<T> layeredWeightSetting);

	void setFixedWeights(
		N networkBO,
		FixedNetworkWeightSetting<T> fixedWeightSetting);

	void setIteratedWeights(
		N networkBO,
		Iterator<T> weightIterator,
		NetworkWeightSelection networkWeightSelection);
}