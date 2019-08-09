package com.bnd.math.business.rand

import java.{lang => jl}

import com.bnd.math.domain.rand.RepeatedDistribution
import com.bnd.math.business.MathUtil
import com.bnd.core.util.RandomUtil

private class RepeatedDistributionProvider[T](
		distribution : RepeatedDistribution[T],
		doubleValue : T => jl.Double
	) extends AbstractRandomDistributionProvider[T](distribution) {

    var index = 0

    val _mean = MathUtil.calcMean(distribution.getValues.map(doubleValue(_)))

    private def square(x : Double) = x*x

    val _variance = distribution.getValues.foldLeft(0d)
    		{case (result, value) => result +  square((_mean - doubleValue(value)))}

	override def next = {
	    val value = distribution.getValues.apply(index)
	    index += 1
	    if (index >= distribution.getValues.size)
	        index = 0
	    value
	}

	override def mean = _mean

	override def variance = _variance
}