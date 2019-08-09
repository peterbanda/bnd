package com.bnd.network.business

import java.{util => ju, lang => jl}

import com.bnd.network.BndNetworkException
import com.bnd.network.domain._
import com.bnd.math.business.rand.RandomDistributionProvider
import com.bnd.math.business.rand.RandomDistributionProviderFactory
import com.bnd.math.domain.rand.RandomDistribution

import scala.collection.JavaConversions._

private abstract class FlatNetworkWeightBuilder[T] extends NetworkWeightBuilder[Iterable[InWeightAccessible[T]], T] {

	override def setLayeredWeights[B <: T](
		nodes : Iterable[InWeightAccessible[T]],
		layeredWeightSetting : LayeredNetworkWeightSetting[B] 
	) = throw new BndNetworkException("Layered network weight setting not expected for a flat network.")

	override def setFixedWeights[B <: T](
		nodes : Iterable[InWeightAccessible[T]],
		fixedWeightSetting : FixedNetworkWeightSetting[B] 
	) {
		val settingOrder = fixedWeightSetting.getSettingOrder
		val weightIterator = fixedWeightSetting.getWeights.iterator
		settingOrder match {
			case FixedNetworkWeightSettingOrder.SimpleOrder => setIteratedWeights(nodes, weightIterator, NetworkWeightSelection.All)
			case FixedNetworkWeightSettingOrder.ImmutableWithinLayerFirst | FixedNetworkWeightSettingOrder.ImmutableGlobalFirst => 
				setIteratedWeights(nodes, weightIterator, NetworkWeightSelection.ImmutableAndThenMutable)
			case _ => throw new BndNetworkException("'SimpleOrder', 'ImmutableWithinLayerFirst', or 'ImmutableAndThenMutable' fixed network weight setting order expected.")
		}
	}

	override def setIteratedWeights[B <: T](
		nodes : Iterable[InWeightAccessible[T]],
		weightIterator : Iterator[B],
		networkWeightSelection : NetworkWeightSelection
	) = if (networkWeightSelection == NetworkWeightSelection.ImmutableAndThenMutable) {
			setIteratedWeights(nodes, weightIterator, NetworkWeightSelection.Immutable)
			setIteratedWeights(nodes, weightIterator, NetworkWeightSelection.Mutable)
		} else for (node <- nodes)
			networkWeightSelection match {
				case NetworkWeightSelection.All => node.setWeights(weightIterator)
				case NetworkWeightSelection.Immutable => node.setImmutableWeights(weightIterator)
				case NetworkWeightSelection.Mutable => node.setMutableWeights(weightIterator)
				case _ => throw new BndNetworkException("'All', 'Immutable', or 'Mutable' network weight selection expected.")
			}
}

private class NoTemplateFlatNetworkWeightBuilder[T] extends FlatNetworkWeightBuilder[T] {

	override def setTemplateWeights[B <: T](
		nodes : Iterable[InWeightAccessible[T]],
		templateWeightSetting : TemplateNetworkWeightSetting[B] 
	) = throw new BndNetworkException("Template weights not supported by default " + this.getClass.getName)
}

private class NumberFlatNetworkWeightBuilder[T <: jl.Number] extends FlatNetworkWeightBuilder[T] {

	override def setTemplateWeights[B <: T](
		nodes : Iterable[InWeightAccessible[T]],
		templateWeightSetting : TemplateNetworkWeightSetting[B] 
	) {
		val randomDistributionProvider = RandomDistributionProviderFactory.apply(templateWeightSetting.getRandomDistribution)

		for (node <- nodes) {
			val randomWeights = randomDistributionProvider.nextList(node.getWeightsNum).asInstanceOf[ju.List[T]] 
			node.setWeights(randomWeights.iterator)
		}
	}
}

private class AnyValFlatNetworkWeightBuilder[T <: AnyVal] extends FlatNetworkWeightBuilder[T] {

	override def setTemplateWeights[B <: T](
		nodes : Iterable[InWeightAccessible[T]],
		templateWeightSetting : TemplateNetworkWeightSetting[B] 
	) {
		val randomDistributionProvider = RandomDistributionProviderFactory.apply(templateWeightSetting.getRandomDistribution)

		for (node <- nodes) {
			val randomWeights = randomDistributionProvider.nextList(node.getWeightsNum).asInstanceOf[ju.List[T]] 
			node.setWeights(randomWeights.iterator)
		}
	}
}