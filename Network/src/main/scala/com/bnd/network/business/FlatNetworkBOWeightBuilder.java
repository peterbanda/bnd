package com.bnd.network.business;

import java.util.Iterator;
import java.util.List;

import com.bnd.network.BndNetworkException;
import com.bnd.network.domain.*;
import com.bnd.math.business.rand.RandomDistributionProvider;
import com.bnd.math.business.rand.RandomDistributionProviderFactory;
import com.bnd.math.domain.rand.RandomDistribution;

public class FlatNetworkBOWeightBuilder<T> implements NetworkBOWeightBuilder<FlatNetworkBO<T>, T> {

	@Override
	public void setTemplateWeights(
		FlatNetworkBO<T> flatNetworkBO,
		TemplateNetworkWeightSetting<T> templateWeightSetting
	) {
		// TODO: Number expected, fix that !!
		RandomDistributionProvider<Number> randomDistributionProvider = RandomDistributionProviderFactory.apply(
                (RandomDistribution<Number>) templateWeightSetting.getRandomDistribution());

		for (final NodeBO<T> nodeBO : flatNetworkBO.getNodes()) {
			List<T> randomWeights = (List<T>) randomDistributionProvider.nextList(nodeBO.getInEdgesNum());
			nodeBO.setInNodesWeights(randomWeights);
		}
	}

	@Override
	public void setLayeredWeights(
		FlatNetworkBO<T> flatNetworkBO,
		LayeredNetworkWeightSetting<T> layeredWeightSetting
	) {
		throw new BndNetworkException("Layered network weight setting not expected for a flat network.");
	}

	@Override
	public void setFixedWeights(
		FlatNetworkBO<T> flatNetworkBO,
		FixedNetworkWeightSetting<T> fixedWeightSetting
	) {
		final FixedNetworkWeightSettingOrder settingOrder = fixedWeightSetting.getSettingOrder();
		final Iterator<T> weightIterator = fixedWeightSetting.getWeights().iterator();
		switch (settingOrder) {
			case SimpleOrder:
				setIteratedWeights(flatNetworkBO, weightIterator, NetworkWeightSelection.All);
				break;
			case ImmutableWithinLayerFirst:
			case ImmutableGlobalFirst:
				setIteratedWeights(flatNetworkBO, weightIterator, NetworkWeightSelection.ImmutableAndThenMutable);
				break;	
		}
	}

	@Override
	public void setIteratedWeights(
		FlatNetworkBO<T> flatNetworkBO,
		Iterator<T> weightIterator,
		NetworkWeightSelection networkWeightSelection
	) {
		if (networkWeightSelection == NetworkWeightSelection.ImmutableAndThenMutable) {
			setIteratedWeights(flatNetworkBO, weightIterator, NetworkWeightSelection.Immutable);
			setIteratedWeights(flatNetworkBO, weightIterator, NetworkWeightSelection.Mutable);
			return;
		}
		for (final InWeightAccessible<T> nodeBO : flatNetworkBO.getNodes()) {
			switch (networkWeightSelection) {
				case All:
					nodeBO.setWeights(weightIterator);
					break;
				case Immutable:
					nodeBO.setImmutableWeights(weightIterator);
					break;
				case Mutable:
					nodeBO.setMutableWeights(weightIterator);
					break;
				default:
					throw new BndNetworkException("'All', 'Immutable', or 'Mutable' network weight selection expected.");
			}
		}
	}
}