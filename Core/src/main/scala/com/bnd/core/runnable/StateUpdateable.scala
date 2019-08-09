package com.bnd.core.runnable

/**
 * @author © Peter Banda
 * @since 2013
 */
trait StateUpdateable[T, S[X]] {

    def updateState(currentState : S[T], timeStep : Option[Double])
}
