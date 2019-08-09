package com.bnd.math.business.dynamics

import scala.Array._
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import scala.collection._
import scala.math.Integral.Implicits._
import scala.math.Numeric
import scala.math._
import java.util.Date
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core._
import scala.util.Random

class VectorSpace[T : Integral](
    val calcInnerProduct : (Iterable[T], Iterable[T]) => T,
    val calcNorm : Iterable[T] => T,
    val createRandomVectorFromNorm : (T, Int) => Iterable[T]) {

    val num = implicitly[Integral[T]]
    val zero = num.zero

	// Gram–Schmidt method for orthogonal vectors
    def orthogonalizeVectors[C[X] <: Iterable[X]](
        vectors: Iterable[C[T]]
    ): Iterable[C[T]] = {
        var innerProductMap = new HashMap[Int, T]
        // define vector projection
        def projectVector(index : Int, u : C[T], v : C[T]) = {
            var selfProduct = innerProductMap.getOrElseUpdate(index, calcInnerProduct(u,u))
            if (selfProduct != num.zero) {
            	val productRatio = calcInnerProduct(u,v) / selfProduct
            	u.view map (_ * productRatio)
            } else {
                u
            }
        }

        // create orthogonal vectors
        var orthogonalVectors : ListBuffer[C[T]] = ListBuffer()
        for (vector <- vectors) {
            val projectThroughVector = projectVector(_ : Int, _ : C[T], vector)
            val orthogonalVector = orthogonalVectors.view.zipWithIndex.map(e =>  projectThroughVector(e._2, e._1)).
            		foldLeft(vector)((_,_).zipped.map(_-_).asInstanceOf[C[T]])
            orthogonalVectors += orthogonalVector
        }
        orthogonalVectors.toList
    }

    // Gram–Schmidt method
    def orthonormalizeVectors(
        vectors: Iterable[Iterable[T]]
    ): Iterable[Iterable[T]] = {
        orthogonalizeVectors(vectors) map normalizeVector
    }

    // Gram–Schmidt method
    def orthonormalizeVectors2[C[X] <: Iterable[X]](
        vectors: Iterable[C[T]]
    ): Iterable[C[T]] = {

        // define vector projection (works for normalized vectors)
        def projectVector(u : C[T], v : C[T]) = (u map (_ * calcInnerProduct(u,v))).asInstanceOf[C[T]]

        // create orthonormal vectors directly
        var orthonormalVectors : ListBuffer[C[T]] = ListBuffer()
        for (vector <- vectors) {
            val orthogonalVector = (for (otherVector <- orthonormalVectors)
                						yield projectVector(otherVector, vector)).foldLeft(vector)((_,_).zipped.map(_-_).asInstanceOf[C[T]])
        	orthonormalVectors += normalizeVector(orthogonalVector)
        }
        orthonormalVectors.toList
    }

    def normalizeVector[C[X] <: Iterable[X]](
    	vector: C[T]
    ) : C[T] = {
    	val norm = calcNorm(vector)
    	if (norm != zero) {
    		(vector map (_ / norm)).asInstanceOf[C[T]]
    	} else {
    	    vector
    	}
    }
}

object InnerProducts {
	def calcEuclideanInnerProduct[T : Numeric](
	    uu: Iterable[T], vv: Iterable[T]) : T = {
	    val num = implicitly[Numeric[T]]
	    (uu,vv).zipped.map(num.times).sum
	}
}

object Norms {
	def calcEuclideanVectorNorm[T : DoubleConvertible : Numeric](
	    vector: Iterable[T]) : T = {
		val converter = implicitly[DoubleConvertible[T]]
	    converter.fromDouble(
	            sqrt(converter.toDouble(InnerProducts.calcEuclideanInnerProduct(vector, vector))))
	}

	def calcManhattanVectorNorm[T : Numeric](
	    vector: Iterable[T]) : T = {
		val num = implicitly[Numeric[T]]
		val absVector = vector map num.abs
	    InnerProducts.calcEuclideanInnerProduct(absVector, absVector)
	}
}

object RandomVectors {
	def createEuclideanRandomVectorFromNorm[T : DoubleConvertible : Numeric](
	    norm : T, dims : Int) : Iterable[T] = {
		val num = implicitly[Numeric[T]]
		val converter = implicitly[DoubleConvertible[T]]
		val fromDouble = converter.fromDouble(_)
		val toDouble = converter.toDouble(_)

		val squareNorm = converter.toDouble(num.times(norm, norm))
		val divisionPoints = (for (i <- 1 to dims) yield squareNorm * Random.nextDouble()).sortWith(_ < _)
		var prevDivisionPoint = num.zero
		val point = for (divisionPoint <- (divisionPoints ++ List(squareNorm))) yield {
		    val divPoint = fromDouble(divisionPoint)
		    val size = fromDouble(sqrt(toDouble(
		            num.minus(divPoint, prevDivisionPoint))))
		    prevDivisionPoint = divPoint
		    size
		}
		Random.shuffle(point)
	}

	def createManhattanRandomVectorFromNorm[T : DoubleConvertible : Numeric](
	    norm : T, dims : Int) : Iterable[T] = {
		val num = implicitly[Numeric[T]]
		val converter = implicitly[DoubleConvertible[T]]
		val fromDouble = converter.fromDouble(_)
		val toDouble = converter.toDouble(_)

		val divisionPoints = (for (i <- 1 to dims) yield toDouble(norm) * Random.nextDouble()).sortWith(_ < _)
		var prevDivisionPoint = num.zero
		val point = for (divisionPoint <- divisionPoints) yield {
			num.minus(fromDouble(divisionPoint), prevDivisionPoint)
		}
		Random.shuffle(point)
	}
}

class EuclideanVectorSpace[T : DoubleConvertible : Integral] extends VectorSpace[T](
        InnerProducts.calcEuclideanInnerProduct(_,_),
        Norms.calcEuclideanVectorNorm(_),
        RandomVectors.createEuclideanRandomVectorFromNorm(_,_))

class ManhattanVectorSpace[T : DoubleConvertible : Integral] extends VectorSpace[T](
        InnerProducts.calcEuclideanInnerProduct(_,_),
        Norms.calcManhattanVectorNorm(_),
        RandomVectors.createManhattanRandomVectorFromNorm(_,_))