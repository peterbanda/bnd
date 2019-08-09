package com.bnd.chemistry.domain;

import java.util.*;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;
import com.bnd.core.util.ObjectUtil;

public class AcItemHistoriesHolder<I extends AcItemHistory> extends TechnicalDomainObject {

	@Deprecated
	private Integer steps;
	private Date createTime = new Date();
	private User createdBy;

	private Collection<I> itemHistories = new LinkedList<I>();
	private List<Double> timeSteps = new ArrayList<Double>();

	public AcItemHistoriesHolder() {
		super();
	}

	public AcItemHistoriesHolder(Long id) {
		super(id);
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Collection<I> getItemHistories() {
		return itemHistories;
	}

	public void setItemHistories(Collection<I> itemHistories) {
		this.itemHistories = itemHistories;
	}

	public void addItemHistory(I itemHistory) {
		itemHistories.add(itemHistory);
		itemHistory.setHolder(this);
	}

	public void addItemHistories(Collection<I> itemHistories) {
		for (I itemHistory : itemHistories) {
			addItemHistory(itemHistory);
		}
	}

	public void removeItemHistory(I itemHistory) {
		itemHistories.remove(itemHistory);
		itemHistory.setHolder(null);
	}

	public void clearItemHistories() {
		itemHistories.clear();
	}

	public boolean hasItemHistories() {
		return itemHistories != null && !itemHistories.isEmpty();
	}

	@Deprecated
	public void setSteps(Integer steps) {
		this.steps = steps;
	}

	@Deprecated
	public Integer getSteps() {
		if (steps == null) {
			refreshSteps();
		}
		return steps;
	}

	@Deprecated
	public Integer getCurrentSteps() {
		refreshSteps();
		return steps;
	}

	@Deprecated
	public void refreshSteps() {
		I speciesHistory = ObjectUtil.getFirst(itemHistories);
		steps = speciesHistory != null ? speciesHistory.getSequence().size() : 0;
	}

	public List<Double> getTimeSteps() {
		return timeSteps;
	}

	public void setTimeSteps(List<Double> timeSteps) {
		this.timeSteps = timeSteps;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getId());
    	return sb.toString();
    }
}