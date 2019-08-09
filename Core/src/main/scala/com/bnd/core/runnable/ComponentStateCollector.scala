package com.bnd.core.runnable

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Publisher
import scala.collection.mutable.Subscriber
import java.{util => ju}

class ComponentStateCollector[T, C, S[X]](implicit c : Copyable[S]) extends Subscriber[StateEvent[T, S], Publisher[StateEvent[T, S]]] {

	type Pub = Publisher[StateEvent[T, S]]

	private var components_ : Option[Iterable[C]] = None
    private val timeStates = ListBuffer[(BigDecimal, S[T])]()
    private var lastTime : BigDecimal = null

    def notify(pub: Pub, event: StateEvent[T, S]) =
	    event match {
	    	case StateUpdatedEvent(time : BigDecimal, components : Iterable[C], state : S[T]) => {
	    	    if (!components_.isDefined) components_ = Some(components)
	    	    if (timeStates.isEmpty || lastTime != time) {
	    	    	lastTime = time
	    	    	timeStates += Tuple2(time, c.copy(state))
	    	    } else {
	    	        timeStates.update(timeStates.size - 1, Tuple2(time, c.copy(state))) 
	    	    }
	    	}
	    	case _ =>
		}

	def collected : Seq[(BigDecimal, S[T])] = timeStates

	def components = components_.get
}