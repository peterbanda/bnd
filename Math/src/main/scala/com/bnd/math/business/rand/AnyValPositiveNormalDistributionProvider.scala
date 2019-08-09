package com.bnd.math.business.rand

import com.bnd.core.NumericConversions._

private class AnyValPositiveNormalDistributionProvider[T <: AnyVal](
        clazz : Class[T],
        normalDistributionProvider : RandomDistributionProvider[T]
	) extends PositiveNormalDistributionProvider[T](clazz, normalDistributionProvider) {

	override def next = {
		// TODO: Potential infinity loop might emerge here!
		var value : T = null.asInstanceOf[T]
		do value = normalDistributionProvider.next while (convertAsNumeric[Double](value) < 0)
		value
	}
}