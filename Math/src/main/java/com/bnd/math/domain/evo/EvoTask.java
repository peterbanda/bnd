package com.bnd.math.domain.evo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;

public class EvoTask extends TechnicalDomainObject {

	private String name;
	private Date createTime = new Date();
	private User createdBy;
	private EvoTaskType taskType;
	private EvoGaSetting gaSetting;

	private Collection<EvoRun<?>> evolutionRuns = new ArrayList<EvoRun<?>>();

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

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public EvoTaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(EvoTaskType taskType) {
		this.taskType = taskType;
	}

	public EvoGaSetting getGaSetting() {
		return gaSetting;
	}

	public void setGaSetting(EvoGaSetting gaSetting) {
		this.gaSetting = gaSetting;
	}

	public Collection<EvoRun<?>> getEvolutionRuns() {
		return evolutionRuns;
	}

	public void setEvolutionRuns(Collection<EvoRun<?>> evolutionRuns) {
		this.evolutionRuns = evolutionRuns;
	}

	public void addEvolutionRun(EvoRun<?> evolutionRun) {
		initEvolutionRuns();
		evolutionRun.setEvoTask(this);
		evolutionRuns.add(evolutionRun);
	}

	public void removeEvolutionRun(EvoRun<?> evolutionRun) {
		initEvolutionRuns();
		evolutionRun.setEvoTask(null);
		evolutionRuns.remove(evolutionRun);
	}

	private void initEvolutionRuns() {
		if (evolutionRuns == null) {
			evolutionRuns = new ArrayList<EvoRun<?>>();
		}
	}
}