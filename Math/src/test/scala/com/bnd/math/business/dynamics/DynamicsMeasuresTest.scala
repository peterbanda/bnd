package com.bnd.math.business.dynamics

import scala.collection.JavaConversions._
import scala.collection._
import scala.math._
import scala.math.Numeric._
import scala.Numeric.DoubleAsIfIntegral

import org.junit.Test

import com.bnd.core.CollectionElementsConversions._
import com.bnd.math.business.dynamics.DynamicsMeasures;

class DynamicsMeasuresTest {

    val dynamicsMeasures = DynamicsMeasures

//    @Test
//    def testComputeRank() {
//        val d = Array(1, 2, 3)
//        val result = dynamicsMeasures computeRank Array(Array(1, 2, 3), Array(3, 4, 5))
//        println(result)
//    }

//    @Test
//    def testLaypunov() {
//        val a = List(Seq(1.9D, 2D, 3D),Seq(4D, 5D, 6D))
//        val b = List(Seq(7D, 8.2D, 9D),Seq(10D, 11.9D, 12D))
//        val aa : Iterable[Seq[java.lang.Double]] = scalaDoubleIterableMatrixTojavaDoubleIterableMatrix(a)
//        val bb : Iterable[Seq[java.lang.Double]] = scalaDoubleIterableMatrixTojavaDoubleIterableMatrix(b)
//        def metricsFun = (x : Seq[java.lang.Double], y : Seq[java.lang.Double]) => (x,y).zipped.map((xx,yy) => abs(xx-yy)).sum
//        val result = dynamicsMeasures calcLyapunovExponent(a : List[Seq[java.lang.Double]], b : List[Seq[java.lang.Double]], metricsFun)
//        println(result)
//    }

//    @Test
//    def testLaypunov2() {
//        val a = List(List(1.9D, 2D, 3D),List(4D, 5D, 6D))
//        val b = List(List(7D, 8.2D, 9D),List(10D, 11.9D, 12D))
//        val result = dynamicsMeasures calcLyapunovExponent(a : List[List[java.lang.Double]], b : List[List[java.lang.Double]], createTestMetrics : Metrics[java.lang.Double])
//        println(result)
//    }

//    private def createTestMetrics : Metrics[java.lang.Double] = {
//        new Metrics[java.lang.Double](){
//            override def calcDistance(firstPoint : java.util.List[java.lang.Double], secondPoint : java.util.List[java.lang.Double]) : Double = {
//                (for ((x,y) <- (firstPoint zip secondPoint)) yield abs(x - y)).sum
//            }
//        }
//    }

//    @Test
//    def testXXX() {
//        def fun1[T](x : Iterator[T]) : Iterable[Iterable[T]] = {
//            if (x.hasNext) {
//            	val e = x.next()
//            	List(e) :: (fun1(x) map (list => e :: list))
//            } else {
//                List(List())
//            }
//        	for (i <- 1 to x.size) yield (x take i)   
//        }
//        def fun[T](x : Iterable[T]) = for (i <- 1 to x.size) for (vec <- (x take i)) yield vec
//        def fun1[T](x : Iterable[T]) = (for ((a,i) <- x.view.zipWithIndex) yield (x take (i + 1))).force
//        def fun2[T](x : Iterable[T]) = for ((a,i) <- x.zipWithIndex) yield (x take (i + 1))
//        println(fun2(Seq(1,2,3)))
//    }
}