package com.bnd.math.business.dynamics

import scala.math._
import com.bnd.core.DoubleConvertible

class SingleRunBasicAnalysisSuite[T: DoubleConvertible: Ordering](
    val timeCorrelationStepToFilter: Int,
    val fixedPointDetectionPrecision : Double,
    val stationaryPointDetectionPrecision : Double,
    val lowerBound: T,
    val upperBound: T) extends SingleRunProcessorSuite[T, SingleRunBasicResults](

    List(new SpatialCorrelationProcessor[T],
        new TimeCorrelationProcessor[T](timeCorrelationStepToFilter),
        new StationaryPointPerTimeProcessor[T](stationaryPointDetectionPrecision),
        new CumulativeDiffPerTimeProcessor[T],
        new NonlinearityProcessor[T](true),
        new FixedPointDetectionProcessor[T](fixedPointDetectionPrecision),
        new UnboundDetectionProcessor[T](lowerBound, upperBound))) {

    override protected def createResult(processorResults: Iterable[_]): SingleRunBasicResults = {
        val procResultsIterator = processorResults.iterator
        def nextResultAs[O]: O = procResultsIterator.next().asInstanceOf[O]

        SingleRunBasicResults(
            nextResultAs[Array[Array[Double]]],
            nextResultAs[Array[Array[Double]]],
            nextResultAs[Iterable[Iterable[Double]]],
            nextResultAs[Iterable[Iterable[Double]]],
            nextResultAs[Iterable[Iterable[Double]]],
            nextResultAs[Iterable[Iterable[Boolean]]],
            nextResultAs[Iterable[Boolean]]
        )
    }
}

case class SingleRunBasicResults(
    spatialCorrelationMatrix: Array[Array[Double]],
    timeCorrelationMatrix: Array[Array[Double]],
    stationaryPointsPerTime: Iterable[Iterable[Double]],
    cumulativeDiffPerTime: Iterable[Iterable[Double]],
    nonlinearityErrors: Iterable[Iterable[Double]],
    fixedPointDetectedFlags: Iterable[Iterable[Boolean]],
    unboundDetectedFlags: Iterable[Boolean]
)