package com.bnd.function.enumerator;

import java.util.Collection;
import java.util.List;

/**
 * @author © Peter Banda
 * @since 2013  
 */
public interface ListEnumerator<T> {

	Collection<List<T>> enumerate(int listLength); 

	Collection<List<T>> enumerate(int listLengthFrom, int listLengthTo);
}