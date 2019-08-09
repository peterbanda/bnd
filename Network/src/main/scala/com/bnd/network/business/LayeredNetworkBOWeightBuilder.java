package com.bnd.network.business;

import java.util.Iterator;
import java.util.List;

import com.bnd.network.BndNetworkException;
import com.bnd.network.domain.FixedNetworkWeightSetting;
import com.bnd.network.domain.FixedNetworkWeightSettingOrder;
import com.bnd.network.domain.LayeredNetworkWeightSetting;
import com.bnd.network.domain.NetworkWeightSelection;
import com.bnd.network.domain.NetworkWeightSetting;
import com.bnd.network.domain.TemplateNetworkWeightSetting;

public class LayeredNetworkBOWeightBuilder<T> implements NetworkBOWeightBuilder<LayeredNetworkBO<T>, T> {

	private UntypedNetworkBOWeightBuilder<T> networkWeightResolver;

	protected LayeredNetworkBOWeightBuilder() {
		// no-op
	}

	@Override
	public void setTemplateWeights(
		LayeredNetworkBO<T> layeredNetworkBO,
		TemplateNetworkWeightSetting<T> templateWeightSetting
	) {
		for (final NetworkBO<T> layerBO : layeredNetworkBO.getLayers()) {
			networkWeightResolver.setWeights(layerBO, templateWeightSetting);
		}		
	}

	@Override
	public void setLayeredWeights(
		LayeredNetworkBO<T> layeredNetworkBO,
		LayeredNetworkWeightSetting<T> layeredWeightSetting
	) {
		List<NetworkWeightSetting<T>> layerWeightSettings = layeredWeightSetting.getLayers();
		if (layerWeightSettings.size() != layeredNetworkBO.getLayersNum() - 1) {
			throw new BndNetworkException("The number of layers of a network '" + (layeredNetworkBO.getLayersNum() - 1) + "' is not equal the number of layers of a weight settings '" + layerWeightSettings.size() + "'.");
		}

		// skip the first layer (no incoming edges, hence no weights expected)
		Iterator<NetworkWeightSetting<T>> layerNetworkWeightSettingIterator = layerWeightSettings.iterator();
		boolean first = true;
		for (final NetworkBO<T> layerBO : layeredNetworkBO.getLayers()) {
			if (first)
				first = false;
			else
				networkWeightResolver.setWeights(layerBO, layerNetworkWeightSettingIterator.next());
		}
	}

	@Override
	public void setFixedWeights(
		LayeredNetworkBO<T> layeredNetworkBO,
		FixedNetworkWeightSetting<T> fixedWeightSetting
	) {
		final FixedNetworkWeightSettingOrder settingOrder = fixedWeightSetting.getSettingOrder();
		Iterator<T> weightIterator = fixedWeightSetting.getWeights().iterator();
		NetworkWeightSelection weightSelection = null;
		switch (settingOrder) {
			case SimpleOrder:
				weightSelection = NetworkWeightSelection.All;
				break;
			case ImmutableWithinLayerFirst:
				weightSelection = NetworkWeightSelection.ImmutableAndThenMutable;
				break;
			case ImmutableGlobalFirst:
				weightSelection = NetworkWeightSelection.Immutable;
				break;
		}
		for (final NetworkBO<T> layerBO : layeredNetworkBO.getLayers()) {
			networkWeightResolver.setIteratedWeights(layerBO, weightIterator, weightSelection);
		}
		if (settingOrder == FixedNetworkWeightSettingOrder.ImmutableGlobalFirst) {
			for (final NetworkBO<T> layerBO : layeredNetworkBO.getLayers()) {
				networkWeightResolver.setIteratedWeights(layerBO, weightIterator, NetworkWeightSelection.Mutable);
			}
		}
	}

	@Override
	public void setIteratedWeights(
		LayeredNetworkBO<T> layeredNetworkBO,
		Iterator<T> weightIterator,
		NetworkWeightSelection networkWeightSelection
	) {
		for (final NetworkBO<T> layerBO : layeredNetworkBO.getLayers()) {
			networkWeightResolver.setIteratedWeights(layerBO, weightIterator, networkWeightSelection);
		}
	}

	public void setNetworkWeightResolver(UntypedNetworkBOWeightBuilder<T> networkWeightResolver) {
		this.networkWeightResolver = networkWeightResolver;
	}
}