package com.bnd.core.runnable

import scala.collection.mutable.Subscriber
import scala.collection.mutable.Publisher
import scala.collection.mutable.ListBuffer
import java.{util => ju}

class StateCollector[T, S[X]](implicit c : Copyable[S]) extends Subscriber[StateEvent[T, S], Publisher[StateEvent[T, S]]] {

	type Pub = Publisher[StateEvent[T, S]]

    private val timeStates = ListBuffer[(BigDecimal, S[T])]()

    def notify(pub: Pub, event: StateEvent[T, S]) =
	    event match {
	    	case StateUpdatedEvent(time : BigDecimal, components : Iterable[_], state : S[T]) =>
	    	    if (timeStates.isEmpty || timeStates.last._1 != time)
	    	    	timeStates += Tuple2(time, c.copy(state))
	    	    else {
	    	        timeStates.dropRight(1)
	    	        timeStates += Tuple2(time, c.copy(state))
	    	    }
	    	case _ =>
		}

	def collected : Seq[(BigDecimal, S[T])] = timeStates
}