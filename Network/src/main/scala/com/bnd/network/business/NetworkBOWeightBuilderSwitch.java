package com.bnd.network.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.bnd.network.domain.FixedNetworkWeightSetting;
import com.bnd.network.domain.LayeredNetworkWeightSetting;
import com.bnd.network.domain.NetworkWeightSelection;
import com.bnd.network.domain.NetworkWeightSetting;
import com.bnd.network.domain.TemplateNetworkWeightSetting;

public class NetworkBOWeightBuilderSwitch<T> implements UntypedNetworkBOWeightBuilder<T> {

	private Map<Class<? extends NetworkBO>, NetworkBOWeightBuilder<NetworkBO<T>, T>> networkBOWeightBuilderMap = new HashMap<Class<? extends NetworkBO>, NetworkBOWeightBuilder<NetworkBO<T>, T>>(); 

	protected NetworkBOWeightBuilderSwitch(Collection<NetworkBOWeightBuilder<NetworkBO<T>, T>> networkWeightBuilders) {
		for (NetworkBOWeightBuilder<NetworkBO<T>, T> networkWeightBuilder : networkWeightBuilders) {
			Class<? extends NetworkBO> networkBOClazz = null;
			if (networkWeightBuilder instanceof LayeredNetworkBOWeightBuilder) {
				((LayeredNetworkBOWeightBuilder) networkWeightBuilder).setNetworkWeightResolver(this);
				networkBOClazz = LayeredNetworkBO.class;
			} else {
				networkBOClazz = FlatNetworkBO.class;
			}
			networkBOWeightBuilderMap.put(networkBOClazz, networkWeightBuilder);
		}
	}

	@Override
	public void setWeights(NetworkBO<T> networkBO, NetworkWeightSetting<T> weightSetting) {
		final NetworkBOWeightBuilder<NetworkBO<T>, T> networkWeightBuilder = networkBOWeightBuilderMap.get(networkBO.getClass());
		if (networkWeightBuilder != null) {
			if (weightSetting.isTemplate()) {
				networkWeightBuilder.setTemplateWeights(networkBO, (TemplateNetworkWeightSetting<T>) weightSetting);
			} else if (weightSetting.hasLayers()) {
				networkWeightBuilder.setLayeredWeights(networkBO, (LayeredNetworkWeightSetting<T>) weightSetting);
			} else {
				networkWeightBuilder.setFixedWeights(networkBO, (FixedNetworkWeightSetting<T>) weightSetting);
			}
		}
	}

	@Override
	public void setIteratedWeights(
		NetworkBO<T> networkBO,
		Iterator<T> weightIterator,
		NetworkWeightSelection networkWeightSelection
	) {
		final NetworkBOWeightBuilder<NetworkBO<T>, T> networkWeightBuilder = networkBOWeightBuilderMap.get(networkBO.getClass());
		if (networkWeightBuilder != null) {
			networkWeightBuilder.setIteratedWeights(networkBO, weightIterator, networkWeightSelection);
		}
	}
}