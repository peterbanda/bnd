package com.bnd.core.converter;

/**
 * @author © Peter Banda
 * @since 2012   
 */
public interface Converter<S, T> {

  T convert(S src);

  S reconvert(T src);
}