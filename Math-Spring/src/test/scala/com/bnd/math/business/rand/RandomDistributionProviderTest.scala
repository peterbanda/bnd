package com.bnd.math.business.rand

import scala.collection.JavaConversions._
import java.{lang => jl}
import scala.collection._
import scala.math._
import scala.math.Integral._
import scala.Numeric.DoubleAsIfIntegral
import com.bnd.core.DoubleConvertible.Implicits._
import org.junit.Test
import com.bnd.core.CollectionElementsConversions._
import com.bnd.math.domain.rand.RandomDistribution
import com.bnd.math.domain.rand.DiscreteDistribution
import com.bnd.math.business.MathTest
import com.bnd.math.domain.rand.UniformDistribution
import com.bnd.math.domain.rand.CompositeDistribution
import com.bnd.math.domain.rand.CompositeDistribution.CompositeFunction
import com.bnd.math.domain.rand.BooleanDensityUniformDistribution

class RandomDistributionProviderTest extends MathTest {

    @Test
    def testBooleanDensityUniform() {
        val rdp = RandomDistributionProviderFactory.apply(new BooleanDensityUniformDistribution)
        (1 to 100).foreach( _ => println(rdp.nextList(10)))
    }

//    @Test
    def testDiscrete() {
        val rdp = RandomDistributionProviderFactory.apply(new DiscreteDistribution[jl.Boolean](
                Array(0.5d, 0.5d), Array(false, true)))
        rdp.next
    }

    // Y = k1 X1 + k2 X2 + k0, 0.2 < ki < 0.8, 0.2 < x < 1
//    @Test
    def testComposite1() {
        val k0 = new UniformDistribution[jl.Double](0.2, 0.8)
        val k1 = new UniformDistribution[jl.Double](0.2, 0.8)
        val k2 = new UniformDistribution[jl.Double](0.2, 0.8)
        
        val x1 = new UniformDistribution[jl.Double](0.2, 1d)
        val x2 = new UniformDistribution[jl.Double](0.2, 1d)

        val d = new CompositeDistribution[jl.Double](
            	new CompositeDistribution[jl.Double](
            		new CompositeDistribution[jl.Double](k1, x1, CompositeFunction.TIMES),
            		new CompositeDistribution[jl.Double](k2, x2, CompositeFunction.TIMES),
            		CompositeFunction.PLUS),
            	k0, CompositeFunction.PLUS)

        val rdp = RandomDistributionProviderFactory.apply(d)

        println("Y = k1 X1 + k2 X2 + k0, 0.2 < ki < 0.8, 0.2 < x < 1")
        println("Mean " + rdp.mean + ", variance " + rdp.variance + ", deviation " + math.sqrt(rdp.variance))
    }

    // Y = k1 X1 + k2 X2 + k0 + 0.3, 0.2 < k1 < 0.8, -0.3 < k2 < 0, 0.4 < k0 < 0.7, 0.2 < x < 1
//    @Test
    def testComposite1a() {
        val k0 = new UniformDistribution[jl.Double](0.4, 0.7)
        val k1 = new UniformDistribution[jl.Double](0.2, 0.8)
        val k2 = new UniformDistribution[jl.Double](-0.3, 0d)

        val x1 = new UniformDistribution[jl.Double](0.2, 1d)
        val x2 = new UniformDistribution[jl.Double](0.2, 1d)

        val d = new CompositeDistribution[jl.Double](
            	new CompositeDistribution[jl.Double](
            		new CompositeDistribution[jl.Double](k1, x1, CompositeFunction.TIMES),
            		new CompositeDistribution[jl.Double](k2, x2, CompositeFunction.TIMES),
            		CompositeFunction.PLUS),
            	k0, CompositeFunction.PLUS)

        val rdp = RandomDistributionProviderFactory.apply(d)

        println("Y = k1 X1 + k2 X2 + k0, 0.2 < k1 < 0.8, -0.3 < k2 < 0, 0.4 < k0 < 0.7, 0.2 < x < 1")
        println("Mean " + rdp.mean + ", variance " + rdp.variance + ", deviation " + math.sqrt(rdp.variance))
    }

    // Y = k1 X1 or k2 x2, 0.2 < ki < 0.8, 0.2 < x < 1
//    @Test
    def testComposite2() {
        val k1 = new UniformDistribution[jl.Double](0.2, 0.8)
        val x1 = new UniformDistribution[jl.Double](0.2, 1)

        val d = new CompositeDistribution[jl.Double](k1, x1, CompositeFunction.TIMES)

        val rdp = RandomDistributionProviderFactory.apply(d)

        println("Y = k1 X1 or k2 x2, 0.2 < ki < 0.8, 0.2 < x < 1")
        println("Mean " + rdp.mean + ", variance " + rdp.variance + ", deviation " + math.sqrt(rdp.variance))
    }

    // Y = k X1 X2, 0.2 < ki < 0.8, 0.2 < x < 1
//    @Test
    def testComposite3() {
        val k = new UniformDistribution[jl.Double](0.2, 0.8)
        val x1 = new UniformDistribution[jl.Double](0.2, 1)
        val x2 = new UniformDistribution[jl.Double](0.2, 1)

        val d = new CompositeDistribution[jl.Double](
                new CompositeDistribution[jl.Double](k, x1, CompositeFunction.TIMES),
                x2, CompositeFunction.TIMES)

        val rdp = RandomDistributionProviderFactory.apply(d)

        println("Y = k X1 X2, 0.2 < ki < 0.8, 0.2 < x < 1")
        println("Mean " + rdp.mean + ", variance " + rdp.variance + ", deviation " + math.sqrt(rdp.variance))
    }

