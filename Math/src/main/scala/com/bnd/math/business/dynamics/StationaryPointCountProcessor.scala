package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import scala.math.Ordering.Implicits._
import com.bnd.core.DoubleConvertible
import com.bnd.core.DoubleConvertible.Implicits._

class StationaryPointCountProcessor[T : DoubleConvertible](val detectionPrecision : Double) extends PrevStateProcessor[T, Int, Int](
    (state, prevState, _) => {
        val diff = state - prevState
        if (diff > detectionPrecision) 1
        	else 
        if (diff < -detectionPrecision) -1
        	else 0
    }) {

    override def postProcess(
        comparisons: Iterable[Int],
        timeStepLength: Double
    ) : Int =
        (comparisons.dropRight(1), comparisons.tail).zipped.map(
        	(comp, prevComp) => if (!comp.equals(prevComp)) 1 else 0
        ).sum
}