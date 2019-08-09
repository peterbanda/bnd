package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import scala.math.Ordering.Implicits._
import com.bnd.core.DoubleConvertible
import com.bnd.core.DoubleConvertible.Implicits._

class StationaryPointPerTimeProcessor[T : DoubleConvertible](val detectionPrecision : Double) extends PrevStateProcessor[T, Int, Iterable[Double]](
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
    ) : Iterable[Double] = {
        var count = 0
        (comparisons.dropRight(1), comparisons.tail, (1 to comparisons.size - 1)).zipped.map(
                (comp, prevComp, index) => {
                    if (!comp.equals(prevComp)) count += 1
                    count.toDouble / index
                }
        )
    }
}