package com.bnd.plotter

import org.junit.Test
import org.junit.Assert._

class PlotterTest {

  @Test
  def testNoExport {
    val plotter = Plotter.apply()
    plotter.plotCounts(List(0.1, 0.2, 0.3, 0.15, 0.22), "Test", 0.1, true)
  }

  @Test
  def testNoExport2 {
    val plotter = Plotter.apply()
    plotter.plotXYDots(List(List(0.1, 0.2), List(0.11, 0.21), List(0.12, 0.22)), "Test")
  }

  @Test
  def testPng {
    val plotter = Plotter("png")
    val output = plotter.plotCounts(List(0.1, 0.2, 0.3, 0.15, 0.22), "Test", 0.1, true)
    println(output)
  }

  @Test
  def testJpg {
    val plotter = Plotter("jpg")
    val output = plotter.plotCounts(List(0.1, 0.2, 0.3, 0.15, 0.22), "Test", 0.1, true)
    println(output)
  }

  @Test
  def testSvg {
    val plotter = Plotter("svg")
    val output = plotter.plotSeries(
      List(List(0.1, 0.2, 0.3, 0.15, 0.22), List(0.2, 0.3, 0.5, 0.22, 0.4)),
      new SeriesPlotSetting().setTitle("Test SVG").setShowLegend(true)
    )
    println(output)
  }
}