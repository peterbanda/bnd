package com.bnd.math.business.dynamics

import org.junit.Test
import com.bnd.core.DoubleConvertible._

class TurningPointCountProcessorTest {

  private val processor = new TurningPointCountProcessor[Double](0.00001, 0.1, 12.8)

  @Test
  def testProcessor {
    val states = Seq(
      Seq(1.1, 2.2, 2.2, 3.4),
      Seq(1.1, 2.2, 2.0, 2.03),
      Seq(4.5, 8.6, 8.6000001, 8.55),
      Seq(12.8, 4.5, 4.5, 12.7, 11.2),
      Seq(5.1, 2.2, 3.4, 3.4)
    )

    val turningPoints = processor.process(states, 0.5D)
    turningPoints.map(println(_))
  }
}