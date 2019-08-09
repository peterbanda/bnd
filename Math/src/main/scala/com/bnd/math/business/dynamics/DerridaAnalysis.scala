package com.bnd.math.business.dynamics

import java.{util => ju}

import scala.collection.JavaConversions._
import com.bnd.core.CollectionElementsConversions._

import scala.math.Numeric._
import scala.math.Integral.Implicits._
import scala.math._
import com.bnd.core.DoubleConvertible.Implicits
import com.bnd.core._

import scala.util.Random
import com.bnd.core.runnable.StateAccessible
import com.bnd.core.runnable.TimeRunnable
import com.bnd.core.util.RandomUtil

class DerridaAnalysis[T : Integral](
    perturbationStrength : T,
    val vectorSpace : VectorSpace[T],
    val random : (T, T) => T) extends PerturbationAnalysis[T](perturbationStrength){

    def run(
        runnable : TimeRunnable with StateAccessible[T], 
        point : Seq[T], 
        timeStepLength : Double, 
        repetitions : Int) : Iterable[Iterable[T]] = {

        val num = implicitly[Integral[T]]
        val maxPoint = perturbate(point, Seq.fill(point.size){num.one})

        analyze(runnable, point, maxPoint, timeStepLength, repetitions)
    }

    def analyze(
        runnable : TimeRunnable with StateAccessible[T],
        minPoint : Seq[T],
        maxPoint : Seq[T],
        timeStepLength : Double,
        repetitions : Int) : Iterable[Iterable[T]] = {

        val num = implicitly[Integral[T]]

        // function to set states, run, and getStates
        def run(states : Seq[T]) : Seq[T] = {
            synchronized {
            	runnable.setStates(states)
            	runnable.runFor(timeStepLength)
            	runnable.getStates()
            }
        }

        val dims = minPoint.size
        var maxDistance = calcDistance(minPoint, maxPoint)
        val distances = for (i <- 1 to repetitions) yield {
            val distance = random(num.zero, maxDistance)
            val firstPoint = minPoint
            val secondVector = vectorSpace.createRandomVectorFromNorm(distance, dims).toSeq
            val secondPoint = (firstPoint, secondVector).zipped.map(_+_)            
//            val firstPoint = (minPoint, maxPoint).zipped.map(random)
//            val secondPoint = (minPoint, maxPoint).zipped.map(random)
            val distance1 = calcDistance(firstPoint, secondPoint)
            val distance2 = calcDistance(run(firstPoint), run(secondPoint))
//            if ((distance1 - maxDistance).signum() > 0) {
//                maxDistance = distance1
//            }
            List(distance1, distance2)
        }
        // normalize
        val normalizedDistances = distances.view map (_.view map (_/maxDistance))
//        normalizedDistances map (list => List(list(0), list(1) / list(0)))
        normalizedDistances
    }

    private def calcDistance(firstPoint : Iterable[T], secondPoint : Iterable[T]) : T = {
    	vectorSpace.calcNorm(diff(firstPoint, secondPoint))
    }
}