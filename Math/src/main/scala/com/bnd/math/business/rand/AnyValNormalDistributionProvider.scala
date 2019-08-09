package com.bnd.math.business.rand

import com.bnd.core.util.ScalaRandomUtil
import com.bnd.math.domain.rand.ShapeLocationDistribution

private class AnyValNormalDistributionProvider[T <: AnyVal](
		distribution : ShapeLocationDistribution[T]) extends AbstractRandomDistributionProvider[T](distribution) {

	override def next = ScalaRandomUtil.nextNormal(clazz, distribution.getLocation, distribution.getShape)

	override def mean = distribution.getLocation

	override def variance = distribution.getShape
}