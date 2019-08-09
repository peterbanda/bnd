package com.bnd.core.runnable

abstract class SingleStateProducer[T: Manifest, C, S[X]] extends StateProducer[T, C, S] with Serializable {

  def nextSingleState(currentState: S[T], timeStep: Option[Double]): T

  def outputComponent: C

  def singleton[X: Manifest](element: X): S[X]

  override def nextState(currentState: S[T], timeStep: Option[Double]) = singleton(nextSingleState(currentState, timeStep))

  override def listOutputComponentsInOrder = List(outputComponent)
}

abstract class ConstantTimeStepSingleStateProducer[T: Manifest, C, S[X]](_nextTimeStep: Double) extends SingleStateProducer[T, C, S] with Serializable {

  override def nextTimeStep = _nextTimeStep

  override def isConstantTimeStep = true
}