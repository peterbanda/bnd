package com.bnd.network.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bnd.core.domain.TechnicalDomainObject;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class NetworkType extends TechnicalDomainObject {

	private String name;
	private Date createTime;

	private NetworkType superType;
	private Set<NetworkType> subTypes = new HashSet<NetworkType>();
	private Set<Network<?>> netDefinitions = new HashSet<Network<?>>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public NetworkType getSuperType() {
		return superType;
	}

	public void setSuperType(NetworkType superType) {
		this.superType = superType;
	}

	public Set<NetworkType> getSubTypes() {
		return subTypes;
	}

	protected void setSubTypes(Set<NetworkType> subTypes) {
		this.subTypes = subTypes;
	}

	public void addSubType(NetworkType subType) {
		subTypes.add(subType);
		subType.setSuperType(this);
	}

	public void removeSubType(NetworkType subType) {
		subTypes.remove(subType);
		subType.setSuperType(null);
	}

	public Set<Network<?>> getNetDefinitions() {
		return netDefinitions;
	}

	protected void setNetDefinitions(Set<Network<?>> netDefinitions) {
		this.netDefinitions = netDefinitions;
	}

	protected void addNetDefinition(Network<?> netDefinition) {
		netDefinitions.add(netDefinition);
	}

	protected void removeNetDefinition(Network<?> netDefinition) {
		netDefinitions.remove(netDefinition);
	}
}