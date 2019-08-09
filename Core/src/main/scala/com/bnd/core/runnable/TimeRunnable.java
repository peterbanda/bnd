package com.bnd.core.runnable;

/**
 * @author © Peter Banda
 * @since 2013
 */
public interface TimeRunnable {

    void runFor(scala.math.BigDecimal timeDiff);

    void runUntil(scala.math.BigDecimal time);

    scala.math.BigDecimal nextTimeStepSize();

    scala.math.BigDecimal currentTime();
}