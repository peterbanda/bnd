package com.bnd.math.business.rand

import com.bnd.math.domain.rand.BooleanDensityUniformDistribution
import scala.collection.JavaConversions._

object RandomDistributionTest extends App {

  val size = 20
  val repetitions = 100000
  val counts = new Array[Int](size + 1)
  val indexOccurences = new Array[Int](size)
  val rdp = RandomDistributionProviderFactory.apply(new BooleanDensityUniformDistribution)

  for (i <- 0 to repetitions) {
    var count = 0

    rdp.nextList(size).toSeq.zipWithIndex.map { case (value, index) =>
      if (value) {
        count += 1
        indexOccurences(index) += 1
      }
    }

    counts(count) += 1
  }

  counts.zipWithIndex.foreach { case (count, i) =>
    println(i + ":" + count)
  }
  println

  indexOccurences.zipWithIndex.foreach { case (occurence, i) =>
    println(i + ":" + occurence)
  }
}
