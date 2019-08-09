package com.bnd.math.business.dynamics

import com.bnd.core.runnable.StateAccessible
import com.bnd.math.business.dynamics.IteratedMap._
import com.bnd.plotter.{Plotter, SeriesPlotSetting}
import com.bnd.core.runnable.TimeRunnable
import org.junit.{Ignore, Test}

import scala.math.Numeric.DoubleAsIfIntegral

class LyapunovAnalysisTest {

  object Defaults {
    val perturbationStrength = 0.000001
    val normalizationThreshold = 0.0001
    val iterations = 10000
  }

  // TODO: this is supposed to be provided automatically
  implicit val doubleAsIntegral = DoubleAsIfIntegral
  private val lyapunovAnalysis = new LyapunovAnalysis[Double](Defaults.perturbationStrength, new EuclideanVectorSpace[Double])
  private val plotter = Plotter.apply

  @Test
  def testTrigonometricMap {
    val map = createTrigonometric2DMap(1, 3)
    val initialStates = Seq(0.27, 1.2)

    runLyapunovAnalysis(
      "Trigonometric 2D Map (Chaotic: r=0.27, b=1.2))",
      map, initialStates, 100
    )
  }

  //    @Test
  //    def testLogisticMapFixedPoint {
  //        runLyapunovAnalysis(
  //                "Logistic Map (Fixed Point: r=0.5)",
  //                createLogisticMap(0.5), Seq(0D))
  //    }
  //
  //    @Test
  //    def testLogisticMapFixedPoint2 {
  //        runLyapunovAnalysis(
  //                "Logistic Map (Fixed Point: r=1.5)",
  //                createLogisticMap(1.5), Seq(0D))
  //    }
  //
  //    @Test
  //    def testLogisticMapChaotic {
  //        runLyapunovAnalysis(
  //                "Logistic Map (Chaotic: r=4)",
  //                createLogisticMap(4), Seq(0D))
  //    }

  @Test
  @Ignore
  def testLorentzSystem {
    val system = createLorenzSystem(18, 10, 8/3, 0.0001)
    val initialStates = Seq(20D, -19D, 40D)

    runLyapunovAnalysis(
      "Lorentz System (Periodic: 18, 10, 8/3)",
      system, initialStates, 200000)
  }

  private def runLyapunovAnalysis(
    title : String,
    runnable: TimeRunnable with StateAccessible[Double],
    initialPoint: Seq[Double],
    iterations: Int = Defaults.iterations) {
    val lambdaExponentsHistory = lyapunovAnalysis.calcSpectrum(runnable, initialPoint, 1, iterations)
    plotter.plotSeries(
      lambdaExponentsHistory,
      new SeriesPlotSetting()
        .setTitle(title)
        .setTransposed(true)
    )
    println("Final Lyapunov Exponents")
    println(lambdaExponentsHistory.last)
    println("Maximal Lyapunov Exponent From Spectrum")
    println(lambdaExponentsHistory.last.max)

    val maxLambdaExponentHistory = lyapunovAnalysis.calcMaximal(runnable, initialPoint, 1, iterations, Defaults.normalizationThreshold)
    plotter.plotSingleSeries(
      maxLambdaExponentHistory,
      new SeriesPlotSetting().setTitle(title)
    )
    println("Maximal Lyapunov Exponent")
    println(maxLambdaExponentHistory.last)
  }
}