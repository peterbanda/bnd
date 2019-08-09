package com.bnd.core.domain.runnable;

import java.util.Date;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;

public abstract class BaseManipulationSeries extends TechnicalDomainObject {

	private String name;
	private Date timeCreated = new Date();
	private User createdBy;

	private Integer repetitions;
	private Integer periodicity;
	private Integer repeatFromElement;

	public BaseManipulationSeries() {
		super();
	}

	public BaseManipulationSeries(Long id) {
		super(id);
	}

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

	public boolean isWithinRepetition(int repetition) {
		return repetitions == null || repetition < repetitions;
	}

	public Integer getRepetitions() {
		return repetitions;
	}

	public boolean hasRepetitions() {
		return repetitions != null;
	}

	public void setRepetitions(Integer repetitions) {
		this.repetitions = repetitions;
	}

	public boolean isPeriodic() {
		return periodicity != null;
	}

	public Integer getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(Integer periodicity) {
		this.periodicity = periodicity;
	}

	public Integer getRepeatFromElement() {
		return repeatFromElement;
	}

	public int getRepeatFromElementSafe() {
		return repeatFromElement != null ? repeatFromElement : 0;
	}

	public void setRepeatFromElement(Integer repeatFromElement) {
		this.repeatFromElement = repeatFromElement;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(name);
    	return sb.toString();
    }
}