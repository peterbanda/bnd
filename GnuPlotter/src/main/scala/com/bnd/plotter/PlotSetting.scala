package com.bnd.plotter

class PlotSetting {
  private var _title: String = ""
  private var _xLabel: String = "time"
  private var _yLabel: String = "val"
  private var _captions: Option[Iterable[String]] = None
  private var _xRangeMin: Option[Double] = None
  private var _xRangeMax: Option[Double] = None
  private var _yRangeMin: Option[Double] = None
  private var _yRangeMax: Option[Double] = None
  private var _showLegend: Boolean = true

  def title = _title
  def setTitle(title : String): this.type = {
    _title = title; this
  }

  def xLabel = _xLabel
  def setXLabel(xLabel : String): this.type = {
    _xLabel = xLabel; this
  }

  def yLabel = _yLabel
  def setYLabel(yLabel : String): this.type = {
    _yLabel = yLabel; this
  }

  def captions = _captions
  def setCaptions(captions : Iterable[String]): this.type = {
    _captions = Some(captions); this
  }

  def xRangeMin = _xRangeMin
  def setXRangeMin(xRangeMin : Double): this.type = {
    _xRangeMin = Some(xRangeMin); this
  }

  def xRangeMax = _xRangeMax
  def setXRangeMax(xRangeMax : Double): this.type = {
    _xRangeMax = Some(xRangeMax); this
  }

  def yRangeMin = _yRangeMin
  def setYRangeMin(yRangeMin : Double): this.type = {
    _yRangeMin = Some(yRangeMin); this
  }

  def yRangeMax = _yRangeMax
  def setYRangeMax(yRangeMax : Double): this.type = {
    _yRangeMax = Some(yRangeMax); this
  }

  def showLegend = _showLegend
  def setShowLegend(showLegend : Boolean): this.type = {
    _showLegend = showLegend; this
  }
}

class SeriesPlotSetting extends PlotSetting {
  private var _xAxes : Option[Iterable[Iterable[Double]]] = None
  private var _transposed : Boolean = false

  def xAxes = _xAxes
  def setXAxes(xAxes : Iterable[Iterable[Double]]): this.type = {
    _xAxes = Some(xAxes); this
  }

  def setXAxis(xAxis: Iterable[Double]): this.type = {
    _xAxes = Some(Stream.continually(xAxis)); this
  }

  def transposed = _transposed
  def setTransposed(transposed : Boolean): this.type = {
    _transposed = transposed; this
  }
}