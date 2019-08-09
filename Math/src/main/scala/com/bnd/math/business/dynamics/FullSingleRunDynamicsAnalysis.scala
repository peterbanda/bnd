package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._

import scala.Array._
import scala.math.Integral.Implicits._
import scala.math.Numeric._
import scala.util.Random
import com.bnd.core.DoubleConvertible
import java.{lang => jl, util => ju}

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import com.bnd.core.runnable.StateAccessible
import com.bnd.math.domain.Stats
import com.bnd.core.runnable.TimeRunnable

class FullSingleRunDynamicsAnalysis[T: Integral: DoubleConvertible: Manifest](val spec: FullDynamicsAnalysisSpec[T]) {

    val basicAnalysis = new SingleRunBasicAnalysisSuite[T](
        spec.timeStepToFilter, spec.fixedPointDetectionPrecision, spec.stationaryPointDetectionPrecision, spec.lowerBound, spec.upperBound)

    val lyapunovAnalysis = new LyapunovAnalysis[T](
        spec.lyapunovPerturbationStrength, spec.vectorSpace)

    val derridaAnalysis = new DerridaAnalysis[T](
        spec.derridaPerturbationStrength, spec.vectorSpace, spec.random)

    def run(runnable: TimeRunnable with StateAccessible[T], initialState: Seq[T]): FullResults[T] = {
        val basicAnalysisResults = basicAnalysis.run(runnable, initialState, spec.timeStepLength, spec.iterations)
        val lyapunovExponents = lyapunovAnalysis.calcSpectrum(runnable, initialState, spec.timeStepLength, spec.iterations)

        val derridaResults = derridaAnalysis.run(runnable, initialState, spec.derridaTimeLength, spec.iterations)

        new FullResults[T](
            basicAnalysisResults.spatialCorrelationMatrix,
            basicAnalysisResults.timeCorrelationMatrix,
            basicAnalysisResults.stationaryPointsPerTime,
            basicAnalysisResults.cumulativeDiffPerTime,
            basicAnalysisResults.nonlinearityErrors,
            basicAnalysisResults.fixedPointDetectedFlags,
            basicAnalysisResults.unboundDetectedFlags,
            lyapunovExponents,
            derridaResults)
    }

    def runDerridaOnly(runnable: TimeRunnable with StateAccessible[T], initialState: Seq[T]): Iterable[Iterable[T]] =
        derridaAnalysis.run(runnable, initialState, spec.derridaTimeLength, spec.iterations)        

    def createStats(index: Double, values: Iterable[Double]): Stats = {
        val stats = new DescriptiveStatistics(values: Array[Double])
        new Stats() {
            setMean(stats.getMean())
            setStandardDeviation(stats.getStandardDeviation())
            setMin(stats.getMin())
            setMax(stats.getMax())
            setPos(index)
        }
    }
}

class FullResults[T](
    spatialCorrelationMatrix: Array[Array[Double]],
    timeCorrelationMatrix: Array[Array[Double]],
    stationaryPointsPerTime: Iterable[Iterable[Double]],
    cumulativeDiffPerTime: Iterable[Iterable[Double]],
    nonlinearityErrors: Iterable[Iterable[Double]],
    fixedPointDetectedFlags: Iterable[Iterable[Boolean]],
    unboundDetectedFlags: Iterable[Boolean],
    val lyapunovExponents: Iterable[Iterable[T]],
    val derridaResults: Iterable[Iterable[T]]) 
    extends SingleRunBasicResults(
            spatialCorrelationMatrix,
            timeCorrelationMatrix,
            stationaryPointsPerTime,
            cumulativeDiffPerTime,
            nonlinearityErrors,
            fixedPointDetectedFlags,
            unboundDetectedFlags)

object FullSingleRunDynamicsAnalysisFactory {

    // TODO: this is supposed to be provided automatically
    implicit val doubleAsIntegral = DoubleAsIfIntegral
    val euclideanVectorSpace = new EuclideanVectorSpace[Double]

    def createDoubleEuclideanInstance(
        timeStepLength: Double,
        iterations: Int,
        lyapunovPerturbationStrength: Double,
        derridaPerturbationStrength: Double,
        derridaTimeLength : Double,
        timeStepToFilter: Int,
        fixedPointDetectionPrecision: Double,
        stationaryPointDetectionPrecision: Double): FullSingleRunDynamicsAnalysis[Double] = {

        new FullSingleRunDynamicsAnalysis[Double](new FullDynamicsAnalysisSpec[Double](
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
            stationaryPointDetectionPrecision))
    }
}