package com.bnd.math.business.dynamics

import com.bnd.core.runnable.StateAccessible
import com.bnd.math.business.dynamics.IteratedMap._
import com.bnd.plotter.{Plotter, SeriesPlotSetting}
import org.junit.Test
import com.bnd.core.CollectionElementsConversions.javaListMatrixToScalaSeqMatrix
import com.bnd.core.runnable.TimeRunnable
import com.bnd.core.util.FileUtil

import scala.collection.JavaConversions.seqAsJavaList
import scala.math.Numeric.DoubleAsIfIntegral

class IteratedMapTest {

  // TODO: this is supposed to be provided automatically
  implicit val doubleAsIntegral = DoubleAsIfIntegral
  private val plotter = Plotter("svg")

  private val fileUtil = FileUtil.getInstance()
  import fileUtil.overwriteStringToFile

  @Test
  def testTrigonometric2dMap {
    val map = createTrigonometric2DMap(1,3)
    val initialStates = Seq(0.27, 1.2)
    map.setStates(initialStates)

    val history = collectStates(map, 1000)
    val output = plotter.plotXY(history, "Trigonometric 2D Map" )

    overwriteStringToFile(output, "Trigonometric-2D-Map.svg")
  }

  @Test
  def testLogisticMap {
    val map = createLogisticMap(4)
    map.setStates(Seq(0.1))

    val history = collectStates(map, 500)

    val output = plotter.plotSeries(
      history.toSeq.transpose,
      new SeriesPlotSetting().setTitle("Logistic Map (r = 4, x0 = 0.1)")
    )

    overwriteStringToFile(output, "Logistic-Map-r-4-x0_1.svg")
  }

  @Test
  def testLogisticMap2 {
    val map = createLogisticMap(1.5)
    map.setStates(Seq(0.2))

    val history = collectStates(map, 500)

    val output = plotter.plotSeries(
      history.toSeq.transpose,
      new SeriesPlotSetting().setTitle("Logistic Map (r = 1.5, x0 = 0.2)")
    )

    overwriteStringToFile(output, "Logistic-Map-r-1_5-x0_2.svg")
  }

  @Test
  def testLorentzSystem {
    val system = createLorenzSystem(18, 10, 8/3, 0.0001)
    system.setStates(Seq(20D, -19D, 40D))

    val history = collectStates(system, 100000)

    val output = plotter.plotXYZ(history, "Lorentz System (18, 10, 8/3)")

    overwriteStringToFile(output, "Lorentz-System-18-10-8_3-xyz.svg")
  }

  @Test
  def testLorentzSystem2 {
    val system = createLorenzSystem(18, 10, 8/3, 0.0001)
    system.setStates(Seq(20D, -19D, 40D))

    val history = collectStates(system, 100000)

    val output = plotter.plotSeries(
      history.toSeq.transpose,
      new SeriesPlotSetting().setTitle("Lorentz System (18, 10, 8/3)")
    )

    overwriteStringToFile(output, "Lorentz-System-18-10-8_3.svg")
  }

  private def collectStates[T](
    timeRunnable: TimeRunnable with StateAccessible[T],
    iterations: Int
  ): Iterable[Iterable[T]] = {
    for (i <- 0 to iterations) yield {
      if (i > 0) {
        timeRunnable.runFor(1D)
      }
      timeRunnable.getStates
    }
  }
}