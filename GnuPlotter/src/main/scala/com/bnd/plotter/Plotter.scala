package com.bnd.plotter

import java.io.StringBufferInputStream
import java.{lang => jl}

import com.bnd.core.CollectionElementsConversions._

import scala.collection.JavaConverters._
import com.panayotis.gnuplot.JavaPlot
import com.panayotis.gnuplot.plot.DataSetPlot
import com.panayotis.gnuplot.style.PlotStyle
import com.panayotis.gnuplot.style.Style
import com.panayotis.gnuplot.terminal.ImageTerminal
import com.panayotis.gnuplot.terminal.SVGTerminal
import com.bnd.core.util.RenderedImageUtil._
import com.bnd.core.XYExtractorUtil._
import com.bnd.core.DoubleConvertible
import com.bnd.core.DoubleConvertible.Implicits.toDouble
import java.{lang => jl}

import com.bnd.core.BndRuntimeException

private class DisplayPlotter extends Plotter[Unit] {

  override protected def createOutput(plot: JavaPlot) = ()
}

private class ExportPlotter(val exportFormat: String) extends Plotter[String] {

  override protected def createPlot(
    title : String,
    xlabel : String,
    ylabel : String,
    zlabel : Option[String] = None
  ) = {
    val javaPlot = super.createPlot(title, xlabel, ylabel, zlabel)
    javaPlot.setTerminal(createTerminal(exportFormat))
    javaPlot
  }

  private def createTerminal(format: String) =
    format match {
      case "svg" => new SVGTerminal() {
        processOutput(new StringBufferInputStream(""))}
      case "png" | "gif" | "jpg" => new ImageTerminal() {
        processOutput(new StringBufferInputStream(""))}
      case _ => throw new BndRuntimeException(format + " format not recognized.")
    }

  override protected def createOutput(plot: JavaPlot) = {
    val terminal = plot.getTerminal()
    terminal match {
      case t: ImageTerminal => getImageAsString(t.getImage(), exportFormat)
      case t: SVGTerminal => t.getTextOutput()
      case _ => throw new BndRuntimeException(exportFormat + " format not recognized.")
    }
  }
}

trait Plotter[T] {

  object Defaults {
    val width = 1200
    val height = 800
    val pointSize = 2
  }

  protected def createPlot(
    title : String,
    xlabel : String,
    ylabel : String,
    zlabel : Option[String] = None
  ) = new JavaPlot() {
    set("xlabel", "'" + xlabel + "'")
    set("ylabel", "'" + ylabel + "'")
    if (zlabel.isDefined) {
      set("zlabel", "'" + zlabel.get + "'")
    }
    set("terminal wxt size", Defaults.width + "," + Defaults.height)
    setTitle(title)
  }

  private def exec(plot: JavaPlot): T = {
    plot.plot()
    createOutput(plot)
  }

  protected def createOutput(plot: JavaPlot): T

  def plotXYWithRanges(
    data: Traversable[Traversable[Double]],
    xRangeMin : Double,
    xRangeMax : Double,
    yRangeMin : Double,
    title : String = ""
  ) {
    val plot = createPlot(title, "x", "y")
    plot.set("xrange", "[" + xRangeMin + ":" + xRangeMax + "]")
    plot.set("yrange", "[" + yRangeMin + ":*]")
    addPlot(plot, data, pointStyle(), "x-y")
    exec(plot)
  }

  def plotXY(
    data: Traversable[Traversable[Double]],
    title: String = ""
  ) = {
    val plot = createPlot(title, "x", "y")
    addPlot(plot, data, pointStyle(), "x-y")
    exec(plot)
  }

  def plotXYDots(
    data: Traversable[Traversable[Double]],
    title: String = ""
  ) = {
    val plot = createPlot(title, "x", "y")
    addPlot(plot, data, dotStyle, "x-y")
    exec(plot)
  }

  def plotXYTuples[X: DoubleConvertible, Y: DoubleConvertible](
    data : Traversable[(X, Y)],
    title : String = ""
  ) = {
    val plot = createPlot(title, "x", "y")
    addPlot(plot, data.map{case (x,y) => List(x : Double, y : Double)}, pointStyle(), "x-y")
    exec(plot)
  }

  def plotXYZ(
    data : Traversable[Traversable[Double]],
    title : String = ""
  ) = {
    val plot = createPlot(title, "x", "y", Some("z"))
    plot.newGraph3D()
    addPlot(plot, data, pointStyle(), "x-y-z")
    exec(plot)
  }

