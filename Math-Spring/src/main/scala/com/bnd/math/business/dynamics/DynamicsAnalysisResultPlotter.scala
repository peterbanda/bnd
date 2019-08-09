package com.bnd.math.business.dynamics

import java.{lang => jl}

import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible
import com.bnd.math.business.MathUtil
import com.bnd.math.domain.dynamics.SingleRunAnalysisResultType
import com.bnd.math.domain.{Stats, StatsType}
import com.bnd.plotter.{Plotter, SeriesPlotSetting}

class DynamicsAnalysisResultPlotter(private val plotter: Plotter[Unit]) {

	//    private val displayMatrix = plotter.plotMatrix(
	//            _ : Iterable[Iterable[Double]],_ : String)()
	//
	//    private val displayTimeSeries = plotter.plotTimeSeries(
	//            _ : Iterable[Iterable[Double]],_ : String, true)
	//
	//    private val displayTimeSeriesTransposed = plotter.plotTimeSeriesTransposed(
	//            _ : Iterable[Iterable[Double]],_ : String, false)
	//
	//    private val displaySingleTimeSeries = plotter.plotSingleTimeSeries(
	//            _ : Iterable[Double],_ : String, true)
	//
	//    private val displayXYWithRanges = plotter.plotXYWithRanges(
	//            _ : Iterable[Iterable[Double]], _ : Double, _ : Double, _ : Double, _ : String)

	def plotBasic(results : SingleRunBasicResults, title : String) {
		plotter.plotMatrix(results.spatialCorrelationMatrix, "Spatial Correlations: " + title)

		plotter.plotMatrix(results.timeCorrelationMatrix, "Time Correlations:  " + title)

		plotter.plotSeries(
      results.stationaryPointsPerTime,
      new SeriesPlotSetting().setTitle("Stationary Points Per Time: " + title)
    )

		val globalFixedPointCount = results.fixedPointDetectedFlags.transpose.map(x  => (x : Iterable[Double]).sum)

		plotter.plotSingleSeries(
			globalFixedPointCount,
			new SeriesPlotSetting().setTitle("Fixed Points Count: " + title)
		)

		plotter.plotSeries(
      results.cumulativeDiffPerTime,
      new SeriesPlotSetting().setTitle("Cumulative Diff Per Time: " + title)
    )

		plotter.plotSeries(
      results.nonlinearityErrors,
      new SeriesPlotSetting().setTitle("Nonlinearity Errors: " + title)
    )

		plotter.plotSingleSeries(
			results.unboundDetectedFlags,
			new SeriesPlotSetting().setTitle("Unbound Detected: " + title)
		)
	}

	def plotFull[T : DoubleConvertible](results : FullResults[T], title : String) {
		plotBasic(results, title)

		plotter.plotSeries(
      results.lyapunovExponents,
      new SeriesPlotSetting()
        .setTitle("Lyapunov Exponents: " + title)
        .setShowLegend(false)
        .setTransposed(true)
    )

		plotter.plotXYWithRanges(results.derridaResults, 0D, 1D, 0D, "Derrida: " + title)
	}

	def plotStats[T](results : StatsResults, title : String) {
		def plotMean(stats : Iterable[Stats], title : String) = plotStats(stats, _.getMean(), title)
		def plotMax(stats : Iterable[Stats], title : String) = plotStats(stats, _.getMax(), title)

		plotMean(results.spatialCorrelations, "Spatial Correlations (Mean): " + title)
		plotMax(results.spatialCorrelations, "Spatial Correlations (Max): " + title)
		plotMean(results.timeCorrelations, "Time Correlations (Mean): " + title)
		plotMax(results.timeCorrelations, "Time Correlations (Max): " + title)
		plotter.plotSingleSeries(
			results.neighborTimeCorrelations,
			new SeriesPlotSetting().setTitle("Neighbor Time Correlations: " + title)
		)
		plotMean(results.spatialStationaryPointsPerTime, "Spatial Stationary Points Per Time (Mean): " + title)
		plotMean(results.timeStationaryPointsPerTime, "Time Stationary Points Per Time (Mean): " + title)
		plotMean(results.spatialCumulativeDiffPerTime, "Spatial Cumulative Diff Per Time (Mean): " + title)
		plotMean(results.timeCumulativeDiffPerTime, "Time Cumulative Diff Per Time (Mean): " + title)
		plotMean(results.spatialNonlinearityErrors, "Spatial Nonlinearity Errors (Mean): " + title)
		plotMean(results.timeNonlinearityErrors, "Time Nonlineariry Errors (Mean): " + title)

		plotter.plotSingleSeries(
			results.finalFixedPointsDetected,
			new SeriesPlotSetting().setTitle("Final Fixed Points: " + title)
		)
		plotter.plotSingleSeries(
			results.meanFixedPointsDetected,
			new SeriesPlotSetting().setTitle("Mean Fixed Points: " + title)
		)

		plotter.plotSingleSeries(
			results.unboundValuesDetected,
			new SeriesPlotSetting().setTitle("Unbound Detected: " + title)
		)

		plotter.plotSingleSeries(
			results.finalLyapunovExponents,
			new SeriesPlotSetting().setTitle("Final Lyapunov Exponents: " + title)
		)

		plotMean(results.derridaResults, "Derrida Results (Mean): " + title)
	}

