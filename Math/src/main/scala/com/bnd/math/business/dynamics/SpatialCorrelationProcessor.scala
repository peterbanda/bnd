package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation
import com.bnd.core.DoubleConvertible

class SpatialCorrelationProcessor[T : DoubleConvertible] extends SingleRunProcessor[T, Array[Array[Double]]] {

    override def isTransposedStatesHistoryExpected = false

    override def process(
    	statesHistory : Iterable[Iterable[T]], 
    	timeStepLength : Double
    ) : Array[Array[Double]] = {
        val matrix : Array[Array[Double]] = statesHistory : Iterable[Iterable[Double]]
        val correlationMatrix  = (new PearsonsCorrelation(matrix)).getCorrelationMatrix()
        correlationMatrix.getData()
    }
}