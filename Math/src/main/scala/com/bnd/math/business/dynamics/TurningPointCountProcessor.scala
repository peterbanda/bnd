package com.bnd.math.business.dynamics

import scala.math.Ordering.Implicits._
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible
import com.bnd.core.DoubleConvertible.Implicits._
import com.bnd.core.UnboundValueException

class TurningPointCountProcessor[T : DoubleConvertible](
    val detectionPrecision : Double,
    val extremeDiff : Double,
    val upperBound : Double) extends PrevStateProcessor[T, (Int,Double), Int](

    (prevState, state, _) => {
        if ((state : Double) >= upperBound) throw new UnboundValueException(state.toString() + " violated upper bound " + upperBound) 
        if ((prevState : Double) >= upperBound) throw new UnboundValueException(prevState.toString() + " violated upper bound " + upperBound)
        val diff = state - prevState
        if (diff > detectionPrecision) (1, state)
        	else 	
        if (diff < -detectionPrecision) (-1, state)
        	else (0, state)
    }) {

    override def postProcess(
        comparisons: Iterable[(Int, Double)],
        timeStepLength: Double
    ) : Int = {
        val compressedComps = comparisons.filter{case (x,_) => x != 0}
        if (compressedComps.isEmpty) 0 else {
        	val localMinMax = (compressedComps.dropRight(1), compressedComps.tail).zipped.foldLeft(List[Double](compressedComps.head._2)) {
        		(list, prevNextComp) => prevNextComp match { 
            		case ((prevDer,prevValue),(der,value)) => 
            		if (!prevDer.equals(der)) value :: list else list
        		}
        	}
        	val a =
        		(localMinMax.dropRight(1), localMinMax.tail).zipped.map(
        			(prevLocalExtreme, localExtreme) => if (Math.abs(localExtreme - prevLocalExtreme) >  extremeDiff) 1 else 0
        		).sum
        	if (a > 200) println("Turning points count " + a)
        	a
        }
    }
}