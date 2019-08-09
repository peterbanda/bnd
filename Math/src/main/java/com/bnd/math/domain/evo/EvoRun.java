package com.bnd.math.domain.evo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;
import com.bnd.core.util.ObjectUtil;

/**
 * The domain object for evolution.
 */
public class EvoRun<C> extends TechnicalDomainObject {

	private Date timeCreated;
	private User createdBy;

	private EvoTask evoTask;
	private C initChromosome;
	private List<Population<C>> populations = new ArrayList<Population<C>>();

	public EvoRun() {
		super();
		this.timeCreated = new Date();
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

	public EvoTask getEvoTask() {
		return evoTask;
	}

	protected void setEvoTask(EvoTask evoTask) {
		this.evoTask = evoTask;
	}

	public List<Population<C>> getPopulations() {
		return populations;
	}

	public void setPopulations(List<Population<C>> populations) {
		this.populations = populations;
	}

	public void addPopulation(Population<C> population) {
		initPopulations();
		population.setEvolutionRun(this);
		populations.add(population);
	}

	public void removePopulation(Population<C> population) {
		initPopulations();
		population.setEvolutionRun(null);
		populations.remove(population);
	}

	public C getInitChromosome() {
		return initChromosome;
	}

	public void setInitChromosome(C initChromosome) {
		this.initChromosome = initChromosome;
	}

	public Population<C> getLastPopulation() {
		return ObjectUtil.getLast(populations);
	}

	private void initPopulations() {
		if (populations == null) {
			populations = new ArrayList<Population<C>>();
		}
	}

	public boolean isDone() {
		if (populations.isEmpty()) {
			return false;
		}
		return ObjectUtil.areObjectsEqual(evoTask.getGaSetting().getGenerationLimit(), getLastPopulation().getGeneration());
	}
}