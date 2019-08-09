package com.bnd.core.runnable

/**
 * @author © Peter Banda
 * @since 2013
 */
trait StateProducer[T, C, S[X]] {

	def nextState(inputState : S[T], timeStep : Option[Double]) : S[T]

	def nextTimeStep : Double

	def isConstantTimeStep : Boolean

	def listInputComponentsInOrder : Iterable[C]

	def listOutputComponentsInOrder : Iterable[C]
}
