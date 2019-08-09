package com.bnd.core.runnable

import java.{util => ju}
import java.{util => ju}

abstract class StateEvent[T, S[X] : Copyable]
case class StateUpdatedEvent[T, C, S[X] : Copyable](time : BigDecimal, components : Iterable[C], state : S[T]) extends StateEvent[T, S]
case class IllegalStateEvent[T, C, S[X] : Copyable](time : BigDecimal, component : C, state : T) extends StateEvent[T, S]
case class StateAlteredEvent[T, C, S[X] : Copyable](time : BigDecimal, componentStates : Iterable[(C, T)]) extends StateEvent[T, S]