package com.bnd.core.domain;

/**
 * <code>TechnicalDomainObject</code> is a domain object with the technical (long-typed) key
 * and the ol (optimistic locking) version.
 *
 * @author © Peter Banda
 * @since 2012
 */
public abstract class TechnicalDomainObject extends DomainObject<Long> {

	/**
	 * Technical id. 
	 */
	private Long id;

	/**
	 * Optimistic locking version. 
	 */
	private Long version = new Long(1);

	/**
	 * Constructor
	 */
	public TechnicalDomainObject() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public TechnicalDomainObject(Long id) {
		this();
		this.id = id;
	}

	/**
	 * Gets the value of the attribute id.
	 * 
	 * @return The value of the attribute id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the value of the attribute id.
	 *
	 * @param anId The value to set.
	 */
	public void setId(Long anId) {
		id = anId;
	}

	/**
	 * Gets the value of the attribute olVersion.
	 * 
	 * @return The value of the attribute olVersion
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * Sets the value of the attribute olVersion.
	 *
	 * @param anOlVersion The value to set.
	 */
	public void setVersion(Long anOlVersion) {
		version = anOlVersion;
	}

	/**
	 * @see KeyHolder#getKey()
	 */
	@Override
	public Long getKey() {
		return getId();
	}

	/**
	 * @see KeyHolder#setKey(java.lang.Object)
	 */
	@Override
	public void setKey(Long aKey) {
		setId(aKey);		
	}
}