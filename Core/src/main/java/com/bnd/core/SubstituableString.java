package com.bnd.core;

/**
 * SubstituableString
 * @author Peter Banda (PEBA)
 */
public interface SubstituableString {

  /**
   * Gets the String with data substitution.
   * 
   * @param psaData The data to substitute
   * @return The String representing text with data substitution
   */
  public String toString(String[] data);
}
