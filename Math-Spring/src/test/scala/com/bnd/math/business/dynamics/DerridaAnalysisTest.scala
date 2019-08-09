package com.bnd.math.business.dynamics

import com.bnd.core.runnable.StateAccessible
import com.bnd.math.business.dynamics.IteratedMap._
import com.bnd.plotter.Plotter
import com.bnd.core.runnable.TimeRunnable
import org.junit.Test

import scala.math.Numeric.DoubleAsIfIntegral
import scala.util.Random

class DerridaAnalysisTest {

  object Defaults {
    val perturbationStrength = 0.00001
    val repetitions = 1000
  }

  // TODO: this is supposed to be provided automatically
  implicit val doubleAsIntegral = DoubleAsIfIntegral
  val plotter = Plotter.apply
  val derridaAnalysis = new DerridaAnalysis[Double](
    Defaults.perturbationStrength,
    new EuclideanVectorSpace[Double],
    (min,max) => min + (max - min) * Random.nextDouble())

  @Test
  def testTrigonometricMap {
    val map = createTrigonometric2DMap(1, 3)
    val minPoint = Seq(0.27, 1.2)
    val maxPoint = Seq(0.3, 1.3)

    runDerridaAnalysis(
      "Trigonometric 2D Map (Chaotic: [0.27, 1.2] -> [0.3, 1.3])",
      map, minPoint, maxPoint
    )
  }

  @Test
  def testLogisticMapFixedPoint {
    runDerridaAnalysis(
      "Logistic Map (Fixed Point: r=0.5, [0] -> [0.1])",
      createLogisticMap(0.5),
      Seq(0D), Seq(0.1D)
    )
  }

  @Test
  def testLogisticMapFixedPoint2 {
    runDerridaAnalysis(
      "Logistic Map (Fixed Point?: r=1.5, [0] -> [0.1])",
      createLogisticMap(1.5),
      Seq(0D), Seq(0.1D)
    )
  }

  @Test
  def testLogisticMapChaotic {
    runDerridaAnalysis(
      "Logistic Map (Chaotic: r=4, [0] -> [0.5])",
      createLogisticMap(4),
      Seq(0D), Seq(0.5D)
    )
  }

  @Test
  def testLogisticMapChaoticPerturb {
    runDerridaAnalysisPerturb(
      "Logistic Map (Chaotic: r=4, perturb [0])",
      createLogisticMap(4),
      Seq(0D)
    )
  }

  @Test
  def testLorentzSystem {
    val system = createLorenzSystem(18, 10, 8/3, 0.0001)
    val minPoint = Seq(-10D, -10D, -10D)
    val maxPoint = Seq(10D, 10D, 10D)

    runDerridaAnalysis(
      "Lorentz System (Periodic: 18, 10, 8/3)",
      system,
      minPoint, maxPoint
    )
  }

  private def runDerridaAnalysis(
    title : String,
    runnable: TimeRunnable with StateAccessible[Double],
    minPoint: Seq[Double],
    maxPoint: Seq[Double],
    repetitions: Int = Defaults.repetitions) {
    val diffs = derridaAnalysis.analyze(runnable, minPoint, maxPoint, 1, repetitions)
    //        val singletons = diffs map (x => List(x))
    plotter.plotXYWithRanges(diffs, 0D, 1D, 0D, title)
  }

  private def runDerridaAnalysisPerturb(
    title : String,
    runnable: TimeRunnable with StateAccessible[Double],
    point: Seq[Double],
    repetitions: Int = Defaults.repetitions) {
    val diffs = derridaAnalysis.run(runnable, point, 1, repetitions)
    //        val singletons = diffs map (x => List(x))
    plotter.plotXYWithRanges(diffs, 0D, 1D, 0D, title)
  }
}