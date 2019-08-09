package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible
import com.bnd.core.DoubleConvertible.Implicits._

class FixedPointDetectionProcessor[T : DoubleConvertible]
	(val detectionPrecision : Double) extends SimplePrevStateProcessor[T, Boolean](
	(state, prevState, timeStepLength) => Math.abs(state - prevState) / timeStepLength < detectionPrecision) {
}