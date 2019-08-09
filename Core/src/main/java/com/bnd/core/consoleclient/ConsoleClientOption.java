package com.bnd.core.consoleclient;

import java.util.Collection;

import com.bnd.core.util.BndConversionException;
import com.bnd.core.util.ConversionUtil;

/**
 * ConsoleClientOption is a data holder of console client's option having attributes as
 * flag, name, description, default value etc.
 * 
 * @author © Peter Banda
 * @since 2011   
 */
public class ConsoleClientOption<T> {

	private static final int MAX_PADDING_SPACE_LENGTH = 10; 

	private String flag;
	private String name;
	private String description;
	private Class<T> type;
	private Class<?> collectionSubType;
	private T defaultValue;
	private boolean mandatory;

	public ConsoleClientOption(String flag, String name, Class<T> type, T defaultValue, String description) {
		this(flag, name, type, null, defaultValue, description);
	}

	public ConsoleClientOption(String flag, String name, Class<T> type, T defaultValue, String description, boolean mandatory) {
		this(flag, name, type, null, defaultValue, description, mandatory);
	}

	public ConsoleClientOption(String flag, String name, Class<T> type, Class<?> collectionSubType, T defaultValue, String description) {
		this.flag = flag;
		this.name = name;
		this.type = type;
		this.collectionSubType = collectionSubType;
		this.defaultValue = defaultValue;
		this.description = description;
	}

	public ConsoleClientOption(String flag, String name, Class<T> type, Class<?> collectionSubType, T defaultValue, String description, boolean mandatory) {
		this(flag, name, type, collectionSubType, defaultValue, description);
		this.mandatory = mandatory;
	}

	public T getConvertedValue(String value) {
		T result = null;
		try {
			if (type == Collection.class) {
				result = (T) ConversionUtil.convertToList(collectionSubType, value, name);
			} else {
				result = ConversionUtil.convert(type, value, name);
			}
		} catch (BndConversionException e) {
			throw new ConsoleClientException("Conversion problem occured.", e);
		}
		return result;
	}

	public boolean hasDefaultValue() {
		return defaultValue != null;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public String getFlag() {
		return flag;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Class<T> getType() {
		return type;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public Class<?> getCollectionSubType() {
		return collectionSubType;
	}

	public String toString() {
		int paddingLength = MAX_PADDING_SPACE_LENGTH - flag.length();
		StringBuffer sb = new StringBuffer();
		sb.append("-");
		sb.append(flag);
		sb.append(getStringWithSpace(paddingLength));
		sb.append(": ");
		sb.append(description);		
		if (hasDefaultValue()) {
			sb.append(" (default ");
			sb.append(defaultValue);
			sb.append(")");
		}
		return sb.toString();
	}

	private static String getStringWithSpace(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}
}