package com.bnd.core.runnable

import scala.collection.Map

abstract class StateInterpretation[T, C, H, I <: StateInterpretationItem[T, C, H]](val items : Iterable[I]){
	def startTime : BigDecimal
	def timeLength : BigDecimal
}

case class RangeStateInterpretation[T, C, H](
	private val startTime_ : BigDecimal,
	private val timeLength_ : BigDecimal,
	items_ : Iterable[RangeStateInterpretationItem[T, C, H]]) extends StateInterpretation[T, C, H, RangeStateInterpretationItem[T, C, H]](items_){

	override def startTime = startTime_
	override def timeLength = timeLength_
}

case class PointStateInterpretation[T, C, H](
	private val applyTime : BigDecimal,
	items_ : Iterable[PointStateInterpretationItem[T, C, H]]) extends StateInterpretation[T, C, H, PointStateInterpretationItem[T, C, H]](items_){

	val zero : BigDecimal = 0

  	override def startTime = applyTime
  	override def timeLength = zero
}

final class StateIdentityInterpretation[T, C](
	applyTime : BigDecimal,
	val components : Iterable[C]) extends PointStateInterpretation[T, C, C](
			applyTime,
	        components.map(component => 
	            new PointStateInterpretationItem[T, C, C](component, List(component), {(stateMap, _) => stateMap.get(component).get})))

abstract class StateInterpretationItem[+T, +C, +H](
    val variable : H,
    val components : Iterable[C])

case class RangeStateInterpretationItem[T, C, H](
    variable_ : H,
    components_ : Iterable[C],
    val fun : (Map[C, Seq[T]], Map[H, T]) => T) extends StateInterpretationItem(variable_, components_)

case class PointStateInterpretationItem[T, C, H](
    variable_ : H,
    components_ : Iterable[C],
    val fun : (Map[C, T], Map[H, T]) => T) extends StateInterpretationItem(variable_, components_)

class StateInterpretationResult[T, C, H](
	val interpretation : StateInterpretation[T, C, H, _],
	val items : Iterable[StateInterpretationResultItem[T, C, H]])

class StateInterpretationResultItem[T, C, H](
	val interpretationItem : StateInterpretationItem[T, C, H],
	val interpretedValue : T)