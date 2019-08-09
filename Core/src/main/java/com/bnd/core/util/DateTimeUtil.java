package com.bnd.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class DateTimeUtil {

	private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String ZULU_TIME_ZONE_CODE = "Zulu";
	private static final DateFormat DEFAULT_LOCAL_FOTMATTER = new SimpleDateFormat(DEFAULT_FORMAT);
	private static final DateFormat DEFAULT_ZULU_FOTMATTER = createFormatter(DEFAULT_FORMAT, ZULU_TIME_ZONE_CODE);

	private static final int[] TIME_FIELDS_IN_ORDER = new int[] {
		Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND};
	
	protected DateTimeUtil() {
		// no-op
	}

	/**
	 * Use a container instantiation instead.
	 * 
	 * @return
	 */
	@Deprecated
	public static DateTimeUtil createInstance() {
		return new DateTimeUtil();
	}

	public Date parseToDate(String dateTimeString) {
		try {
			return DEFAULT_LOCAL_FOTMATTER.parse(dateTimeString);
		} catch (ParseException e) {
			throw new BndParseException("Date time '" + dateTimeString + "' can't be parsed using format '" + DEFAULT_FORMAT + "'.", e);
		}
	}

	public Date parseToZuluDate(String dateTimeString) {
		try {
			return DEFAULT_ZULU_FOTMATTER.parse(dateTimeString);
		} catch (ParseException e) {
			throw new BndParseException("Date time '" + dateTimeString + "' can't be parsed using format '" + DEFAULT_FORMAT + "'.", e);
		}
	}

	public Date parseToDate(String dateTimeString, String dateTimeFormat, String timeZone) {
		try {
			return createFormatter(dateTimeFormat, timeZone).parse(dateTimeString);
		} catch (ParseException e) {
			throw new BndParseException("Date time '" + dateTimeString + "' can't be parsed using format '" + dateTimeFormat + "'.", e);
		}
	}

	public Calendar parseToCal(String dateTimeString) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(parseToDate(dateTimeString));
		return cal;
	}

	public Calendar parseToZuluCal(String dateTimeString) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(parseToZuluDate(dateTimeString));
		return cal;
	}

	public Calendar parseToCal(String dateTimeString, String dateTimeFormat, String timeZone) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(parseToDate(dateTimeString, dateTimeFormat, timeZone));
		return cal;
	}

	public XMLGregorianCalendar parseToXMLCal(String dateTimeString) {
		return toXMLGregorianCalendar(parseToDate(dateTimeString));
	}

	public XMLGregorianCalendar parseToZuluXMLCal(String dateTimeString) {
		return toXMLGregorianCalendar(parseToZuluDate(dateTimeString));
	}

	public XMLGregorianCalendar parseToXMLCal(String dateTimeString, String dateTimeFormat, String timeZone) {
		return toXMLGregorianCalendar(parseToDate(dateTimeString, dateTimeFormat, timeZone));
	}

	public String format(Date date) {
		return DEFAULT_LOCAL_FOTMATTER.format(date);
	}

	public String format(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}

	public String format(Calendar cal) {
		return format(cal.getTime());
	}

	public String format(XMLGregorianCalendar xmlCal) {
		return format(toDate(xmlCal));
	}

	public String formatToZulu(Date date, String pattern) {
		return createZuluFormatter(pattern).format(date);
	}

	public String formatToZulu(Calendar cal, String pattern) {
		return createZuluFormatter(pattern).format(cal.getTime());
	}

	public String formatToZulu(XMLGregorianCalendar xmlCal, String pattern) {
		return formatToZulu(toDate(xmlCal), pattern);
	}

	public Date fromLocalToZulu(Date date) {
		String localDateString = format(date);
		return parseToZuluDate(localDateString);
	}

	public XMLGregorianCalendar createXMLGregorianCalendar() {
		return toXMLGregorianCalendar(new Date());
	}

	public XMLGregorianCalendar toXMLGregorianCalendar(Date time) {
		XMLGregorianCalendar xmlCal = null;
		try {
			TimeZone utcTimezone = TimeZone.getTimeZone("GMT");	
			GregorianCalendar cal = new GregorianCalendar(utcTimezone);
			cal.setTime(time);
			DatatypeFactory dtf = DatatypeFactory.newInstance();
			xmlCal = dtf.newXMLGregorianCalendar(cal);			
		} catch (DatatypeConfigurationException e) {
			throw new BndConversionException("Error occurred while creating a XML gregorian calendar");
		}
		return xmlCal;
	}

	public Date toDate(XMLGregorianCalendar xmlCal) {
		return xmlCal.toGregorianCalendar().getTime();
	}

	public Double getTimeFieldFractional(Calendar cal, int timeCalField) {
		double msSum = 0;
		boolean found = false;
		for (int timeField : TIME_FIELDS_IN_ORDER) {
			if (!found && timeField == timeCalField) {
				found = true;
			}
			if (found) {
				final int value = cal.get(timeField);
				msSum += convertToMs(value, timeField);	
			}
		}
		if (!found) {
			return null;
		}

		final long timeCalFieldInMs = convertToMs(1, timeCalField);
		return msSum / (double) timeCalFieldInMs;
	}

	public long getTimeIntervalLengthMs(Date startDate, Date endDate) {
		Calendar startCal = GregorianCalendar.getInstance();
		startCal.setTime(startDate);

		Calendar endCal = GregorianCalendar.getInstance();
		endCal.setTime(endDate);

		return endCal.getTimeInMillis() - startCal.getTimeInMillis();
	}

	public Date addToDate(Date date, int calField, int value) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.add(calField, value);
		return cal.getTime();
	}

	public Date addMsToDate(Date date, int value) {
		return addToDate(date, Calendar.MILLISECOND, value);
	}

	private long convertToMs(int value, int calField) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeInMillis(0);
		cal.add(calField, value);
		return cal.getTimeInMillis();
	}

	private DateFormat createZuluFormatter(String pattern) {
		return createFormatter(pattern, ZULU_TIME_ZONE_CODE);
	}

	private static DateFormat createFormatter(String pattern, String timeZoneCode) {
		DateFormat dateFormatter = new SimpleDateFormat(pattern);
		dateFormatter.setTimeZone(TimeZone.getTimeZone(timeZoneCode));
		return dateFormatter;
	}
} 