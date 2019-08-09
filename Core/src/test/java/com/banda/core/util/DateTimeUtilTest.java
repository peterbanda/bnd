package com.bnd.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.bnd.core.util.DateTimeUtil;
import com.bnd.core.util.RandomUtil;
import junit.framework.TestCase;

import org.junit.Test;

public class DateTimeUtilTest extends TestCase {

	private static final double PRECISION = 0.000001;

	DateTimeUtil dtUtil = new DateTimeUtil();

	@Test
	public void testGetTimeFieldFractional() {
		final int hour = RandomUtil.nextInt(24);
		final int minute = RandomUtil.nextInt(60);
		final int second = RandomUtil.nextInt(60);
		final int ms = RandomUtil.nextInt(1000);

		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, ms);

		double hoursFractional = dtUtil.getTimeFieldFractional(cal, Calendar.HOUR_OF_DAY);
		assertEqualsWithPrecision(hour + ((double) minute/60) + ((double)second/3600) + ((double)ms/3600000), hoursFractional);
	}

	@Test
	public void testGetTimeIntervalLengthMs() {
		final long diffMs = 200;
		final Date startDate = new Date();

		try {
			Thread.sleep(diffMs);
		} catch (InterruptedException e) {
			// no-op
		}

		final Date endDate = new Date();

		long lengthSelf = dtUtil.getTimeIntervalLengthMs(startDate, startDate);
		long length = dtUtil.getTimeIntervalLengthMs(startDate, endDate);
		long lengthInverse = dtUtil.getTimeIntervalLengthMs(endDate, startDate);

		assertEquals(0, lengthSelf);
		assertEqualsWithPrecision(diffMs, length);
		assertEquals(lengthInverse, -length);
	}

	private static void assertEqualsWithPrecision(double number1, double number2) {
		assertTrue(Math.abs(number1 - number2) < PRECISION);
	}

	private static void assertEqualsWithPrecision(int number1, int number2) {
		assertTrue(Math.abs(number1 - number2) < 2);
	}
}