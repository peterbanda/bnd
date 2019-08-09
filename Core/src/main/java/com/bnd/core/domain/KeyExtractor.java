package com.bnd.core.domain;

/**
 * <code>KeyExtractor</code>
 * Description: The interface defining an object holding key.
 *
 * @author © Peter Banda
 * @since 2010
 */
public interface KeyExtractor<KEY, O> {

  /**
   * Gets the key associated to the object.
   *
   * @return The key associated to the object
   */
  public KEY getKey(O object);

  /**
   * Sets the key to a given object.
   *
   * @param object The object to set the key for
   */
  public void setKey(O object, KEY key);
}