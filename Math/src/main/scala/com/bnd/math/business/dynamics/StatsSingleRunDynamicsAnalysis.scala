package com.bnd.math.business.dynamics

import java.util.Date

import scala.Array._
import scala.collection.JavaConversions._
import scala.math.Integral.Implicits._
import scala.math.Numeric._
import scala.util.Random
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible
import com.bnd.core.DoubleConvertible.Implicits._
import com.bnd.math.domain.dynamics.SingleRunAnalysisResult
import com.bnd.math.domain.dynamics.SingleRunAnalysisSpec
import com.bnd.math.domain.Stats
import com.bnd.math.domain.StatsSequence
import com.bnd.math.business.MathUtil._
import com.bnd.core.runnable.StateAccessible
import com.bnd.core.XYExtractorUtil._
import java.{lang => jl}
import java.{util => ju}

import com.bnd.core.runnable.TimeRunnable

class StatsSingleRunDynamicsAnalysis[T: Integral: DoubleConvertible: Manifest](val spec: StatsDynamicsAnalysisSpec[T]) {

    val fullAnalysis = new FullSingleRunDynamicsAnalysis[T](spec.fullSpec)
    val num = implicitly[Integral[T]]
    def reduceList[T] = take[T](spec.fullSpec.timeStepToFilter)_

    def run(runnable: TimeRunnable with StateAccessible[T], initialState: Seq[T]): StatsResults = {
        val fullResults = fullAnalysis.run(runnable, initialState)

        val neighborTimeCorrelations = fullResults.timeCorrelationMatrix.zipWithIndex.dropRight(1).map{
            case (correlations,index) => correlations.toSeq(index + 1)}

        val derridaStats = createDerridaStats(fullResults.derridaResults)

        val meanFixedPoints = reduceList(fullResults.fixedPointDetectedFlags.transpose).map(calcMean(_))

        new StatsResults(
        	createStats(fullResults.spatialCorrelationMatrix, true),
            createStats(fullResults.timeCorrelationMatrix, spec.fullSpec.timeStepToFilter, true),
            neighborTimeCorrelations,
            createStats(fullResults.stationaryPointsPerTime),
            createReducedStats(fullResults.stationaryPointsPerTime.transpose),
            createStats(fullResults.cumulativeDiffPerTime),
            createReducedStats(fullResults.cumulativeDiffPerTime.transpose),
            createStats(fullResults.nonlinearityErrors),
            createReducedStats(fullResults.nonlinearityErrors.transpose),
            fullResults.fixedPointDetectedFlags.map(_.last),
            meanFixedPoints,
            fullResults.unboundDetectedFlags,
            fullResults.lyapunovExponents.last,
            derridaStats)
    }

    def runDerridaOnly(runnable: TimeRunnable with StateAccessible[T], initialState: Seq[T]): Iterable[Stats] = {
        val fullDerridaResults = fullAnalysis.runDerridaOnly(runnable, initialState)
        createDerridaStats(fullDerridaResults)
	}

    private def createDerridaStats(fullDerridaResults : Iterable[Iterable[T]]) : Iterable[Stats] = {
        val XYDerridaStats = compressXYIterableSlots(
        		spec.derridaResolution,
        		fullDerridaResults,
        		calcStats(_, _ : Traversable[T]),
        		Option(num.zero),
        		Option(num.one))

        XYDerridaStats.map(_._2)
    }

    def createStats[T: DoubleConvertible](
        values: Iterable[Iterable[T]],
        increment : Double = 1,
        dropDiagional : Boolean = false
    ): Iterable[Stats] = values.zipWithIndex.map{
        	case (values, index) => {
        	    val newValues = if (dropDiagional) dropIndex(values.toList, index) else values
        	    calcStats(index * increment, newValues)
        	}
        }

    def createReducedStats[T: DoubleConvertible](
        values: Iterable[Iterable[T]]
    ): Iterable[Stats] = (reduceList(values)).zipWithIndex.map{
        	case (values, index) => calcStats(index * spec.fullSpec.timeStepToFilter, values) }
}

class StatsResults(
    val spatialCorrelations: Iterable[Stats],
    val timeCorrelations: Iterable[Stats],
    val neighborTimeCorrelations : Iterable[Double],
    val spatialStationaryPointsPerTime: Iterable[Stats],
    val timeStationaryPointsPerTime: Iterable[Stats],
    val spatialCumulativeDiffPerTime: Iterable[Stats],
    val timeCumulativeDiffPerTime: Iterable[Stats],
    val spatialNonlinearityErrors: Iterable[Stats],
    val timeNonlinearityErrors: Iterable[Stats],

    val finalFixedPointsDetected: Iterable[Boolean],
    val meanFixedPointsDetected: Iterable[Double],
    val unboundValuesDetected: Iterable[Boolean],
    val finalLyapunovExponents: Iterable[Double],
    val derridaResults: Iterable[Stats])

