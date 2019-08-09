package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import scala.math.Ordering.Implicits._

class UnboundDetectionProcessor[T: Ordering](
    val lowerBound: T,
    val upperBound: T) extends SingleRunProcessor[T, Iterable[Boolean]] {

    override def isTransposedStatesHistoryExpected = true

    override def process(
        stateHistories: Iterable[Iterable[T]],
        timeStepLength: Double
    ): Iterable[Boolean] = {
        val lastTimeStep = stateHistories.head.size - 1
        val minimaAndMaximaWithIndex = for (stateHistory <- stateHistories) yield
                (stateHistory.zipWithIndex.minBy(_._1),
                 stateHistory.zipWithIndex.maxBy(_._1))

        minimaAndMaximaWithIndex.view.map {
            case ((min, minIndex), (max, maxIndex)) =>
                min <= lowerBound ||
                max >= upperBound ||
                minIndex == lastTimeStep ||
                maxIndex == lastTimeStep}.force
    }
}