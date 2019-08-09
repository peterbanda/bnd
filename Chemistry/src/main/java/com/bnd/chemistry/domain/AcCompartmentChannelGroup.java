package com.bnd.chemistry.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AcCompartmentChannelGroup implements Serializable {

	private Long id;
	private Long version = new Long(1);
	private AcCompartment compartment;
	private List<AcCompartmentChannel> channels = new ArrayList<AcCompartmentChannel>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public AcCompartment getCompartment() {
		return compartment;
	}

	public void setCompartment(AcCompartment compartment) {
		this.compartment = compartment;
	}

	public List<AcCompartmentChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<AcCompartmentChannel> channels) {
		this.channels = channels;
	}

	public void addChannel(AcCompartmentChannel channel) {
		channels.add(channel);
	}

	public void removeChannel(AcCompartmentChannel channel) {
		channels.remove(channel);
	}
}