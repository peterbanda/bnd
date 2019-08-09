package com.bnd.core.runnable

import java.{util => ju}

import SeqIndexAccessible.Implicits._
import com.bnd.core.BndRuntimeException

trait StateHolder[T, C, S[X]] extends FullStateAccessible[T, C] {

	private[runnable] var states = initStates

	protected def fillStates(num : Int, state : T) : Unit
	protected def initStates : S[T]
	protected def setState(index : Int, state : T) : Unit
	protected def getState(index : Int) : T

	override def setState(component : C, state : T) : Unit = {
		val index = componentIndex(component)
		if (index.isDefined) setState(index.get, state)
	}

	override def getState(component : C) = {
		val index = componentIndex(component)
		if (index.isDefined)
			getState(index.get)
		else throw new BndRuntimeException("State for component " + component + " not found.")
	}

	protected def componentIndex(component : C) : Option[Int]
}

abstract class ComponentsStateHolder[T, C, S[X]](val components : Iterable[C]) extends StateHolder[T, C, S] with Serializable {

	private[runnable] val componentIndexMap_ = components.zipWithIndex.toMap
	override def componentIndexMap = componentIndexMap_
	override protected def componentIndex(component : C) = componentIndexMap.get(component)
}

class SeqIndexAccessibleStateHolder[T : Manifest, C, S[X]](
	components : Iterable[C])(
	implicit sia : SeqIndexAccessible[S],
	jlc : JavaListConvertible[S]
) extends ComponentsStateHolder[T, C, S](components) {

	override protected def fillStates(num : Int, state : T) : Unit = states = sia.fill(num, state)
	override protected def initStates : S[T] = sia.empty
	override protected def setState(idx : Int, state : T) = states.update(idx, state)
	override protected def getState(idx : Int) = states(idx)

	override def getStates = jlc.toJavaList(states)
	override def setStates(states : ju.List[T]) = this.states = jlc.fromJavaList(states)
	override def componentStates = components.zip(states.iterator.toIterable)
}