package com.bnd.network.business;

import com.bnd.network.domain.TopologicalNode;

trait WeightAccessible[T] {

	def setWeight(start : TopologicalNode, end : TopologicalNode, value : T)

	def getWeight(start : TopologicalNode, end : TopologicalNode) : Option[T]
}

final class WeightAccessibleImpl[T](nodeInWeightMap : Map[TopologicalNode, InWeightAccessible[T]]) extends WeightAccessible[T] {

	override def setWeight(start : TopologicalNode, end : TopologicalNode, value : T) = {
    val node = nodeInWeightMap.get(end)
    if (node.isDefined) node.get.setWeight(start, value)
  }

	override def getWeight(start : TopologicalNode, end : TopologicalNode) = {
    val node = nodeInWeightMap.get(end)
    if (node.isDefined) {
      val weight = node.get.getWeight(start)
      if (weight != null) Some(weight) else None
    } else None
  }
}