package com.bnd.network.business

import com.bnd.network.domain.{TopologicalNode, TopologicalNodeLocationComparator}

object TopologicalNodeSorter {

  implicit val scalaOrdering: Ordering[TopologicalNode] = (x: TopologicalNode, y: TopologicalNode) => new TopologicalNodeLocationComparator().compare(x, y)

  def apply(
    components : Iterable[TopologicalNode]
  ) = {
    val componentsAux = components.toSeq
    if (components.head.hasLocation) componentsAux.sorted else componentsAux
  }
}