object StatsSingleRunDynamicsAnalysisFactory {

    // TODO: this is supposed to be provided automatically
    implicit val doubleAsIntegral = DoubleAsIfIntegral
    val euclideanVectorSpace = new EuclideanVectorSpace[Double]

    def createDoubleEuclideanInstance(
        timeStepLength: Double,
        iterations: Int,
        lyapunovPerturbationStrength: Double,
        derridaPerturbationStrength: Double,
        derridaTimeLength: Double,
        timeStepToFilter: Int,
        fixedPointDetectionPrecision: Double,
        stationaryPointDetectionPrecision: Double,
        derridaResolution : Double): StatsSingleRunDynamicsAnalysis[Double] = {

        new StatsSingleRunDynamicsAnalysis[Double](new StatsDynamicsAnalysisSpec[Double](
            new FullDynamicsAnalysisSpec[Double](
            	timeStepLength,
            	iterations,
            	lyapunovPerturbationStrength,
            	derridaPerturbationStrength,
            	derridaTimeLength,
            	euclideanVectorSpace,
            	(min, max) => min + (max - min) * Random.nextDouble(),
            	timeStepToFilter,
            	fixedPointDetectionPrecision,
            	Double.NegativeInfinity,
            	Double.PositiveInfinity,
            	stationaryPointDetectionPrecision),
            derridaResolution))
    }

    def createJavaDoubleEuclideanInstance(spec : SingleRunAnalysisSpec): StatsSingleRunDynamicsAnalysis[Double] = {

        createDoubleEuclideanInstance(
        	spec.getTimeStepLength(),
        	spec.getIterations(),
        	spec.getLyapunovPerturbationStrength(),
        	spec.getDerridaPerturbationStrength(),
        	spec.getDerridaTimeLength(),
        	spec.getTimeStepToFilter(),
        	spec.getFixedPointDetectionPrecision(),
        	spec.getFixedPointDetectionPrecision(),             // using same detection precision for fixed and stationary points
        	spec.getDerridaResolution())
    }
}

class JavaDoubleStatsSingleRunDynamicsAnalysis(spec : SingleRunAnalysisSpec) {

	implicit def toStatsSeq(stats : Iterable[Stats]) = new StatsSequence {
		setStats(stats)
   	}

    val proxiedAnalysis = StatsSingleRunDynamicsAnalysisFactory.createJavaDoubleEuclideanInstance(spec)

	def run(
		runnable: TimeRunnable with StateAccessible[jl.Double],
		initialState: ju.List[jl.Double]
	): SingleRunAnalysisResult = {
		val scalaDoubleRunnable = TimeRunnableDoubleAdapter(runnable)
		val seq = initialState : Seq[jl.Double]
		val result = proxiedAnalysis.run(scalaDoubleRunnable, seq)

		new SingleRunAnalysisResult {
		    setTimeCreated(new Date())
		    setInitialState(initialState)
			setSpatialCorrelations(result.spatialCorrelations)
			setTimeCorrelations(result.timeCorrelations)
			setNeighborTimeCorrelations(result.neighborTimeCorrelations.toSeq : Seq[jl.Double])
			setSpatialStationaryPointsPerTime(result.spatialStationaryPointsPerTime)
			setTimeStationaryPointsPerTime(result.timeStationaryPointsPerTime)
			setSpatialCumulativeDiffPerTime(result.spatialCumulativeDiffPerTime)
			setTimeCumulativeDiffPerTime(result.timeCumulativeDiffPerTime)
			setSpatialNonlinearityErrors(result.spatialNonlinearityErrors)
			setTimeNonlinearityErrors(result.timeNonlinearityErrors)

			setFinalFixedPointsDetected(result.finalFixedPointsDetected.toSeq : Seq[jl.Boolean])
			setMeanFixedPointsDetected(result.meanFixedPointsDetected.toSeq : Seq[jl.Double])
			setUnboundValuesDetected(result.unboundValuesDetected.toSeq : Seq[jl.Boolean])
			setFinalLyapunovExponents(result.finalLyapunovExponents.toSeq : Seq[jl.Double])
			setDerridaResults(result.derridaResults)
		}
	}

    def runDerridaOnly(
		runnable: TimeRunnable with StateAccessible[jl.Double],
		initialState: ju.List[jl.Double]
	): StatsSequence = {
		val scalaDoubleRunnable = TimeRunnableDoubleAdapter(runnable)
		val seq = initialState : Seq[jl.Double]
		proxiedAnalysis.runDerridaOnly(scalaDoubleRunnable, seq)
	}
}