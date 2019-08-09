package com.bnd.math.business.dynamics

import org.junit.Test
import com.bnd.core.DoubleConvertible._

class NonlinearityProcessorTest {

  private val processor = new NonlinearityProcessor[Double](true)

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