package com.bnd.math.business.dynamics

import org.junit.Test
import com.bnd.core.DoubleConvertible._

class NonlinearityLastProcessorTest {

  private val processor = new NonlinearityLastProcessor[Double](true, 12)

  @Test
  def testProcessor {
    val states = Seq(
      Seq(1.1, 2.2, 3.4),
      Seq(4.5, 8.6, 10.1),
      Seq(7.9, 8.6, 12.8),
      Seq(7.9, 8.6, 12.8)
    )

    //        val states = List.fill(STATES, STATES)(if (Random.nextBoolean) Random.nextDouble else -Random.nextDouble)
    val linErrors = processor.process(states, 0.5D)
    linErrors.map(println)
  }
}