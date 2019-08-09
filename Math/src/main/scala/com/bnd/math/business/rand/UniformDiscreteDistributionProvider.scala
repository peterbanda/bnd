package com.bnd.math.business.rand

import java.{lang => jl}

import com.bnd.math.domain.rand.UniformDiscreteDistribution
import com.bnd.core.util.RandomUtil

private class UniformDiscreteDistributionProvider[T](
  distribution: UniformDiscreteDistribution[T],
  doubleValue: T => jl.Double
) extends AbstractRandomDistributionProvider[T](distribution) {

  val _mean = distribution.getValues.foldLeft(0d) { case (result, value) => result + doubleValue(value)} / distribution.getValues.size

  private def square(x: Double) = x * x

  val _variance = distribution.getValues.foldLeft(0d) { case (result, value) => result + square((_mean - doubleValue(value)))} / distribution.getValues.size

  override def next = RandomUtil.nextElement(distribution.getValues)

  override def mean = _mean

  override def variance = _variance
}