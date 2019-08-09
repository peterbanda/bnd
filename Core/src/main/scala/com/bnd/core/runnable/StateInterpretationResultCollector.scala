package com.bnd.core.runnable

import scala.collection.mutable.Subscriber
import scala.collection.mutable.Publisher
import scala.collection.mutable.ListBuffer

class StateInterpretationResultCollector[T, C, H] extends Subscriber[StateInterpretationEvent[T, C, H], Publisher[StateInterpretationEvent[T, C, H]]] {

    private val results = ListBuffer[StateInterpretationResult[T, C, H]]()

	def notify(pub: Publisher[StateInterpretationEvent[T, C, H]], event: StateInterpretationEvent[T, C, H]) = results += event.result

	def collected : Seq[StateInterpretationResult[T, C, H]] = results
}