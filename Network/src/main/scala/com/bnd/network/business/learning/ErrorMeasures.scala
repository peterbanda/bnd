package com.bnd.network.business.learning

import java.{lang => jl}

object ErrorMeasures {
  def calcSquares(outputs: Seq[jl.Double], desiredOutputs: Seq[jl.Double]) =
    (outputs, desiredOutputs).zipped.map { case (y, ye) => (y - ye) * (y - ye)}

  def calcSamps(outputs: Seq[jl.Double], desiredOutputs: Seq[jl.Double]) =
    (outputs, desiredOutputs).zipped.map { case (y, ye) => if (y + ye == 0) 100 else 100d * math.abs(y - ye) / (math.abs(y) + math.abs(ye))}

  def calcUpDownMatches(outputs: Seq[jl.Double], desiredOutputs: Seq[jl.Double]) =
    (outputs, desiredOutputs).zipped.map { case (y, ye) => if ((y > 0 && ye > 0) || (y < 0 && ye < 0)) 1d else 0d}

  def calcUpDownMatchesAbs(outputs: Seq[jl.Double], desiredOutputs: Seq[jl.Double]) =
    (outputs, desiredOutputs).zipped.map { case (y, ye) => if ((y > 0 && ye > 0) || (y < 0 && ye < 0)) math.abs(ye) else -math.abs(ye)}
}
