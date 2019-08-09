package com.bnd.math.business

import com.bnd.math.business.rand.RandomDistributionProviderFactory
import com.bnd.math.domain.Stats
import org.junit.Test
import java.{lang => jl, util => ju}

import scala.collection.JavaConversions._
import org.junit.Assert._
import org.springframework.beans.factory.annotation.Autowired
import com.bnd.function.enumerator.ListEnumeratorFactory
import org.apache.commons.lang.StringUtils
import com.bnd.math.business.learning.IOStreamFactory
import com.bnd.math.domain.rand.UniformDistribution
import com.bnd.core.util.RandomUtil

import scala.collection.mutable.ListBuffer

class MathUtilTest extends MathTest {

    @Autowired
    val ioStreamFactory : IOStreamFactory = null

    private def narma2SimplifiedStreamFun(
    	a : Double,
    	b : Double, 
    	c : Double,
    	d : Double)(
    	xs : Seq[Double],
    	ys : Seq[Double]) = a * ys.last + b * ys.last * ys.last + c * xs.last * xs.head + d 

    private def narma2StreamFun(
    	a : Double,
    	b : Double, 
    	c : Double,
    	d : Double)(
    	xs : Seq[Double],
    	ys : Seq[Double]) = a * ys.last + b * ys.last * (ys.last + ys.head) + c * xs.last * xs.head + d

    private def henonStreamFun(
    	a : Double,
    	b : Double,
    	stdDev : Double)(
    	ys : Seq[Double]) = 1 - a * ys.last * ys.last + b * ys.head + RandomUtil.nextNormal(0, stdDev); 

    private def maxStreamFun(
    	xs : Seq[Double],
    	ys : Seq[Double]) = RandomUtil.nextDouble(0.2, 0.8) * math.max(xs.head, xs.last) + RandomUtil.nextDouble(0.1, 0.4)

    private def lwaStreamFun(
    	xs : Seq[Double],
    	ys : Seq[Double]) = RandomUtil.nextDouble(0.2, 0.8) * xs.head + RandomUtil.nextDouble(0.2, 0.8) * xs.last + RandomUtil.nextDouble(0.1, 0.4)

    private def lwmaFun(ks : Iterable[Double], k0 : Double)(xs : Iterable[Double]) = (ks, xs).zipped.map(_*_).sum + k0

    private def wmmFun(k : Double, k0 : Double)(xs : Iterable[Double]) = k * xs.fold(0d)(math.max) + k0

