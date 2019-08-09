package com.bnd.network.domain

import java.{lang => jl}
import com.bnd.math.domain.rand.RandomDistribution

case class ReservoirSetting(
  inputNodeNum: Int,
  bias: Double,
  nonBiasInitial: Double,
  reservoirNodeNum: Int,
  reservoirInDegree: Option[Int],
  reservoirInDegreeDistribution: Option[RandomDistribution[Integer]] = None,
  reservoirEdgesNum: Option[Int] = None,
  reservoirPreferentialAttachment: Boolean = false,
  reservoirBias: Boolean,
  reservoirCircularInEdges: Option[Seq[Int]] = None,
  reservoirAllowSelfEdges: Boolean = true,
  reservoirAllowMultiEdges: Boolean = false,
  inputReservoirConnectivity: Double,
  weightDistribution: RandomDistribution[jl.Double],
  reservoirSpectralRadius: Option[Double] = None,
  reservoirFunctionType: ActivationFunctionType,
  reservoirFunctionParams: Seq[Double] = Nil,
  perNodeReservoirFunctionWithParams: Option[Stream[(ActivationFunctionType, Seq[jl.Double])]] = None
)