package com.bnd.network.business

import com.bnd.network.domain.NetworkWeightSelection
import com.bnd.network.domain.NetworkWeightSetting

trait UntypedNetworkWeightBuilder[-T] {

	def setWeights[B <: T](
		network : Any,
		weightSetting : NetworkWeightSetting[B])

	def setIteratedWeights[B <: T](
		network : Any,
		weightIterator : Iterator[B],
		networkWeightSelection : NetworkWeightSelection)
}