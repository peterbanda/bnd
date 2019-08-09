package com.bnd.core.runnable

/**
 * @author © Peter Banda
 * @since 2012
 */
trait FullStateAccessible[T, C] extends StateAccessible[T] {

	def setState(component : C, state : T) : Unit

	def getState(component : C) : T

	def componentStates : Iterable[(C, T)]

	def componentIndexMap : Map[C, Int] = componentStates.map(_._1).zipWithIndex.toMap
}
