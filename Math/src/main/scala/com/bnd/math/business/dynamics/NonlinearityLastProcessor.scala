package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible.Implicits._
import com.bnd.core.DoubleConvertible
import com.bnd.core.UnboundValueException
import org.apache.commons.math3.stat.regression.SimpleRegression

class NonlinearityLastProcessor[T : DoubleConvertible](
    val normalized : Boolean,
    val upperBound : Double) extends SingleRunProcessor[T, Iterable[Double]] {

    override def isTransposedStatesHistoryExpected = true

    override def process(
        stateHistories : Iterable[Iterable[T]],
        timeStepLength : Double
    ) : Iterable[Double] =
    	for (stateHistory <- stateHistories) yield {
    	    val regression = new SimpleRegression()
    	    var min = Double.PositiveInfinity
    	    var max = Double.NegativeInfinity
    	    for ((value,index) <- stateHistory.zipWithIndex) {
    	        min = Math.min(min, value)
    	        max = Math.max(max, value)
    	        if (value >= upperBound) throw new UnboundValueException(value.toString() + " violated upper bound " + upperBound) 
    	    	regression.addData(index * timeStepLength, value : Double)
    	    }
    	    val rmsd = Math.sqrt(regression.getSumSquaredErrors() / stateHistory.size)
    	    val range = if (min.equals(max)) 1 else max - min
    	    if (normalized) rmsd / range else rmsd
    	}
}