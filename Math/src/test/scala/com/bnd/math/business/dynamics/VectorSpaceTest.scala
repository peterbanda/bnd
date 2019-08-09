package com.bnd.math.business.dynamics

import scala.collection.JavaConversions._
import scala.collection._
import scala.math._
import scala.math.Integral._
import scala.Numeric.DoubleAsIfIntegral
import com.bnd.core.DoubleConvertible.Implicits._

import org.junit.Test

import com.bnd.core.CollectionElementsConversions._

class VectorSpaceTest {

    // TODO: this is supposed to be provided automatically
    implicit val doubleAsIntegral = DoubleAsIfIntegral

    val vectorSpace = new EuclideanVectorSpace[Double]

    @Test
    def testOrthonormalizeVectors1() {
        val vectors : Iterable[Iterable[Double]] = List(List(3,1), List(2,2))
        val expectedOrthonormalVectors : Iterable[Iterable[Double]] = List(
                List(3/sqrt(10),  1/sqrt(10)),
                List(-1/sqrt(10), 3/sqrt(10)))
        testOrthonormalizeVectors(vectors, expectedOrthonormalVectors)
    }

    @Test
    def testOrthonormalizeVectors2() {
        val vectors : Iterable[Iterable[Double]] = List(List(1,1,1,1), List(-1,4,4,1), List(4,-2,2,0))
        val expectedOrthonormalVectors : Iterable[Iterable[Double]] = List(
                List(0.5            , 0.5             , 0.5            , 0.5),
                List(-1/sqrt(2)     , sqrt(2)/3       , sqrt(2)/3      , -1/(3*sqrt(2))),
                List(1/(2 * sqrt(3)), -5/(6 * sqrt(3)), 7/(6 * sqrt(3)), -5/(6 * sqrt(3))))
        testOrthonormalizeVectors(vectors, expectedOrthonormalVectors)
    }

    @Test
    def testOrthonormalizeVectors3() {
        val vectors : Iterable[Iterable[Double]] = List(List(1,1,1,1), List(-1,4,4,-1), List(4,-2,2,0))
        val expectedOrthonormalVectors : Iterable[Iterable[Double]] = List(
                List(0.5  , 0.5   , 0.5 , 0.5),
                List(-0.5 , 0.5   , 0.5 , -0.5),
                List(0.5  , -0.5  , 0.5 , -0.5))
        testOrthonormalizeVectors(vectors, expectedOrthonormalVectors)
    }

    private def testOrthonormalizeVectors(
        vectors : Iterable[Iterable[Double]],
        expectedOrthonormalVectors : Iterable[Iterable[Double]]) {
        val orthonormalVectors = vectorSpace orthonormalizeVectors vectors

        // check the results
        println("Expected")
        for (v <- expectedOrthonormalVectors) println(v)
        println("Actual")
        for (v <- orthonormalVectors) println(v)
        println("Norms")
        orthonormalVectors map (x => println(vectorSpace.calcNorm(x)))
    }
}