    private def printStats(stats : Stats) = {
        println("Mean:     " + stats.getMean)
        println("Variance: " + stats.getVariance)
        println("STD Dev:  " + stats.getStandardDeviation)
    }

//    @Test
    def narma2StatsTest {
        val inputStream = ioStreamFactory.createRandomStream(new UniformDistribution[Double](0, 0.5))
        val outputStream = ioStreamFactory.inputRecursiveStream(narma2StreamFun(0.3, 0.05, 1.5, 0.1))(0d)(2,2)(inputStream)
        val outputStreamTimesTwo = outputStream.map(_ * 2)
        val stats = MathUtil.calcStats(0, outputStream.take(1000000))
        val statsTimesTwo = MathUtil.calcStats(0, outputStreamTimesTwo.take(1000000))
        println("NARMA2")
        printStats(stats)
        println("2 * NARMA2")
        printStats(statsTimesTwo)
    }

//    @Test
    def narma2StatsbTest {
        val inputStream = ioStreamFactory.createRandomStream(new UniformDistribution[Double](0, 0.5))
        val outputStream = ioStreamFactory.narma(2)(inputStream)
        val outputStreamTimesTwo = outputStream.map(_ * 2)
        val stats = MathUtil.calcStats(0, outputStream.take(1000000))
        val statsTimesTwo = MathUtil.calcStats(0, outputStreamTimesTwo.take(1000000))
        println("NARMA2b")
        printStats(stats)
        println("2 * NARMA2b")
        printStats(statsTimesTwo)
    }

//    @Test
    def narma10StatsTest {
        val inputStream = ioStreamFactory.createRandomStream(new UniformDistribution[Double](0, 0.5))
        val outputStream = ioStreamFactory.narma(10)(inputStream)
        val outputStreamTimesTwo = outputStream.map(_ * 2)
        val stats = MathUtil.calcStats(0, outputStream.take(100000))
        val statsTimesTwo = MathUtil.calcStats(0, outputStreamTimesTwo.take(100000))
        println("NARMA10")
        printStats(stats)
        println("2 * NARMA10")
        printStats(statsTimesTwo)
    }

//    @Test
    def narmaTanh10StatsTest {
        val inputStream = ioStreamFactory.createRandomStream(new UniformDistribution[Double](0, 0.5))
        val outputStream = ioStreamFactory.narmaTanh(10)(inputStream)
        val outputStreamTimesTwo = outputStream.map(_ * 2)
        val stats = MathUtil.calcStats(0, outputStream.take(100000))
        val statsTimesTwo = MathUtil.calcStats(0, outputStreamTimesTwo.take(100000))
        println("NARMA (tanh) 10")
        printStats(stats)
        println("2 * NARMA (tanh) 10")
        printStats(statsTimesTwo)
    }

//    @Test
    def henonStatsTest {
        val outputStream = ioStreamFactory.recursiveStream(henonStreamFun(1.4, 0.3, 0.001))(0d)(2)
        val shiftedScaledOutputStream = outputStream.map(x => 0.25 * (x + 4))
        val stats = MathUtil.calcStats(0, shiftedScaledOutputStream.take(1000000))
        printStats(stats)
    }

//    @Test
    def maxStatsTest {
        val inputStream = ioStreamFactory.createRandomStream(new UniformDistribution[Double](0.2, 1d))
        val outputStream = ioStreamFactory.inputRecursiveStream(maxStreamFun)(0d)(2,2)(inputStream)
        val stats = MathUtil.calcStats(0, outputStream.take(2000000))
        printStats(stats)
    }

//    @Test
    def lwmaStatsTest {
        val inputStream = ioStreamFactory.createRandomStream(new UniformDistribution[Double](0.2, 1d))
        val outputStream = ioStreamFactory.inputRecursiveStream(lwaStreamFun)(0d)(2,2)(inputStream)
        val stats = MathUtil.calcStats(0, outputStream.take(1000000))
        printStats(stats)
    }

//    @Test
    def lwmaStatsNewTest {
        val size = 2
        val ksRDP = RandomDistributionProviderFactory.apply(new UniformDistribution[Double](0.2, 0.8))
        val k0RDP = RandomDistributionProviderFactory.apply(new UniformDistribution[Double](0.1, 0.4))
        val inputDistribution = new UniformDistribution[Double](0.2, 1d)

        val globalOutputStream = new ListBuffer[Double]
        for (_ <-1 to 10000) {
            val trainingStream = ioStreamFactory.createInstance1D(lwmaFun(ksRDP.nextList(size), k0RDP.next)_)(0d, size, size)(inputDistribution)
            globalOutputStream ++= trainingStream.outputStream.map(_.head).take(1000).toSeq
        }

        val stats = MathUtil.calcStats(0, globalOutputStream)
        println("LWMA2")
        printStats(stats)
    }

 //   @Test
    def wmmStatsNewTest {
        val size = 2
        val kRDP = RandomDistributionProviderFactory.apply(new UniformDistribution[Double](0.2, 0.8))
        val k0RDP = RandomDistributionProviderFactory.apply(new UniformDistribution[Double](0.1, 0.4))
        val inputDistribution = new UniformDistribution[Double](0.2, 1d)

        val globalOutputStream = new ListBuffer[Double]
        for (_ <-1 to 10000) {
            val trainingStream = ioStreamFactory.createInstance1D(wmmFun(kRDP.next, k0RDP.next)_)(0d, size, size)(inputDistribution)
            globalOutputStream ++= trainingStream.outputStream.map(_.head).take(1000).toSeq
        }

        val stats = MathUtil.calcStats(0, globalOutputStream)
        println("WMM2")
        printStats(stats)
    }
}