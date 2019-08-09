package com.bnd.math.business.dynamics

import java.{util => ju}

import com.bnd.core.runnable.TimeRunnable

import scala.collection.JavaConversions._
import scala.math.Numeric._
import scala.math.Integral.Implicits._
import scala.math._

trait SingleRunProcessor[T,O] {

    def process(statesHistory : Iterable[Iterable[T]], timeStepLength : Double) : O

    def isTransposedStatesHistoryExpected : Boolean
}