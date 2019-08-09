package com.bnd.function.business.ode

import com.bnd.core.runnable.StateProducer
import java.{lang => jl, util => ju}

import scala.collection.JavaConversions._
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.dynamics.ODESolver

abstract class ODEStateProducer[C](private val solver : ODESolver) extends StateProducer[jl.Double, C, Array] {

    override def nextState(x : Array[jl.Double], step : Option[Double]) = {
        val diffs = solver.getApproxDiffs(x)
        (x, diffs).zipped.map {(x,diff) => (x + diff) : jl.Double}
    }

	override def nextTimeStep = solver.getTimeStep
}