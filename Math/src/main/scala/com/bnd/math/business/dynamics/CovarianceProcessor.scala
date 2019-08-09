package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import org.apache.commons.math3.stat.correlation.Covariance
import com.bnd.core.DoubleConvertible

@Deprecated
class CovarianceProcessor[T : DoubleConvertible] extends SingleRunProcessor[T, Array[Array[Double]]] {

    override def isTransposedStatesHistoryExpected = false

    override def process(
    	statesHistory : Iterable[Iterable[T]],
    	timeStepLength : Double
    ) : Array[Array[Double]] = {
        val matrix : Array[Array[Double]] = statesHistory : Iterable[Iterable[Double]]
        val covarianceMatrix  = new Covariance(matrix).getCovarianceMatrix()
        covarianceMatrix.getData()
    }
}