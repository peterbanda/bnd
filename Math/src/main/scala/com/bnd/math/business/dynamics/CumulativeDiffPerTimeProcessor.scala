package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation
import com.bnd.core.DoubleConvertible
import com.bnd.core.DoubleConvertible.Implicits._

class CumulativeDiffPerTimeProcessor[T: DoubleConvertible] extends SimplePrevStateProcessor[T, Double](
    (state, prevState, _) => Math.abs(state - prevState)) {

    override def postProcess(
        absDiffs: Iterable[Double],
        timeStepLength: Double) = {
        val cumulativeDiffs = absDiffs.scanLeft(0D)(_ + _)

        cumulativeDiffs.zipWithIndex.map {
            case (cumulativeDiffAtTime, index) => if (index == 0) 0 else cumulativeDiffAtTime / (index * timeStepLength)
        }
    }
}