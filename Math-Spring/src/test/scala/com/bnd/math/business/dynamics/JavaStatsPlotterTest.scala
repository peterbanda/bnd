package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import com.bnd.math.domain.{Stats, StatsType}
import com.bnd.plotter.Plotter
import org.junit.Test

class JavaStatsPlotterTest {

  private val plotter = new JavaStatsPlotter(Plotter())

  @Test
  def testOrthonormalizeVectors1() {
    val data = Seq(Seq(
      new Stats{setPos(0D); setMean(0.1); setMin(0.01); setMax(0.2)},
      new Stats{setPos(0.1D); setMean(0.2); setMin(0.02); setMax(0.3)},
      new Stats{setPos(0.2D); setMean(0.4); setMin(0.08); setMax(0.6)}
    ),
    Seq(
      new Stats{setPos(0D); setMean(0.12); setMin(0.018); setMax(0.253)},
      new Stats{setPos(0.1D); setMean(0.25); setMin(0.025); setMax(0.3543)},
      new Stats{setPos(0.2D); setMean(0.46); setMin(0.081); setMax(0.676)}
    ))

    plotter.plotStats(data, StatsType.Mean, "Test", "X", "Y", null, null)
  }
}