  def plotSingleSeries[T: DoubleConvertible](
    data : Traversable[T],
    setting : SeriesPlotSetting
  ) = plotSeries(Seq(data), setting)

  def plotSeries[T: DoubleConvertible](
    lines: Traversable[Traversable[T]],
    setting: SeriesPlotSetting
  ) = {
    val initLines = if (setting.transposed) lines.transpose else lines
    val captions = setting.captions.getOrElse((1 to initLines.size).map(i => "time series " + i))
    val plot = createPlot(setting.title, setting.xLabel, setting.yLabel)

    handleRanges(plot, setting)

    val xAxes = setting.xAxes.getOrElse(lines.map(line => List.range(0, line.size).map(_.toDouble)))
    (xAxes.toSeq, initLines.toSeq, captions).zipped.foreach(
      addSeries(plot, _, _, _, setting.showLegend))
    exec(plot)
  }

  private def handleRanges(
    plot : JavaPlot,
    setting : PlotSetting
  ) {
    plot.set("xrange", "[ %1$s : %2$s]" format (setting.xRangeMin.getOrElse("*"), setting.xRangeMax.getOrElse("*")))
    plot.set("yrange", "[ %1$s : %2$s]" format (setting.yRangeMin.getOrElse("*"), setting.yRangeMax.getOrElse("*")))
  }

  private def addSeries[T : DoubleConvertible](
    plot: JavaPlot,
    xAxis: Traversable[Double],
    line: Traversable[T],
    caption: String,
    showCaption: Boolean
  ) {
    val xWithLine = List(xAxis, line.map(x => x:Double)).transpose
    var legendCaption = if (showCaption) caption else ""
    addPlot(plot, xWithLine, lineStyle, legendCaption)
  }

  def plotCounts(
    data: Traversable[Double],
    title: String = "",
    tickSize : Double = 0.01,
    showLegend : Boolean = true
  ) = {
    val xyCounts = compressSlots(tickSize, data, {(_, slot : Traversable[_]) => slot.size})
    plotXYTuples(xyCounts, title)
  }

  def plotFrequencies(
    data: Traversable[Double],
    title: String = "",
    tickSize: Double = 0.01,
    showLegend: Boolean = true
  ) = {
    val totalSize = data.size
    val xyFrequencies = compressSlots(tickSize, data, {(_, slot : Traversable[_]) => slot.size.toDouble / totalSize})
    plotXYTuples(xyFrequencies, title)
  }

  def plotMatrix(
    data : Traversable[Traversable[Double]],
    title : String = "",
    pointSize : Int = 10
  ) = {
    val xyData = for ((row,x) <- data.toSeq.zipWithIndex; (value,y) <- row.toSeq.zipWithIndex) yield List(x,y,value)
    val plot = createPlot(title, "'x'", "'y'")
    addPlot(plot, xyData, pointPaletteStyle(pointSize))
    exec(plot)
  }

  private def addPlot[T : DoubleConvertible](
    plot: JavaPlot,
    data: Traversable[Traversable[T]],
    plotStyle: PlotStyle,
    title: String = ""
  ) = {
    val s = new DataSetPlot(data.map(_.map(x => x:Double).toArray).toArray)
    s.setPlotStyle(plotStyle)
    s.setTitle(title)
    plot.addPlot(s)
  }

  private def pointStyle(pointSize : Int = 1, pointType : Int = 5) : PlotStyle = new PlotStyle() {
    setStyle(Style.POINTS)
    setPointType(pointType)
    setPointSize(pointSize)
  }

  val dotPaletteStyle : PlotStyle = new PlotStyle() {
    setStyle(Style.DOTS)
    set("linetype","palette")
  }

  private def pointPaletteStyle(pointSize : Int = 1) : PlotStyle = new PlotStyle() {
    setStyle(Style.POINTS)
    set("linetype","palette")
    setPointType(5)
    setPointSize(pointSize)
  }

  val dotStyle : PlotStyle = new PlotStyle() {
    setStyle(Style.DOTS)
  }

  val lineStyle : PlotStyle = new PlotStyle() {
    setStyle(Style.LINES)
    setLineWidth(2)
  }
}

object Plotter {
  def apply(exportFormat: String): Plotter[String] = new ExportPlotter(exportFormat)
  def apply(): Plotter[Unit] = new DisplayPlotter()
}