package com.bnd.math.business.rand

import scala.AnyVal
import com.bnd.core.util.ScalaRandomUtil
import com.bnd.math.domain.rand.UniformDistribution
import com.bnd.core.NumericConversions._

private class AnyValUniformDistributionProvider[T <: AnyVal](
		distribution : UniformDistribution[T]) extends AbstractRandomDistributionProvider[T](distribution) {

	override def next = ScalaRandomUtil.next(clazz, distribution.getFrom, distribution.getTo)

	override def mean = (convertAsNumeric[Double](distribution.getFrom) + convertAsNumeric[Double](distribution.getTo)) / 2

	override def variance = {
		val diff = convertAsNumeric[Double](distribution.getTo) - convertAsNumeric[Double](distribution.getFrom)
		diff * diff / 12
	}
}