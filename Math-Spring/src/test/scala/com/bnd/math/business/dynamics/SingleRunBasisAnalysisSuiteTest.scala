package com.bnd.math.business.dynamics

import com.bnd.core.DoubleConvertible.DoubleAsDoubleConvertible
import com.bnd.core.runnable.StateAccessible
import com.bnd.math.business.dynamics.DynamicsAnalysisResultPlotter.plotBasic
import com.bnd.math.business.dynamics.IteratedMap._
import com.bnd.core.runnable.TimeRunnable
import org.junit.{Ignore, Test}

class SingleRunBasisAnalysisSuiteTest {

  object Defaults {
    val repetitions = 10000
    val timeCorrelationStepToFilter = 100
    val fixedPointDetectionPrecision = 0.0000001
  }

  private val basicAnalysisSuite = new SingleRunBasicAnalysisSuite[Double](
    Defaults.timeCorrelationStepToFilter,
    Defaults.fixedPointDetectionPrecision,
    Defaults.fixedPointDetectionPrecision,
    Double.NegativeInfinity,
    Double.PositiveInfinity
  )

  @Test
  def testTrigonometricMap {
    val map = createTrigonometric2DMap(1, 3)
    val initialPoint = Seq(0.27, 1.2)

    runBasicAnalysis(
      "Trigonometric 2D Map (Chaotic: [0.27, 1.2] -> [0.3, 1.3])",
      map, initialPoint)
  }

  @Test
  @Ignore
  def testLorentzSystem {
    val system = createLorenzSystem(18, 10, 8/3, 0.0001)
    val initialPoint = Seq(-10D, -10D, -10D)

    runBasicAnalysis(
      "Lorentz System (Periodic: 18, 10, 8/3)",
      system, initialPoint)
  }

  private def runBasicAnalysis(
    title : String,
    runnable: TimeRunnable with StateAccessible[Double],
    point: Seq[Double],
    repetitions: Int = Defaults.repetitions) {
    val result = basicAnalysisSuite.run(runnable, point, 1, repetitions)
    plotBasic(result, title)
  }
}