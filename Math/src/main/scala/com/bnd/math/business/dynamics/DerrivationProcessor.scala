package com.bnd.math.business.dynamics

import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible
import com.bnd.core.DoubleConvertible.Implicits._

class DerrivationProcessor[T: DoubleConvertible] extends SimplePrevStateProcessor[T, Double](
    (state, prevState, timeStepLength) =>
        (state - prevState) / timeStepLength)