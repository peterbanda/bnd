package com.bnd.core.domain;

import java.io.Serializable;
import java.util.Comparator;

import com.bnd.core.util.ObjectUtil;

/**
 * <p>Title: DomainObject </p>
 * <p>Description: The domain object that is reflected in the database.</p>
 *
 * @param <KEY> The key of the domain object  
 * @author © Peter Banda
 * @since 2008   
 */
public abstract class DomainObject<KEY extends Serializable & Comparable<KEY>> implements KeyHolder<KEY>, Serializable {

	public static class DomainObjectKeyComparator<K extends Serializable & Comparable<K>> implements Comparator<DomainObject<K>> {

		@Override
		public int compare(DomainObject<K> do1, DomainObject<K> do2) {
			return ObjectUtil.compareObjects(do1.getKey(), do2.getKey());
		}
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object object) {
		if (object == null || !(object instanceof DomainObject)) {
			return false;
		}
		DomainObject<KEY> domainObject = (DomainObject<KEY>) object;
		return this == domainObject
				|| (ObjectUtil.areObjectsEqual(getClass(), domainObject.getClass())
						&& ObjectUtil.areObjectsNotNullAndEqual(getKey(), domainObject.getKey()));
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getKey() != null ? getKey().hashCode() : super.hashCode();
	}

	/**
	 * Copies the attributes from the given value object.
	 * 
	 * @param domainObject The value object to copy from
	 */
	public void copyFrom(DomainObject<KEY> domainObject) {
		setKey(domainObject.getKey());
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
		final KEY key = getKey();
		return key != null ? key.toString() : null;
    }
}