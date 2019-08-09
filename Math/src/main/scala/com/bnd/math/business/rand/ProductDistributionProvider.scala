package com.bnd.math.business.rand

import scala.collection.JavaConversions._

protected class ProductDistributionProvider[T](
        times : (T, T) => T)(
		distr1 : RandomDistributionProvider[T],
		distr2 : RandomDistributionProvider[T]
	) extends AbstractRandomDistributionProvider[T](distr1.getValueType) {

	override def next = times(distr1.next, distr2.next) 

	override def mean = distr1.mean * distr2.mean

	override def variance = (distr1.mean * distr1.mean) * distr2.variance + (distr2.mean * distr2.mean) * distr1.variance + distr1.variance * distr2.variance
}