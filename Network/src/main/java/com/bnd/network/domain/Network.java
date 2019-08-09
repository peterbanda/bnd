package com.bnd.network.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class Network<T> extends TechnicalDomainObject {

	private String name;
	private Date timeCreated = new Date();
	private User createdBy;

	private Topology topology;
	private NetworkFunction<T> function;
	private NetworkWeightSetting<T> weightSetting;
	private T defaultBiasState;
  private T defaultNonBiasState;

	private Set<NetworkType> netTypes = new HashSet<NetworkType>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Topology getTopology() {
		return topology;
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
	}

	public NetworkWeightSetting<T> getWeightSetting() {
		return weightSetting;
	}

	public void setWeightSetting(NetworkWeightSetting<T> weightSetting) {
		this.weightSetting = weightSetting;
	}

	public NetworkFunction<T> getFunction() {
		return function;
	}

	public void setFunction(NetworkFunction<T> function) {
		this.function = function;
	}

	public Set<NetworkType> getNetTypes() {
		return netTypes;
	}

	protected void setNetTypes(Set<NetworkType> netTypes) {
		this.netTypes = netTypes;
	}

	public void addNetType(NetworkType netType) {
		netTypes.add(netType);
		netType.addNetDefinition(this);
	}

	public void removeNetType(NetworkType netType) {
		netTypes.remove(netType);
		netType.addNetDefinition(null);
	}

	public Class<T> getStateClazz() {
		return function != null ? function.getFunction().getInputClazz() : null;
	}

	public T getDefaultBiasState() {
		return defaultBiasState;
	}

	public void setDefaultBiasState(T defaultBiasState) {
		this.defaultBiasState = defaultBiasState;
	}

	public boolean hasDefaultBiasState() {
		return defaultBiasState != null;
	}

  public T getDefaultNonBiasState() {
    return defaultNonBiasState;
  }

  public void setDefaultNonBiasState(T defaultNonBiasState) {
    this.defaultNonBiasState = defaultNonBiasState;
  }

  public boolean hasDefaultNonBiasState() {
    return defaultNonBiasState != null;
  }

  /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if (name != null) {
    		sb.append(name);
    	} else {
    		sb.append("<no name>");
    	}
    	return sb.toString();
    }
}