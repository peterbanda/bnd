package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import java.{lang => jl}
import java.{lang => jl}

import DynamicsAnalysisResultHelper._
import com.bnd.math.domain.dynamics._
import com.bnd.math.domain.Stats
import com.bnd.math.business.MathUtil
import com.bnd.math.domain.StatsSequence
import com.bnd.math.domain.StatsType
import com.bnd.core.BndRuntimeException

class DynamicsAnalysisResultProcessor(private val statsType: StatsType) {

    private val projStats = MathUtil.projStats(statsType)_

    def calcStatsFromSingleRuns(
        selection: SingleRunAnalysisResult => Iterable[jl.Double])(
        singleRunAnalysisResults: jl.Iterable[SingleRunAnalysisResult]
    ): jl.Iterable[Stats] = {
        val values = singleRunAnalysisResults.view.map(selection(_))
        MathUtil.calcStatsBySecond(values: Iterable[Iterable[Double]]): jl.Iterable[Stats]
    }

    def calcStatsFromSingleRuns(
        seqSelection: SingleRunAnalysisResult => StatsSequence,
        statAttSelection: Stats => jl.Double)(
        singleRunAnalysisResults: jl.Iterable[SingleRunAnalysisResult]
    ): jl.Iterable[Stats] =
        if (singleRunAnalysisResults.isEmpty) {
        	List[Stats]()
        } else {
        	val values = singleRunAnalysisResults.view.map {
        		s => seqSelection(s).getStats().view.map(statAttSelection(_))
        	}
        	val firstResult = singleRunAnalysisResults.head
        	val indeces = getIndeces(seqSelection(firstResult))
        	MathUtil.calcStatsBySecond(values: Iterable[Iterable[Double]], indeces)
        }

    def calcStatsFromMultiRuns[C[X] <: jl.Iterable[X]](
        selection: SingleRunAnalysisResult => Iterable[jl.Double])(
        multiRunAnalysisResults: C[_ <: MultiRunAnalysisResult[jl.Double]]
    ) = calcStatsFromSingleRuns(selection)(collectSingleRunResults(multiRunAnalysisResults))

    def calcStatsFromMultiRuns[C[X] <: jl.Iterable[X]](
        seqSelection: SingleRunAnalysisResult => StatsSequence,
        statAttSelection: Stats => jl.Double)(
        multiRunAnalysisResults: C[_ <: MultiRunAnalysisResult[jl.Double]]
    ) = calcStatsFromSingleRuns(seqSelection, statAttSelection)(collectSingleRunResults(multiRunAnalysisResults))

    private def collectSingleRunResults(ms : Iterable[_ <: MultiRunAnalysisResult[jl.Double]]) = ms.foldLeft(
            Seq[SingleRunAnalysisResult]()){(list, m) => m.getSingleRunResults().toList ++ list}

    def calcStatsForTypeSingle(at: SingleRunAnalysisResultType) =
        if (at.holdsStats()) 
        	calcStatsFromSingleRuns(projSingleAnalysisResult1(at), projStats)_
        else
            calcStatsFromSingleRuns(projSingleAnalysisResult2(at))_ 

    def calcStatsForTypeMulti(at: SingleRunAnalysisResultType) =
        if (at.holdsStats()) 
        	calcStatsFromMultiRuns(projSingleAnalysisResult1(at), projStats)_
        else
            calcStatsFromMultiRuns(projSingleAnalysisResult2(at))_ 

    private def getIndeces(statsSequence: StatsSequence) = statsSequence.getStats().view.map(_.getPos)
}

object DynamicsAnalysisResultHelper {
    def projSingleAnalysisResult1(at: SingleRunAnalysisResultType)(result : SingleRunAnalysisResult) = 
        at match {
            case SingleRunAnalysisResultType.SpatialCorrelations => result.getSpatialCorrelations()
            case SingleRunAnalysisResultType.TimeCorrelations => result.getTimeCorrelations()
            case SingleRunAnalysisResultType.SpatialStationaryPointsPerTime => result.getSpatialStationaryPointsPerTime()
            case SingleRunAnalysisResultType.TimeStationaryPointsPerTime => result.getTimeStationaryPointsPerTime()
            case SingleRunAnalysisResultType.SpatialCumulativeDiffPerTime => result.getSpatialCumulativeDiffPerTime()
            case SingleRunAnalysisResultType.TimeCumulativeDiffPerTime => result.getTimeCumulativeDiffPerTime()
            case SingleRunAnalysisResultType.SpatialNonlinearityErrors => result.getSpatialNonlinearityErrors()
            case SingleRunAnalysisResultType.TimeNonlinearityErrors => result.getTimeNonlinearityErrors()
            case SingleRunAnalysisResultType.DerridaResults => result.getDerridaResults()
            case _ => throw new BndRuntimeException(at + " not recognized")
        }

    def projSingleAnalysisResult2(at: SingleRunAnalysisResultType)(result : SingleRunAnalysisResult) = 
        at match {
            case SingleRunAnalysisResultType.FinalLyapunovExponents => result.getFinalLyapunovExponents()
            case SingleRunAnalysisResultType.MeanFixedPointsDetected => result.getMeanFixedPointsDetected()
            case SingleRunAnalysisResultType.NeighborTimeCorrelations => result.getNeighborTimeCorrelations()
//            case SingleRunAnalysisResultType.FinalFixedPointsDetected => result.getFinalFixedPointsDetected()
//            case SingleRunAnalysisResultType.UnboundValuesDetected => result.getUnboundValuesDetected()
            case _ => throw new BndRuntimeException(at + " not recognized")
        }
}