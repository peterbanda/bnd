package com.bnd.core.runnable

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Publisher
import scala.collection.mutable.Subscriber
import java.{util => ju}

class AlteredStateCollector[T, C, S[X] : Copyable] extends Subscriber[StateEvent[T, S], Publisher[StateEvent[T, S]]] {

	type Pub = Publisher[StateEvent[T, S]]

    private val timeComponentStates = ListBuffer[(BigDecimal, Iterable[(C, T)])]()

    def notify(pub: Pub, event: StateEvent[T, S]) =
	    event match {
	    	case StateAlteredEvent(time : BigDecimal, componentStates : Iterable[(C,T)]) => timeComponentStates += Tuple2(time, componentStates)
	    	case _ =>
		}

	def collected : Seq[(BigDecimal, Iterable[(C, T)])] = timeComponentStates
}