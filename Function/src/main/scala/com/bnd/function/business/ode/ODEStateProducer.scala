package com.bnd.function.business.ode

import java.{lang => jl, util => ju}
import scala.collection.JavaConverters._
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.dynamics.ODESolver
import com.bnd.core.runnable.StateProducer

abstract class ODEStateProducer[C](private val solver : ODESolver) extends StateProducer[jl.Double, C, Array] {

    override def nextState(x : Array[jl.Double], step : Option[Double]) = {
        val diffs = solver.getApproxDiffs(x)
        (x, diffs).zipped.map {(x,diff) => (x + diff) : jl.Double}
    }

	override def nextTimeStep = solver.getTimeStep
}