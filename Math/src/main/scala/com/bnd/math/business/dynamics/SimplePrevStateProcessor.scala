package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._

class SimplePrevStateProcessor[T, I](calcWithPrev : (T,T,Double) => I) extends PrevStateProcessor[T, I, Iterable[I]](calcWithPrev) {

    override def postProcess(
        zippedStates: Iterable[I],
        timeStepLength: Double) = zippedStates
}