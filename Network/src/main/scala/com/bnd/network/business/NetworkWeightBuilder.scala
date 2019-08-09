package com.bnd.network.business

import com.bnd.network.domain.FixedNetworkWeightSetting
import com.bnd.network.domain.LayeredNetworkWeightSetting
import com.bnd.network.domain.NetworkWeightSelection
import com.bnd.network.domain.TemplateNetworkWeightSetting
import com.bnd.core.runnable.StateProducer

trait NetworkWeightBuilder[N, -T] {

	def setTemplateWeights[B <: T](
		networkStateProducer : N,
		templateWeightSetting : TemplateNetworkWeightSetting[B])

	def setLayeredWeights[B <: T](
		networkStateProducer : N,
		layeredWeightSetting : LayeredNetworkWeightSetting[B])

	def setFixedWeights[B <: T](
		networkStateProducer : N,
		fixedWeightSetting : FixedNetworkWeightSetting[B])

	def setIteratedWeights[B <: T](
		networkStateProducer : N,
		weightIterator : Iterator[B],
		networkWeightSelection : NetworkWeightSelection)
}