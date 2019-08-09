package com.bnd.math.business.dynamics

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.seqAsJavaList
import scala.math.Integral
import scala.math.Integral.Implicits.infixIntegralOps
import scala.math.log
import com.bnd.core.DoubleConvertible
import com.bnd.core.runnable.StateAccessible
import com.bnd.core.runnable.TimeRunnable

class LyapunovAnalysis[T : Integral : DoubleConvertible](
    perturbationStrength : T,
    val vectorSpace : VectorSpace[T]) extends PerturbationAnalysis[T](perturbationStrength){

    val num = implicitly[Integral[T]]
    val converter = implicitly[DoubleConvertible[T]]

    implicit def fromDouble(a : Double) : T = converter.fromDouble(a)
    def logNorm(vector : Iterable[T]) : T = log(vectorSpace.calcNorm(vector).toDouble())

    def calcSpectrum(
        runnable : TimeRunnable with StateAccessible[T], 
        initialPoint : Seq[T], 
        timeStepLength : Double, 
        iterations : Int) : Iterable[Iterable[T]] = {

        // function to set states, run, and get States
        def run(states : Seq[T]) : Seq[T] = {
            synchronized {
            	runnable.setStates(states)
            	runnable.runFor(timeStepLength)
            	runnable.getStates
            }
        }

        val dim = initialPoint.size
        var referencePoint = initialPoint
        var perturbVectors : Iterable[Seq[T]] = Seq.tabulate(dim, dim){(i,j) => if (i == j) num.one else num.zero}
        var normSums = Seq.fill(dim){num.zero}

        for (i <- 1 to iterations) yield {
            val newRererencePoint = run(referencePoint)
//            	val differences = (referencePoint,newRererencePoint).zipped.map{case (xOld, xNew) => num.abs(xOld - xNew)}
//            	val fixedPointx = num.equiv(differences.sum, num.zero)

            val newPerturbPoints = perturbate(referencePoint, perturbVectors).view map run
            val newPerturbVectors = diff(newRererencePoint, newPerturbPoints).view map (_ map (_ / perturbationStrength))
            val newOrthogonalPerturbVectors = vectorSpace.orthogonalizeVectors(newPerturbVectors)

            normSums = (normSums, newOrthogonalPerturbVectors).zipped.map((sum, vector) => sum + logNorm(vector))
            referencePoint = newRererencePoint
            perturbVectors = newOrthogonalPerturbVectors.view map vectorSpace.normalizeVector          

            normSums map { x =>
            	if (x.toDouble().isNegInfinity) x else x / (timeStepLength * i.toDouble) }
        }
//        val overallTime = iterations * timeStepLength
//        normSums map (_ / overallTime) 
    }

    def calcMaximal(
        runnable : TimeRunnable with StateAccessible[T], 
        initialPoint : Seq[T], 
        timeStepLength : Double, 
        iterations : Int,
        normalizationThreshold : T) : Iterable[T] = {

        // function to set states, run, and get States
        def run(states : Seq[T]) : Seq[T] = {
            synchronized {
            	runnable.setStates(states)
            	runnable.runFor(timeStepLength)
            	runnable.getStates()
            }
        }

        val dim = initialPoint.size
//        val length = Math.sqrt((perturbationStrength * perturbationStrength).toDouble / dim.toDouble) : T
        var referencePoint = initialPoint
        var perturbVector = vectorSpace.createRandomVectorFromNorm(perturbationStrength, dim).toSeq
//        println("Pertrub vector " + perturbVector)
//        var perturbVector = Seq.tabulate(dim){i => if (i == 0) Math.sqrt(perturbationStrength.toDouble) : T else num.zero}
//        var perturbVector = Seq.tabulate(dim){i => if (i == 1) perturbationStrength else num.zero}
//        var perturbVector = Seq.fill(dim)(length)
//        var perturbVector = Seq.fill(dim)(perturbationStrength)
        var normSum = num.zero

        val iterationsToSkip = 0

        for (i <- 1 to iterationsToSkip; if !normSum.toDouble.isNegInfinity) {
            val newRererencePoint = run(referencePoint)
            val newPerturbPoint = run(add(referencePoint, perturbVector))
            val newPerturbVector = diff(newPerturbPoint, newRererencePoint)
            val distance = vectorSpace.calcNorm(newPerturbVector)
            val alpha = distance / perturbationStrength
            perturbVector = newPerturbVector.view map (_ / alpha)                
            referencePoint = newRererencePoint                
        }

        for (i <- iterationsToSkip + 1 to iterations; if !normSum.toDouble.isNegInfinity) yield {
            val newRererencePoint = run(referencePoint)
            val newPerturbPoint = run(add(referencePoint, perturbVector))
            val newPerturbVector = diff(newPerturbPoint, newRererencePoint)
            val distance = vectorSpace.calcNorm(newPerturbVector)
            val alpha = distance / perturbationStrength
            perturbVector = newPerturbVector.view map (_ / alpha)                
            referencePoint = newRererencePoint                

            normSum += (log(alpha.toDouble()) : T)
            if (normSum.toDouble.isNegInfinity) normSum else normSum / (timeStepLength * (i - iterationsToSkip)).toDouble
        }
//        val overallTime = iterations * timeStepLength
//        normSums map (_ / overallTime) 
    }
}