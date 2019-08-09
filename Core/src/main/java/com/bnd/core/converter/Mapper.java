package com.bnd.core.converter;

/**
 * @author © Peter Banda
 * @since 2012   
 */
public interface Mapper<S, T> {

  void map(S src, T target);

  void remap(T src, S target);
}