	def plotStats(
		stats : Iterable[Stats],
		proj : Stats => jl.Double,
		title : String
	) {
		val xyMeanData = stats.map(s => List(s.getPos, proj(s)))
		plotter.plotXY(xyMeanData, title)
	}
}

object DynamicsAnalysisResultPlotter extends DynamicsAnalysisResultPlotter(Plotter())

class JavaStatsPlotter(private val plotter: Plotter[Unit]) {

	def plotStats(proj : Stats => jl.Double)(
		stats : jl.Iterable[Stats],
		title : String
	) {
		val xyData = stats.map(s => List(s.getPos, proj(s)))
		plotter.plotXY(xyData, title)
	}

	def plotStatsForType(statsType : StatsType) = plotStats(MathUtil.projStats(statsType))(_,_)

	private def toOption(value : jl.Double) : Option[Double] =
		if (value == null) None else Option(value : Double)

	private def toOption[T](value : jl.Iterable[T]) : Option[Iterable[T]] =
		if (value == null) None else Option(value : Iterable[T])

	def plotStats(
		stats : jl.Iterable[Stats],
		title : String,
		xlabel : String,
		ylabel : String,
		yRangeMax : jl.Double
	) {
		val data = stats.view.map(s => List(s.getMean, s.getMin, s.getMax))
		val xAxis = stats.view.map(_.getPos: Double)
		plotter.plotSeries(
      data.transpose,
      new SeriesPlotSetting()
        .setTitle(title)
        .setXAxis(xAxis)
        .setXLabel(xlabel)
        .setYLabel(ylabel)
        .setCaptions(List("Mean", "Min", "Max"))
        .setYRangeMax(yRangeMax)
        .setShowLegend(true)
    )
	}

	def plotStats(
		multiStats : jl.Iterable[_ <: jl.Iterable[Stats]],
		statsType : StatsType,
		title : String,
		xlabel : String,
		ylabel : String,
		yRangeMax : jl.Double,
		captions : jl.Iterable[String]
	) {
		val scalMultiStats : Iterable[Iterable[Stats]] = multiStats
		val data = scalMultiStats.view.map(_.view.map(MathUtil.projStats(statsType)))
		val xAxis = scalMultiStats.head.view.map(_.getPos: Double)
		plotter.plotSeries(
      data,
      new SeriesPlotSetting()
        .setTitle(title)
        .setXAxis(xAxis)
        .setXLabel(xlabel)
        .setYLabel(ylabel)
        .setCaptions(captions)
        .setYRangeMax(yRangeMax)
        .setShowLegend(captions != null)
    )
  }

	def getXLabel(singleRunAnalysisResultType : SingleRunAnalysisResultType) : String =
		singleRunAnalysisResultType match {
			case SingleRunAnalysisResultType.DerridaResults => "perturbation strength"

			case SingleRunAnalysisResultType.SpatialCorrelations |
					 SingleRunAnalysisResultType.SpatialCumulativeDiffPerTime |
					 SingleRunAnalysisResultType.SpatialStationaryPointsPerTime |
					 SingleRunAnalysisResultType.SpatialNonlinearityErrors |
					 SingleRunAnalysisResultType.FinalLyapunovExponents => "species"

			case SingleRunAnalysisResultType.TimeCorrelations |
					 SingleRunAnalysisResultType.TimeCumulativeDiffPerTime |
					 SingleRunAnalysisResultType.TimeStationaryPointsPerTime |
					 SingleRunAnalysisResultType.TimeNonlinearityErrors |
					 SingleRunAnalysisResultType.MeanFixedPointsDetected |
					 SingleRunAnalysisResultType.NeighborTimeCorrelations => "time"

			case _ => "?"
		}
}