package com.bnd.core

import com.bnd.core.ArrayListKeyInnerNode

final class ScalaBooleanArrayListKeyInnerNode[V] extends ArrayListKeyInnerNode[Boolean,V](2) {

	override protected def getArrayIndex(key : Boolean) = if (key) 1 else 0

	override protected def newInstance = new ScalaBooleanArrayListKeyInnerNode[V]
}