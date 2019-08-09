package com.bnd.network.business

import java.util.List
import com.bnd.network.BndNetworkException
import com.bnd.network.domain.FixedNetworkWeightSetting
import com.bnd.network.domain.FixedNetworkWeightSettingOrder
import com.bnd.network.domain.LayeredNetworkWeightSetting
import com.bnd.network.domain.NetworkWeightSelection
import com.bnd.network.domain.NetworkWeightSetting
import com.bnd.network.domain.TemplateNetworkWeightSetting
import com.bnd.core.runnable.ComposedStateProducer
import scala.collection.JavaConversions._

protected class ComposedNetworkWeightBuilder[T, S[X]] extends NetworkWeightBuilder[ComposedStateProducer[T, _, S], T] {

	private var networkWeightResolver : UntypedNetworkWeightBuilder[T] = _

	override def setTemplateWeights[B <: T](
		composedNetwork : ComposedStateProducer[T, _, S], 
		templateWeightSetting : TemplateNetworkWeightSetting[B] 
	) = for (layer <- composedNetwork.listNestedProducers) yield networkWeightResolver.setWeights(layer, templateWeightSetting)

	override def setLayeredWeights[B <: T](
		composedNetwork : ComposedStateProducer[T, _, S], 
		layeredWeightSetting : LayeredNetworkWeightSetting[B] 
	) {
		val layerWeightSettings = layeredWeightSetting.getLayers
		if (layerWeightSettings.size != composedNetwork.listNestedProducers.size)
			throw new BndNetworkException("The number of layers of a network '" + composedNetwork.listNestedProducers.size + "' is not equal the number of layers of a weight settings '" + layerWeightSettings.size + "'.")

		val layerNetworkWeightSettingIterator = layerWeightSettings.iterator
		for (layer <- composedNetwork.listNestedProducers) 
			if (layerNetworkWeightSettingIterator.hasNext)
				networkWeightResolver.setWeights(layer, layerNetworkWeightSettingIterator.next)
			else throw new BndNetworkException("The number of layers of a network '" + composedNetwork.listNestedProducers.size + "' is not equal the number of layers of a weight settings '" + layerWeightSettings.size + "'.")
	}

	override def setFixedWeights[B <: T](
		composedNetwork : ComposedStateProducer[T, _, S], 
		fixedWeightSetting : FixedNetworkWeightSetting[B] 
	) {
		val settingOrder = fixedWeightSetting.getSettingOrder
		val weightIterator : Iterator[T] = fixedWeightSetting.getWeights.iterator
		val weightSelection = settingOrder match {
				case FixedNetworkWeightSettingOrder.SimpleOrder =>  NetworkWeightSelection.All
				case FixedNetworkWeightSettingOrder.ImmutableWithinLayerFirst => NetworkWeightSelection.ImmutableAndThenMutable
				case FixedNetworkWeightSettingOrder.ImmutableGlobalFirst => NetworkWeightSelection.Immutable
			}

		for (layer <- composedNetwork.listNestedProducers) yield networkWeightResolver.setIteratedWeights(layer, weightIterator, weightSelection)

		if (settingOrder == FixedNetworkWeightSettingOrder.ImmutableGlobalFirst)
			for (layer <- composedNetwork.listNestedProducers) yield networkWeightResolver.setIteratedWeights(layer, weightIterator, NetworkWeightSelection.Mutable)
	}

	override def setIteratedWeights[B <: T](
		composedNetwork : ComposedStateProducer[T, _, S], 
		weightIterator : Iterator[B], 
		networkWeightSelection : NetworkWeightSelection 
	) = for (layer <- composedNetwork.listNestedProducers) yield 
		networkWeightResolver.setIteratedWeights(layer, weightIterator, networkWeightSelection)

	def setNetworkWeightResolver(networkWeightResolver : UntypedNetworkWeightBuilder[T]) = {
	    this.networkWeightResolver = networkWeightResolver
	}
}