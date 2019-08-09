package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._

abstract class PrevStateProcessor[T, I, O](calcWithPrev : (T,T,Double) => I) extends SingleRunProcessor[T, Iterable[O]] {

    override def isTransposedStatesHistoryExpected = true

    override def process(
    	stateHistories : Iterable[Iterable[T]],
    	timeStepLength : Double
    ) : Iterable[O] =
        for (stateHistory <- stateHistories) yield {
            val zippedStates = (stateHistory.dropRight(1), stateHistory.tail).zipped.map(calcWithPrev(_,_,timeStepLength))
            postProcess(zippedStates, timeStepLength)
        }

    def postProcess(zippedStates : Iterable[I], timeStepLength : Double) : O 
}