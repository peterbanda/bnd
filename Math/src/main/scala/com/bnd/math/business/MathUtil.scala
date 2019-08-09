package com.bnd.math.business

import com.bnd.core.CollectionElementsConversions._
import scala.math.Integral.Implicits._
import com.bnd.core.DoubleConvertible.Implicits._
import scala.collection.mutable.ListBuffer
import java.{lang => jl}
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import scala.collection.JavaConverters._
import com.bnd.core.DoubleConvertible
import com.bnd.core.XYExtractable
import com.bnd.math.domain.dynamics._
import com.bnd.math.domain.Stats
import com.bnd.math.domain.StatsSequence
import com.bnd.math.domain.StatsType

object MathUtil {

    def calcMean[T : DoubleConvertible](
        data : Iterable[T]
    ) : Double = (data : Iterable[Double]).sum / data.size

    def calcMeanNumeric[T : Numeric](
        data : Iterable[T]
    ) : Double = {
        val num = implicitly[Numeric[T]]
        num.toDouble(data.sum) / data.size
    }

    def calcStats[T: DoubleConvertible](
        index: Double, 
        values: Traversable[T]
    ): Stats = {
        val nanFilteredValues = values.filterNot(value => (value : Double).isNaN)
        val stats = new DescriptiveStatistics(nanFilteredValues.map(x => x: Double).toSeq)
        new Stats() {
            setMean(stats.getMean())
            setStandardDeviation(stats.getStandardDeviation())
            setMin(stats.getMin())
            setMax(stats.getMax())
            setPos(index)
        }
    }

    def calcStats[T: DoubleConvertible](f: () => T)(repetitions : Int) : Stats = calcStats(0, (1 to repetitions).map(_=> f()))

    def calcStatsByFirst[T: DoubleConvertible](
        values: Iterable[Iterable[T]]
    ): Iterable[Stats] = values.view.zipWithIndex.map{
        	case (values, index) => calcStats(index, values)
        }

    def calcStatsByFirst[T: DoubleConvertible](
        values: Iterable[Iterable[T]],
        indeces : Iterable[Double]
    ): Iterable[Stats] = (values, indeces).zipped.map{
        	case (values, index) => calcStats(index, values)
        }

    def calcStatsBySecond[T: DoubleConvertible](
        values: Iterable[Iterable[T]]
    ): Iterable[Stats] = calcStatsByFirst(values.transpose)

    def calcStatsBySecond[T: DoubleConvertible](
        values: Iterable[Iterable[T]],
        indeces : Iterable[Double]
    ): Iterable[Stats] = calcStatsByFirst(values.transpose, indeces)

    def calcStats(
        index: Double, 
        values: Iterable[Stats],
        selection : Stats => Double
    ): Stats = calcStats(index, values.map(s => selection(s)))

    def calcStats[T: DoubleConvertible]( 
        multiRunAnalysisResult: MultiRunAnalysisResult[T],
        selection : SingleRunAnalysisResult => Iterable[T]
    ): Iterable[Stats] = {
        val values = multiRunAnalysisResult.getSingleRunResults().map(selection(_))
        calcStatsBySecond(values) 
    }

    def projStats(statsType : StatsType)(stats : Stats) = 
        statsType match {
            case StatsType.Mean => stats.getMean()
            case StatsType.Max => stats.getMax()
            case StatsType.Min => stats.getMin()
            case StatsType.StandardDeviation => stats.getStandardDeviation()
        }
}

object JavaMathUtil {
    def calcMean(data: jl.Iterable[jl.Double]): jl.Double = MathUtil.calcMean(data)

    def calcStats(
        index: jl.Double,
        values: jl.Iterable[jl.Double]) = MathUtil.calcStats(index, values)

    def calcStatsMean(
        index: jl.Double,
        values: jl.Iterable[Stats]) = MathUtil.calcStats(index, values, _.getMean())

    def calcStatsMean(
        index: jl.Double,
        seq: StatsSequence) = MathUtil.calcStats(index, seq.getStats(), _.getMean())

    def calcStatsByFirst(
        values: jl.Iterable[_ <: jl.Iterable[jl.Double]]): jl.Iterable[Stats] = MathUtil.calcStatsByFirst(values)

    def calcStatsByFirst(
        values: jl.Iterable[_ <: jl.Iterable[jl.Double]],
        indeces: jl.Iterable[jl.Double]): jl.Iterable[Stats] = MathUtil.calcStatsByFirst(values, indeces: Iterable[jl.Double])

    def calcStatsBySecond(
        values: jl.Iterable[_ <: jl.Iterable[jl.Double]]): jl.Iterable[Stats] = MathUtil.calcStatsBySecond(values)

    def calcStatsBySecond(
        values: jl.Iterable[_ <: jl.Iterable[jl.Double]],
        indeces: jl.Iterable[jl.Double]): jl.Iterable[Stats] = MathUtil.calcStatsBySecond(values, indeces: Iterable[jl.Double])

    def calcStatsByFirst(
        statsType: StatsType)(
            values: jl.Iterable[_ <: jl.Iterable[Stats]]): jl.Iterable[Stats] = MathUtil.calcStatsByFirst(projMatrixStats(statsType)(values))

    def calcStatsByFirst(
        statsType: StatsType)(
            values: jl.Iterable[_ <: jl.Iterable[Stats]],
            indeces: jl.Iterable[jl.Double]): jl.Iterable[Stats] = MathUtil.calcStatsByFirst(projMatrixStats(statsType)(values), indeces: Iterable[jl.Double])

    def calcStatsBySecond(
        statsType: StatsType)(
            values: jl.Iterable[_ <: jl.Iterable[Stats]]): jl.Iterable[Stats] = MathUtil.calcStatsBySecond(projMatrixStats(statsType)(values))

    def calcStatsBySecond(
        statsType: StatsType)(
            values: jl.Iterable[_ <: jl.Iterable[Stats]],
            indeces: jl.Iterable[jl.Double]): jl.Iterable[Stats] = MathUtil.calcStatsBySecond(projMatrixStats(statsType)(values), indeces: Iterable[jl.Double])

    def projStats(statsType: StatsType)(values: jl.Iterable[Stats]): jl.Iterable[jl.Double] = values.map(MathUtil.projStats(statsType)): Iterable[jl.Double]

    private def projMatrixStats(statsType: StatsType)(values: Iterable[Iterable[Stats]]) = values.view.map(_.view.map(MathUtil.projStats(statsType)))
}