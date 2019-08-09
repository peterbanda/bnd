package com.bnd.math.business.dynamics

import org.junit.Test

class UnboundDetectionProcessorTest {

    private val processor = new UnboundDetectionProcessor[Double](Double.NegativeInfinity, Double.PositiveInfinity)

    @Test
    def testProcessor {
        val states = Seq(
            Seq(1.1, 2.2, 3.4),
            Seq(4.5, 8.6, 10.1),
            Seq(Double.NegativeInfinity, 2.1, 12.8),
            Seq(-1.5, Double.PositiveInfinity, 12.9)
        )

        val unboundFlags = processor.process(states, 1D)
        println(unboundFlags)
    }
}