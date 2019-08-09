package com.bnd.core.domain.um;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bnd.core.domain.TechnicalDomainObject;

/**
 * <code>Role</code> is a domain class that represents a single user.
 * 
 * @author © Peter Banda
 * @since 2010
 */
public class User extends TechnicalDomainObject {

    private String username;                    // required; unique
    private String password;                    // required
    private String passwordHint;
    private String firstName;                   // required
    private String lastName;                    // required
    private String email;                       // required; unique
    private String affiliation;                 // required
    private String intendedUse;                 // required
    private String phoneNumber;
    private String website;
    private String address;
    private String aboutMe;
//    private Address address = new Address();
    
    private Set<Role> roles = new HashSet<Role>();
    private boolean accountEnabled = true;
    private boolean accountLocked = false;
    private boolean accountExpired = false;
    private boolean credentialsExpired = false;
	private Date createTime;
	private Date changeTime;

    /**
     * Default constructor - creates a new instance with no values set.
     */
    public User() {
    	// empty body
    }

    /**
     * Create a new instance and set the username.
     * 
     * @param username Login name for user
     */
    public User(final String anUsername) {
        username = anUsername;
    }

	/**
	 * Gets the value of the attribute username.
	 * 
	 * @return The value of the attribute username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the value of the attribute username.
	 *
	 * @param anUsername The value to set.
	 */
	public void setUsername(String anUsername) {
		username = anUsername;
	}

	/**
	 * Gets the value of the attribute password.
	 * 
	 * @return The value of the attribute password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the value of the attribute password.
	 *
	 * @param aPassword The value to set.
	 */
	public void setPassword(String aPassword) {
		password = aPassword;
	}

	/**
	 * Gets the value of the attribute passwordHint.
	 * 
	 * @return The value of the attribute passwordHint
	 */
	public String getPasswordHint() {
		return passwordHint;
	}

	/**
	 * Sets the value of the attribute passwordHint.
	 *
	 * @param aPasswordHint The value to set.
	 */
	public void setPasswordHint(String aPasswordHint) {
		passwordHint = aPasswordHint;
	}

	/**
	 * Gets the value of the attribute firstName.
	 * 
	 * @return The value of the attribute firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the value of the attribute firstName.
	 *
	 * @param aFirstName The value to set.
	 */
	public void setFirstName(String aFirstName) {
		firstName = aFirstName;
	}

	/**
	 * Gets the value of the attribute lastName.
	 * 
	 * @return The value of the attribute lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the value of the attribute lastName.
	 *
	 * @param aLastName The value to set.
	 */
	public void setLastName(String aLastName) {
		lastName = aLastName;
	}

	/**
	 * Gets the value of the attribute email.
	 * 
	 * @return The value of the attribute email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the value of the attribute email.
	 *
	 * @param anEmail The value to set.
	 */
	public void setEmail(String anEmail) {
		email = anEmail;
	}

	/**
	 * Gets the value of the attribute phoneNumber.
	 * 
	 * @return The value of the attribute phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Sets the value of the attribute phoneNumber.
	 *
	 * @param aPhoneNumber The value to set.
	 */
	public void setPhoneNumber(String aPhoneNumber) {
		phoneNumber = aPhoneNumber;
	}

	/**
	 * Gets the value of the attribute website.
	 * 
	 * @return The value of the attribute website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * Sets the value of the attribute website.
	 *
	 * @param aWebsite The value to set.
	 */
	public void setWebsite(String aWebsite) {
		website = aWebsite;
	}

	/**
	 * Sets the value of the attribute address.
	 *
	 * @param anAddress The value to set.
	 */
	public void setAddress(String anAddress) {
		address = anAddress;
	}

	/**
	 * Gets the value of the attribute address.
	 * 
	 * @return The value of the attribute address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Gets the value of the attribute accountEnabled.
	 * 
	 * @return The value of the attribute accountEnabled
	 */
	public boolean getAccountEnabled() {
		return accountEnabled;
	}

	/**
	 * Gets the value of the attribute accountEnabled.
	 * 
	 * @return The value of the attribute accountEnabled
	 */
	public boolean isAccountEnabled() {
		return accountEnabled;
	}

	/**
	 * Sets the value of the attribute accountEnabled.
	 *
	 * @param anEnabled The value to set.
	 */
	public void setAccountEnabled(boolean anEnabled) {
		accountEnabled = anEnabled;
	}

	/**
	 * Gets the value of the attribute accountExpired.
	 * 
	 * @return The value of the attribute accountExpired
	 */
	public boolean isAccountExpired() {
		return accountExpired;
	}

