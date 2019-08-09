package com.bnd.network.domain

import java.{lang => jl}

import com.bnd.math.domain.learning.MachineLearningSetting
import com.bnd.math.domain.rand.RandomDistribution

class ReservoirLearningSetting extends MachineLearningSetting {

  private var inScale: Double = _
  private var outScale: Double = _
  private var bias: Double = _
  private var nonBiasInitial: Double = _
  private var reservoirNodeNum: Int = _
  private var reservoirInDegree: Option[Int] = None
  private var reservoirInDegreeDistribution: Option[RandomDistribution[Integer]] = None
  private var reservoirEdgesNum: Option[Int] = None
  private var reservoirPreferentialAttachment: Boolean = _
  private var reservoirBias: Boolean = _
  private var reservoirCircularInEdges: Option[Seq[Int]] = None

  private var inputReservoirConnectivity: Double = _
  private var weightDistribution: RandomDistribution[jl.Double] = _
  private var reservoirSpectralRadius: Double = _
  private var reservoirFunctionType: ActivationFunctionType = _
  private var reservoirFunctionParams: Option[Seq[jl.Double]] = None
  private var perNodeReservoirFunctionWithParams: Option[Stream[(ActivationFunctionType, Seq[jl.Double])]] = None
  private var weightAdaptationIterationNum: Int = _
  private var washoutPeriod: Int = _

  def setInScale(inScale: Double) = this.inScale = inScale

  def setOutScale(outScale: Double) = this.outScale = outScale

  def setBias(bias: Double) = this.bias = bias

  def setNonBiasInitial(nonBiasInitial: Double) = this.nonBiasInitial = nonBiasInitial

  def setReservoirNodeNum(reservoirNodeNum: Int) = this.reservoirNodeNum = reservoirNodeNum

  def setReservoirInDegree(reservoirInDegree: Option[Int]) = this.reservoirInDegree = reservoirInDegree

  def setReservoirInDegreeDistribution(reservoirInDegreeDistribution: Option[RandomDistribution[Integer]]) = this.reservoirInDegreeDistribution = reservoirInDegreeDistribution

  def setReservoirEdgesNum(reservoirEdgesNum: Option[Int]) = this.reservoirEdgesNum = reservoirEdgesNum

  def setReservoirPreferentialAttachment(reservoirPreferentialAttachment: Boolean) = this.reservoirPreferentialAttachment = reservoirPreferentialAttachment

  def setReservoirBias(reservoirBias: Boolean) = this.reservoirBias = reservoirBias

  def setInputReservoirConnectivity(inputReservoirConnectivity: Double) = this.inputReservoirConnectivity = inputReservoirConnectivity

  def setWeightDistribution(weightDistribution: RandomDistribution[jl.Double]) = this.weightDistribution = weightDistribution

  def setReservoirSpectralRadius(reservoirSpectralRadius: Double) = this.reservoirSpectralRadius = reservoirSpectralRadius

  def setReservoirFunctionType(reservoirFunctionType: ActivationFunctionType) = this.reservoirFunctionType = reservoirFunctionType

  def setReservoirFunctionParams(reservoirFunctionParams: Option[Seq[jl.Double]]) = this.reservoirFunctionParams = reservoirFunctionParams

  def setPerNodeReservoirFunctionWithParams(perNodeReservoirFunctionWithParams: Option[Stream[(ActivationFunctionType, Seq[jl.Double])]]) = this.perNodeReservoirFunctionWithParams = perNodeReservoirFunctionWithParams

  def setWeightAdaptationIterationNum(weightAdaptationIterationNum: Int) = this.weightAdaptationIterationNum = weightAdaptationIterationNum

  def setWashoutPeriod(washoutPeriod: Int) = this.washoutPeriod = washoutPeriod

  def setReservoirCircularInEdges(reservoirCircularInEdges: Option[Seq[Int]]) = this.reservoirCircularInEdges = reservoirCircularInEdges

  def getInScale = inScale

  def getOutScale = outScale

  def getBias = bias

  def getNonBiasInitial = nonBiasInitial

  def getReservoirNodeNum = reservoirNodeNum

  def getReservoirInDegree = reservoirInDegree

  def getReservoirInDegreeDistribution = reservoirInDegreeDistribution

  def getReservoirEdgesNum = reservoirEdgesNum

  def getReservoirPreferentialAttachment = reservoirPreferentialAttachment

  def getReservoirBias = reservoirBias

  def getInputReservoirConnectivity = inputReservoirConnectivity

  def getWeightDistribution = weightDistribution

  def getReservoirSpectralRadius = reservoirSpectralRadius

  def getReservoirFunctionType = reservoirFunctionType

  def getReservoirFunctionParams = reservoirFunctionParams

  def getPerNodeReservoirFunctionWithParams = perNodeReservoirFunctionWithParams

  def getWeightAdaptationIterationNum = weightAdaptationIterationNum

  def getWashoutPeriod = washoutPeriod

  def getReservoirCircularInEdges: Option[Seq[Int]] = reservoirCircularInEdges
}