package com.bnd.math.business.rand

import java.{lang => jl}

import com.bnd.math.domain.rand.DiscreteDistribution
import com.bnd.core.util.RandomUtil

private class DiscreteDistributionProvider[T](
		distribution : DiscreteDistribution[T],
		doubleValue : T => jl.Double
	) extends AbstractRandomDistributionProvider[T](distribution) {

    val probSumValues = { 
        val probSums = distribution.getProbabilities.scanLeft(0d){case (a, b) => a + b}
        probSums.tail zip distribution.getValues
    }

    val probSum = distribution.getProbabilities.reduce(_+_)

    val _mean = (distribution.getProbabilities, distribution.getValues).zipped.foldLeft(0d)
    		{case (result, (prob, value)) => result + prob * doubleValue(value)}

    private def square(x : Double) = x*x

    val _variance = (distribution.getProbabilities, distribution.getValues).zipped.foldLeft(0d)
    		{case (result, (prob, value)) => result + prob * square((_mean - doubleValue(value)))}

	override def next = {
	    val hitProb = RandomUtil.nextDouble(probSum)
    	val probSumValue = probSumValues.find(_._1 > hitProb)
    	probSumValue.get._2
	}

	override def mean = _mean

	override def variance = _variance
}