package com.bnd.math.business.learning

import org.apache.commons.lang.StringUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import com.bnd.function.domain.Expression
import com.bnd.math.business.MathTest
import com.bnd.math.domain.rand.{RandomDistribution, UniformDiscreteDistribution, UniformDistribution}
import java.{lang => jl}

import com.bnd.function.{domain => fd}
import com.bnd.core.util.FileUtil
import junit.framework.Assert._

class IOStreamFactoryTest extends MathTest {

    @Autowired
    var ioStreamFactory : IOStreamFactory = null

    private def lwmaFun(ks : Iterable[Double], k0 : Double)(xs : Iterable[Double]) = (ks, xs).zipped.map(_*_).sum + k0

    private def wmmFun(k : Double, k0 : Double)(xs : Iterable[Double]) = k * xs.fold(0d)(math.max) + k0

    def round(x : jl.Double) : jl.Double = Math.round(x * 100000) / 100000D
    def printStream(s : Stream[Seq[jl.Double]], size : Int) = {
        s take size foreach(seq => {
        	print("[")
        	seq.map(x => print(round(x) + ","))
        	print("]")   
        })
        println
    }

    // @Test
    def testANDPairFunction() {
        val size = 20
        def andFun(xs: Iterable[jl.Double]) : jl.Double = if (xs.forall(_ == 1)) 1d else 0d
        val inputDistribution: RandomDistribution[jl.Double] = new UniformDiscreteDistribution[jl.Double](Array[jl.Double](0d, 1d))

        val traningStream = ioStreamFactory.createStaticInstance(andFun)(inputDistribution, 2)

        println("AND fun\n")

        println("IN\n")
        printStream(traningStream.inputStream, size)
        println("OUT\n")
        printStream(traningStream.outputStream, size)
    }

    // @Test
    def testPairFunction() {
        val size = 20
        def fun(x1 : jl.Double, x2 : jl.Double) = 2 * x1 + x2 : jl.Double

        val traningStream = ioStreamFactory.createInstance1DPair(fun, 0D : jl.Double)(new UniformDistribution[jl.Double](0D, 1D))

        println("Pair fun\n")
        
        printStream(traningStream.inputStream, size)
        printStream(traningStream.outputStream, size)
        val bufferedIOStream = ioStreamFactory.createDelayLineIOStream(0d : jl.Double)(traningStream,2)
        printStream(bufferedIOStream.inputStream, size)
        printStream(bufferedIOStream.outputStream, size)
    }
    
    // @Test
    def testExpression() {
        val size = 20
        val fun = Expression.Double("2 * x0 + x1")

        val traningStream = ioStreamFactory.createInstance1DFun(fun)(0d, 2, 2)(new UniformDistribution[jl.Double](0D, 1D))

        println("Pair fun as expression\n")
        
        printStream(traningStream.inputStream, size)
        printStream(traningStream.outputStream, size)
    }
 
    // @Test
    def testNarmaTanh10() {
        val order = 10
        val size = 100
        val traningStream = ioStreamFactory.createNarmaTanhInstance(order, false)(new UniformDistribution[jl.Double](0D, 0.5D))

        println("Narma IO\n")
        
        printStream(traningStream.inputStream, size)
        printStream(traningStream.outputStream, size)
        val bufferedIOStream = ioStreamFactory.createDelayLineIOStream(0D : jl.Double)(traningStream, order)
        println("Narma IO DL n = 10\n")
        printStream(bufferedIOStream.inputStream, size)
        printStream(bufferedIOStream.outputStream, size)
    }

    // @Test
    def testNarmaTanh10b() {
        val order = 10
        val size = 100

        val content = FileUtil.getInstance().readStringFromFileSafe("ac_3652_as_6989_t_100000_ts_1088.csv")
        val inputTargetOutputPairs = StringUtils.split(content, '\n').tail.map(row => {
            val items = StringUtils.split(row,',')
            (items(1).toDouble : jl.Double, items(2).toDouble : jl.Double)
        })
        val inputs : Seq[jl.Double] = inputTargetOutputPairs.map(_._1)
        val targetOutputs : Seq[jl.Double] = inputTargetOutputPairs.map(_._2)

        val traningStream = ioStreamFactory.createInstance1DStreamWithInputs(
            ioStreamFactory.javaNarmaTanh(order, false))(0)(inputs.toStream)

        println("Narma10 tanh\n")

        println("Inputs from file\n")
        inputs.take(size).foreach(print(_))
        println
        println("Inputs from training stream\n")
        printStream(traningStream.inputStream, size)

        println("Outputs from file\n")
        targetOutputs.take(size).foreach(print(_))
        println
        println("Outputs from training stream\n")
        printStream(traningStream.outputStream, size)


//        val bufferedIOStream = ioStreamFactory.createDelayLineIOStream(traningStream, order)
//        printStream(bufferedIOStream.inputStream, size)
//        printStream(bufferedIOStream.outputStream, size)
    }

    @Test
    def testLwma2 {
        val size = 2
        val ks = List[Double](0.5, 0.5)
        val k0 = 0.2
        val inputDistribution = new UniformDistribution[Double](0.2, 1d)

        val ioStream = ioStreamFactory.createInstance1D(lwmaFun(ks, k0)_)(0d,2,0)(inputDistribution)
        val trainingStream = ioStreamFactory.createDelayLineIOStream[Double](0d)(ioStream, size)

        println("LWMA2")
        println("Input : " + ioStream.inputStream.map(_.head).take(20).toSeq.mkString(","))
        println("Output: " + ioStream.outputStream.map(_.head).take(20).toSeq.mkString(","))
        println("LWMA2 DL")
        println("Input : " + trainingStream.inputStream.take(20).toSeq.mkString(","))
        println("Output: " + trainingStream.outputStream.map(_.head).take(20).toSeq.mkString(","))
    }
}