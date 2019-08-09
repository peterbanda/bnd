package com.bnd.function.enumerator;

import java.util.List;

/**
 * @author © Peter Banda
 * @since 2013 
 */
public interface ListEnumeratorFactory {

	public <T> ListEnumerator<T> createInstance(
		Boolean allowRepetitions,
		T rangeFrom,
		T rangeTo
	);

	public <T> ListEnumerator<T> createInstance(
		Boolean allowRepetitions,
		List<T> rangeFrom,
		List<T> rangeTo
	);

	public <T> ListEnumerator<T> createInstance(
		Boolean allowRepetitions,
		List<T> values
	);
}