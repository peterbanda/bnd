package com.bnd.core.domain;

import java.io.Serializable;
import java.util.Arrays;

import com.bnd.core.BndRuntimeException;

/** 
 * @author © Peter Banda
 * @since 2011
 */
public class ValueBound<E> implements Serializable {

	private E from;
	private E to;
	private E[] enumeratedOrderedValues;
	private Integer length;

	public ValueBound() {
		// no-op
	}

	public ValueBound(E from, E to) {
		this.from = from;
		this.to = to;
		this.enumeratedOrderedValues = null; 
	}

	public ValueBound(E[] enumeratedOrderedValues) {
		this.enumeratedOrderedValues = enumeratedOrderedValues;
		this.from = enumeratedOrderedValues[0];
		this.to = enumeratedOrderedValues[enumeratedOrderedValues.length - 1];
	}

	public ValueBound(Integer length) {
		this.length = length;
	}

	public E getFrom() {
		return from;
	}

	public void setFrom(E from) {
		this.from = from;
	}

	public E getTo() {
		return to;
	}

	public void setTo(E to) {
		this.to = to;
	}

	public E[] getEnumeratedOrderedValues() {
		return enumeratedOrderedValues;
	}

	public void setEnumeratedOrderedValues(E[] enumeratedOrderedValues) {
		this.enumeratedOrderedValues = enumeratedOrderedValues;
	}

	public boolean hasEnumeratedValues() {
		return enumeratedOrderedValues != null;
	}

	public int getEnumeratedOrderedValuesNum() {
		return hasEnumeratedValues() ? enumeratedOrderedValues.length : 0;
	}

	public Integer getEnumeratedValueIndex(E value) {
		if (!hasEnumeratedValues()) {
			throw new BndRuntimeException("Value bound is not enumerated!");
		}
		int index = 0;
		for (E enumeratedValue : enumeratedOrderedValues) {
			if (enumeratedValue == value) {
				return index;
			}
			index++;
		}
		return null;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (from != null) {
//			sb.append("From-To Type: ");
			sb.append(from);
			sb.append(" - ");
			sb.append(to);
		} else if (hasEnumeratedValues()) {
			sb.append("Enum Type: ");
			sb.append(Arrays.toString(enumeratedOrderedValues));
		} else if (length != null) {
			sb.append("Length Type: ");
			sb.append(length);
		} else {
			sb.append("Empty");
		}
		return sb.toString();
	}
}