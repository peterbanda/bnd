package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible.Implicits._
import com.bnd.core.DoubleConvertible
import org.apache.commons.math3.stat.regression.SimpleRegression

class NonlinearityProcessor[T : DoubleConvertible](val normalized : Boolean) extends SingleRunProcessor[T, Iterable[Iterable[Double]]] {

    override def isTransposedStatesHistoryExpected = true

    override def process(
        stateHistories : Iterable[Iterable[T]],
        timeStepLength : Double
    ) : Iterable[Iterable[Double]] =
    	for (stateHistory <- stateHistories) yield {
    	    val regression = new SimpleRegression()
    	    var min = Double.PositiveInfinity
    	    var max = Double.NegativeInfinity
    	    for ((value,index) <- stateHistory.zipWithIndex) yield {
    	        min = Math.min(min, value)
    	        max = Math.max(max, value)
    	    	regression.addData(index * timeStepLength, value : Double)
    	    	val rmsd = Math.sqrt(regression.getSumSquaredErrors() / (index + 1))
    	    	val range = if (min.equals(max)) 1 else max - min
    	    	if (normalized) rmsd / range else rmsd
    	    }
    	}
}