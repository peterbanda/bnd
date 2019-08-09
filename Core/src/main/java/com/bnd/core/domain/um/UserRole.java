package com.bnd.core.domain.um;

import java.io.Serializable;

import com.bnd.core.util.ObjectUtil;

/**
 * <code>UserRole</code> is a simple association of user to role.
 * 
 * @author © Peter Banda
 * @since 2010
 */
public class UserRole implements Serializable {

	private User user;
	private Role role;

	public boolean equals(Object other) {
		if (!(other instanceof UserRole)) {
			return false;
		}

		UserRole userRole = (UserRole) other;
		return ObjectUtil.areObjectsEqual(user, userRole.user)
				&& ObjectUtil.areObjectsEqual(role, userRole.role);
	}

	public int hashCode() {
		return ObjectUtil.getHashCode(new Object[] {user.getId(), role.getId()});
	}
}
