package com.bnd.math.business.rand

import com.bnd.core.util.ScalaRandomUtil
import com.bnd.math.domain.rand.ShapeLocationDistribution

private class AnyValLogNormalDistributionProvider[T <: AnyVal](
        distribution : ShapeLocationDistribution[T]) extends AbstractRandomDistributionProvider[T](distribution) {

	override def next = ScalaRandomUtil.nextLogNormal(clazz, distribution.getLocation, distribution.getShape)

	override def mean = math.exp(distribution.getLocation + (distribution.getShape / 2))

	override def variance = {
		val location = distribution.getLocation
		val shape = distribution.getShape
		math.exp(2 * (location + shape)) - Math.exp(2 * location + shape)
	}
}