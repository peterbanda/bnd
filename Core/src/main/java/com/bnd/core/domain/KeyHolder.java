package com.bnd.core.domain;

/**
 * <p>Title: KeyHolder </p>
 * <p>Description: The interface defining an object holding key.</p>
 *
 * @author © Peter Banda
 * @since 2008
 */
public interface KeyHolder<KEY> {

  /**
   * Gets the key associated to the object.
   *
   * @return The key associated to the object
   */
  public KEY getKey();

  /**
   * Sets the key that should be associated with the object.
   *
   * @param key The key to set
   */
  public void setKey(KEY key);
}