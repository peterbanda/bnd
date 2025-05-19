package com.bnd.math.business.rand

import java.{lang => jl, util => ju}

import scala.jdk.CollectionConverters._
import com.bnd.math.domain.rand.DiscreteDistribution
import com.bnd.core.util.RandomUtil

private class BooleanDensityUniformDistributionProvider extends AbstractRandomDistributionProvider[jl.Boolean](classOf[jl.Boolean]) {

    override def next = RandomUtil.nextBoolean

	override def mean = 0.5

	override def variance = 0.5

	override def nextList(size : Int) = {
	    val ones = RandomUtil.nextInt(size + 1)
	    val positions = RandomUtil.nextElementsWithoutRepetitions(size, ones)

	    (0 until size).map { i =>
				(positions.contains(i : Integer)) : jl.Boolean
			}.asJava : ju.List[jl.Boolean]
	}
}