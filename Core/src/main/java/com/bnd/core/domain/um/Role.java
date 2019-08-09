package com.bnd.core.domain.um;

import com.bnd.core.domain.TechnicalDomainObject;

/**
 * <code>Role</code> is a domain class that represents user role.
 * 
 * @author © Peter Banda
 * @since 2010
 */
public class Role extends TechnicalDomainObject {
    
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private String name;
    private String description;

    /**
     * Default constructor - creates a new instance with no values set.
     */
    public Role() {
    	// empty body
    }

    /**
     * Create a new instance and set the name.
     * 
     * @param aName name of the role.
     */
    public Role(final String aName) {
        name = aName;
    }
    
    /**
	 * Gets the value of the attribute name.
	 * 
	 * @return The value of the attribute name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the attribute name.
	 *
	 * @param aName The value to set.
	 */
	public void setName(String aName) {
		name = aName;
	}

	/**
	 * Gets the value of the attribute description.
	 * 
	 * @return The value of the attribute description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the value of the attribute description.
	 *
	 * @param aDescription The value to set.
	 */
	public void setDescription(String aDescription) {
		description = aDescription;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
    	return name;
    }
}