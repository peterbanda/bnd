package com.bnd.math.business.dynamics

import org.junit.Test
import com.bnd.core.DoubleConvertible._

class CumulativeDiffPerTimeProcessorTest {

  private val STATES = 100
  private val processor = new CumulativeDiffPerTimeProcessor[Double]

  @Test
  def testProcessor {
    val states = Seq(
      Seq(1.1, 2.2, 3.4),
      Seq(4.5, 8.6, 10.1),
      Seq(-1.5, 2.1, 12.8)
    )
    //        val states = List.fill(STATES, STATES)(if (Random.nextBoolean) Random.nextDouble else -Random.nextDouble)
    val cumDiffs = processor.process(states, 0.5D)
    cumDiffs.map(println(_))
  }
}