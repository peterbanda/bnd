package com.bnd.math.business.dynamics

import scala.Array._
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.collection._
import scala.math.Integral.Implicits._
import scala.math.Numeric._
import scala.math._
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.metrics.Metrics

object DynamicsMeasures {

//    def computeRank(matrix: Array[Array[Double]]) : Double = {
//        MatrixFeatures rank new DenseMatrix64F(matrix)
//    }

//correlation dimension ( D2 ) [20], Kolmogorov
//entropy [21], and Lyapunov characteristic exponents

	def calcLyapunovExponent[T](
	    firstTrajectory : Iterable[Seq[T]],
	    secondTrajectory : Iterable[Seq[T]],
	    metrics : (Seq[T], Seq[T]) => Double
	) : Double = {
        val diffs = (firstTrajectory, secondTrajectory).zipped.map(metrics(_,_))
        val sum = diffs.map(x => log (x / diffs.head)).sum
        sum / firstTrajectory.size
    }

	def calcLyapunovExponent[T](
	    firstTrajectory : Iterable[Seq[T]],
	    secondTrajectory : Iterable[Seq[T]],
	    metrics : Metrics[T]
	) : Double = {
	    calcLyapunovExponent(firstTrajectory, secondTrajectory, metrics.calcDistance(_:Seq[T],_:Seq[T]))
	}
}