    // Y = k0, 0.1 < k0 < 0.4
//    @Test
    def testComposite4() {
        val d = new UniformDistribution[jl.Double](0.1, 0.4)

        val rdp = RandomDistributionProviderFactory.apply(d)

        println("Y = k0, 0.1 < k0 < 0.4")
        println("Mean " + rdp.mean + ", variance " + rdp.variance + ", deviation " + math.sqrt(rdp.variance))
    }

    // Y = k0 X1 X2 + k1 X1 + k2 X2 + k0, 0 < k0 < 1, 0 < ki < 0.5, 0 < x < 0.5
//    @Test
    def testComposite5() {
        val k0 = new UniformDistribution[jl.Double](0, 1)
        val k1 = new UniformDistribution[jl.Double](0, 0.5)
        val k2 = new UniformDistribution[jl.Double](0, 0.5)
        
        val x1 = new UniformDistribution[jl.Double](0, 1)
        val x2 = new UniformDistribution[jl.Double](0, 1)

        val d = new CompositeDistribution[jl.Double](
            	new CompositeDistribution[jl.Double](
            		new CompositeDistribution[jl.Double](k1, x1, CompositeFunction.TIMES),
            		new CompositeDistribution[jl.Double](k2, x2, CompositeFunction.TIMES),
            		CompositeFunction.PLUS),
            	new CompositeDistribution[jl.Double](
            		new CompositeDistribution[jl.Double](k0, x1, CompositeFunction.TIMES),
            		x2, CompositeFunction.TIMES),
            	CompositeFunction.PLUS)

        val rdp = RandomDistributionProviderFactory.apply(d)

        println("Y = k0 X1 X2 + k1 X1 + k2 X2 + k0, 0 < k0 < 1, 0 < ki < 0.5, 0 < x < 0.5")
        println("Mean " + rdp.mean + ", variance " + rdp.variance + ", deviation " + math.sqrt(rdp.variance))
    }

    // Y = k1 X1 + k2 X2 + k0, 0.2 < ki < 0.8, 0.1 < k0 < 0.4, 0.2 < x < 1
//    @Test
    def testComposite6() {
        val k0 = new UniformDistribution[jl.Double](0.1, 0.4)
        val k1 = new UniformDistribution[jl.Double](0.2, 0.8)
        val k2 = new UniformDistribution[jl.Double](0.2, 0.8)

        val x1 = new UniformDistribution[jl.Double](0.2, 1)
        val x2 = new UniformDistribution[jl.Double](0.2, 1)

        val d = new CompositeDistribution[jl.Double](
            	new CompositeDistribution[jl.Double](
            		new CompositeDistribution[jl.Double](k1, x1, CompositeFunction.TIMES),
            		new CompositeDistribution[jl.Double](k2, x2, CompositeFunction.TIMES),
            		CompositeFunction.PLUS),
            	k0, CompositeFunction.PLUS)

        val rdp = RandomDistributionProviderFactory.apply(d)

        println("Y = k1 X1 + k2 X2 + k0, 0.2 < ki < 0.8, 0.1 < k0 < 0.4, 0.2 < x < 1")
        println("Mean " + rdp.mean + ", variance " + rdp.variance + ", deviation " + math.sqrt(rdp.variance))
    }

    // Y = k1 X1 + k2 X2, 0 < ki < 10, 0 < x < 500
//    @Test
    def testComposite7() {
        val k1 = new UniformDistribution[jl.Double](0, 10d)
        val k2 = new UniformDistribution[jl.Double](0, 10d)

        val x1 = new UniformDistribution[jl.Double](0, 500d)
        val x2 = new UniformDistribution[jl.Double](0, 500d)

        val d = new CompositeDistribution[jl.Double](
            		new CompositeDistribution[jl.Double](k1, x1, CompositeFunction.TIMES),
            		new CompositeDistribution[jl.Double](k2, x2, CompositeFunction.TIMES),
            		CompositeFunction.PLUS)

        val rdp = RandomDistributionProviderFactory.apply(d)

        println("Y = k1 X1 + k2 X2, 0 < ki < 10, 0 < x < 500")
        println("Mean " + rdp.mean + ", variance " + rdp.variance + ", deviation " + math.sqrt(rdp.variance))
    }

    // Y = k1 X1 + k2 X2 + k0, 0.2 < k1 < 0.8, -0.3 < k2 < 0, 0.3 < k0 < 0.4, 0.2 < x < 1
    @Test
    def testComposite8() {
        val k0 = new UniformDistribution[jl.Double](0.3, 0.4)
        val k1 = new UniformDistribution[jl.Double](0.2, 0.8)
        val k2 = new UniformDistribution[jl.Double](-0.3, 0d)

        val x1 = new UniformDistribution[jl.Double](0.2, 1d)
        val x2 = new UniformDistribution[jl.Double](0.2, 1d)

        val d = new CompositeDistribution[jl.Double](
            new CompositeDistribution[jl.Double](
                new CompositeDistribution[jl.Double](k1, x1, CompositeFunction.TIMES),
                new CompositeDistribution[jl.Double](k2, x2, CompositeFunction.TIMES),
                CompositeFunction.PLUS),
            k0, CompositeFunction.PLUS)

        val rdp = RandomDistributionProviderFactory.apply(d)

        println("Y = k1 X1 + k2 X2 + k0, 0.2 < k1 < 0.8, -0.3 < k2 < 0, 0.3 < k0 < 0.4, 0.2 < x < 1")
        println("Mean " + rdp.mean + ", variance " + rdp.variance + ", deviation " + math.sqrt(rdp.variance))
    }
}