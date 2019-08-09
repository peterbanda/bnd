package com.bnd.math.business.dynamics

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation

import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible

class TimeCorrelationProcessor[T: DoubleConvertible](val timeStepToFilter : Int) extends SingleRunProcessor[T, Array[Array[Double]]] {

    override def isTransposedStatesHistoryExpected = true

    override def process(
        stateHistories: Iterable[Iterable[T]],
        timeStepLength: Double
    ) : Array[Array[Double]] = {
        val filteredStateHistories = stateHistories.view.map(take(timeStepToFilter))

        val matrix: Array[Array[Double]] = filteredStateHistories: Iterable[Iterable[Double]]
        val correlationMatrix = new PearsonsCorrelation(matrix).getCorrelationMatrix()
        correlationMatrix.getData()
    }
}