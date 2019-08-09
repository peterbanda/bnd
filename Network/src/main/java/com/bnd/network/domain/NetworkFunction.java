package com.bnd.network.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bnd.core.domain.MultiStateUpdateType;
import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;
import com.bnd.core.util.ObjectUtil;
import com.bnd.function.domain.AbstractFunction;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class NetworkFunction<T> extends TechnicalDomainObject implements Comparable<NetworkFunction<?>> {

	private String name;
	private Date timeCreated = new Date();
	private User createdBy;

	private Integer index;

	private StatesWeightsIntegratorType statesWeightsIntegratorType;
	private ActivationFunctionType activationFunctionType;
	private List<T> activationFunctionParams;

	// an explicit function (if the type is not provided)
	private AbstractFunction<T, T> function;

	private MultiStateUpdateType multiComponentUpdaterType;

	private NetworkFunction<T> parentFunction;
	private List<NetworkFunction<T>> layerFunctions = new ArrayList<NetworkFunction<T>>();

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

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public AbstractFunction<T, T> getFunction() {
		return function;
	}

	public void setFunction(AbstractFunction<T, T> function) {
		this.function = function;
	}

	public StatesWeightsIntegratorType getStatesWeightsIntegratorType() {
		return statesWeightsIntegratorType;
	}

	public void setStatesWeightsIntegratorType(StatesWeightsIntegratorType statesWeightsIntegratorType) {
		this.statesWeightsIntegratorType = statesWeightsIntegratorType;
	}

	public ActivationFunctionType getActivationFunctionType() {
		return activationFunctionType;
	}

	public void setActivationFunctionType(ActivationFunctionType activationFunctionType) {
		this.activationFunctionType = activationFunctionType;
	}

	public MultiStateUpdateType getMultiComponentUpdaterType() {
		return multiComponentUpdaterType;
	}

	public void setMultiComponentUpdaterType(MultiStateUpdateType multiComponentUpdaterType) {
		this.multiComponentUpdaterType = multiComponentUpdaterType;
	}
	
	public List<NetworkFunction<T>> getLayerFunctions() {
		return layerFunctions;
	}

	public void setLayerFunctions(List<NetworkFunction<T>> layerFunctions) {
		this.layerFunctions = layerFunctions;
	}

	public NetworkFunction<T> getParentFunction() {
		return parentFunction;
	}

	protected void setParentFunction(NetworkFunction<T> parentFunction) {
		this.parentFunction = parentFunction;
	}

	public void addLayerFunction(NetworkFunction<T> layerFunction){
		layerFunctions.add(layerFunction);
		layerFunction.setParentFunction(this);
	}

	public void removeLayerFunction(NetworkFunction<T> layerFunction) {
		layerFunctions.remove(layerFunction);
		layerFunction.setParentFunction(null);
	}

	public List<T> getActivationFunctionParams() {
		return activationFunctionParams;
	}

	public void setActivationFunctionParams(List<T> activationFunctionParams) {
		this.activationFunctionParams = activationFunctionParams;
	}

	@Override
	public int compareTo(NetworkFunction<?> netFun) {
		return ObjectUtil.compareObjects(index, netFun.getIndex());
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