	/**
	 * Sets the value of the attribute accountExpired.
	 *
	 * @param anAccountExpired The value to set.
	 */
	public void setAccountExpired(boolean anAccountExpired) {
		accountExpired = anAccountExpired;
	}

	/**
	 * Gets the value of the attribute accountLocked.
	 * 
	 * @return The value of the attribute accountLocked
	 */
	public boolean isAccountLocked() {
		return accountLocked;
	}

	/**
	 * Sets the value of the attribute accountLocked.
	 *
	 * @param anAccountLocked The value to set.
	 */
	public void setAccountLocked(boolean anAccountLocked) {
		accountLocked = anAccountLocked;
	}

	/**
	 * Gets the value of the attribute credentialsExpired.
	 * 
	 * @return The value of the attribute credentialsExpired
	 */
	public boolean isCredentialsExpired() {
		return credentialsExpired;
	}

	/**
	 * Sets the value of the attribute credentialsExpired.
	 *
	 * @param aCredentialsExpired The value to set.
	 */
	public void setCredentialsExpired(boolean aCredentialsExpired) {
		credentialsExpired = aCredentialsExpired;
	}

	/**
	 * Gets the value of the attribute aboutMe.
	 * 
	 * @return The value of the attribute aboutMe
	 */
	public String getAboutMe() {
		return aboutMe;
	}

	/**
	 * Sets the value of the attribute aboutMe.
	 *
	 * @param anAboutMe The value to set.
	 */
	public void setAboutMe(String anAboutMe) {
		aboutMe = anAboutMe;
	}

	/**
	 * Gets the value of the attribute createTime.
	 * 
	 * @return The value of the attribute createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * Sets the value of the attribute createTime.
	 *
	 * @param aCreateTime The value to set.
	 */
	public void setCreateTime(Date aCreateTime) {
		createTime = aCreateTime;
	}

	/**
	 * Gets the value of the attribute changeTime.
	 * 
	 * @return The value of the attribute changeTime
	 */
	public Date getChangeTime() {
		return changeTime;
	}

	/**
	 * Sets the value of the attribute changeTime.
	 *
	 * @param aChangeTime The value to set.
	 */
	public void setChangeTime(Date aChangeTime) {
		changeTime = aChangeTime;
	}

	/**
	 * Sets the value of the attribute 1.
	 *
	 * @param someRoles The value to set.
	 */
	public void setRoles(Set<Role> someRoles) {
		roles = someRoles;
	}

	/**
	 * Gets the value of the attribute roles.
	 * 
	 * @return The value of the attribute roles
	 */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Checks whether given role is associated or not to the user
     * 
     * @param aRole RoleDO to find
     * @return The result of the test
     */
    public boolean hasRole(Role aRole) {
    	return (aRole == null) ? false : getRoles().contains(aRole);
    }
    
	public boolean getAccountExpired() {
		return accountExpired;
	}

	public boolean getAccountLocked() {
		return accountLocked;
	}

	public boolean getCredentialsExpired() {
		return credentialsExpired;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getIntendedUse() {
		return intendedUse;
	}

	public void setIntendedUse(String intendedUse) {
		this.intendedUse = intendedUse;
	}

	/**
     * Returns the full name.
     * 
     * @return firstName + ' ' + lastName
     */
    public String getFullName() {
        return firstName + ' ' + lastName;
    }

    /**
     * Adds a role for the user
     * 
     * @param role the fully instantiated role
     */
    public void addRole(Role role) {
        getRoles().add(role);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return username;
    }

	public void copyFrom(User userDO) {
		setId(userDO.getId());
		setVersion(userDO.getVersion());
	    setUsername(userDO.getUsername());
	    setPassword(userDO.getPassword());
	    setPasswordHint(userDO.getPasswordHint());
	    setFirstName(userDO.getFirstName());
	    setLastName(userDO.getLastName());
	    setEmail(userDO.getEmail());
	    setPhoneNumber(userDO.getPhoneNumber());
	    setWebsite(userDO.getWebsite());
	    setAddress(userDO.getAddress());
	    setAccountEnabled(userDO.isAccountEnabled());
	    setAccountExpired(userDO.isAccountExpired());
	    setAccountLocked(userDO.isAccountLocked());
	    setCredentialsExpired(userDO.isCredentialsExpired());
		setAboutMe(userDO.getAboutMe());
		setCreateTime(userDO.getCreateTime());
		setChangeTime(userDO.getChangeTime());
		setAffiliation(userDO.getAffiliation());
		setIntendedUse(userDO.getIntendedUse());
	}
}