package com.bnd.network.business

import com.bnd.core.runnable.ComposedStateProducer
import com.bnd.network.BndNetworkException
import com.bnd.network.domain.{FixedNetworkWeightSetting, LayeredNetworkWeightSetting, NetworkWeightSelection, NetworkWeightSetting, TemplateNetworkWeightSetting}

import scala.collection.Iterator

class NetworkWeightBuilderSwitch[T, S[X]](
  composedNetworkWeightBuilder: ComposedNetworkWeightBuilder[T, S],
  flatNetworkWeightBuilder: NetworkWeightBuilder[Iterable[InWeightAccessible[T]], T]) extends UntypedNetworkWeightBuilder[T] {

  composedNetworkWeightBuilder.setNetworkWeightResolver(this)

  override def setWeights[B <: T](
    network: Any,
    weightSetting: NetworkWeightSetting[B]
  ) {
    if (network.isInstanceOf[ComposedStateProducer[T, _, S]]) {

      val composedProducer = network.asInstanceOf[ComposedStateProducer[T, _, S]]
      val nestedProducers = composedProducer.listNestedProducers

      if (nestedProducers.forall(_.isInstanceOf[InWeightAccessible[T]])) {

        val weightSettables = nestedProducers.asInstanceOf[Iterable[InWeightAccessible[T]]]
        if (weightSetting.isTemplate)
          flatNetworkWeightBuilder.setTemplateWeights(weightSettables, weightSetting.asInstanceOf[TemplateNetworkWeightSetting[T]])

        else if (weightSetting.hasLayers)
          flatNetworkWeightBuilder.setLayeredWeights(weightSettables, weightSetting.asInstanceOf[LayeredNetworkWeightSetting[T]])

        else
          flatNetworkWeightBuilder.setFixedWeights(weightSettables, weightSetting.asInstanceOf[FixedNetworkWeightSetting[T]])

      } else {
        if (weightSetting.isTemplate)
          composedNetworkWeightBuilder.setTemplateWeights(composedProducer, weightSetting.asInstanceOf[TemplateNetworkWeightSetting[T]])

        else if (weightSetting.hasLayers)
          composedNetworkWeightBuilder.setLayeredWeights(composedProducer, weightSetting.asInstanceOf[LayeredNetworkWeightSetting[T]])

        else
          composedNetworkWeightBuilder.setFixedWeights(composedProducer, weightSetting.asInstanceOf[FixedNetworkWeightSetting[T]])
      }

    } else throw new BndNetworkException("Network weight builder for " + network.getClass + " is not defined.")
  }

  override def setIteratedWeights[B <: T](
    network: Any,
    weightIterator: Iterator[B],
    networkWeightSelection: NetworkWeightSelection
  ) {
    if (network.isInstanceOf[ComposedStateProducer[T, _, S]]) {

      val composedProducer = network.asInstanceOf[ComposedStateProducer[T, _, S]]
      val nestedProducers = composedProducer.listNestedProducers

      if (nestedProducers.forall(_.isInstanceOf[InWeightAccessible[T]])) {
        val weightSettables = nestedProducers.asInstanceOf[Iterable[InWeightAccessible[T]]]
        flatNetworkWeightBuilder.setIteratedWeights(weightSettables, weightIterator, networkWeightSelection)
      } else {
        composedNetworkWeightBuilder.setIteratedWeights(composedProducer, weightIterator, networkWeightSelection)
      }

    } else throw new BndNetworkException("Network weight builder for " + network.getClass + " is not defined.")
  }
}