package com.bnd.math.task;

import java.util.Collection;

import com.bnd.core.domain.task.Task;
import com.bnd.math.domain.evo.Chromosome;
import com.bnd.math.domain.evo.EvoGaSetting;
import com.bnd.math.domain.evo.EvoRun;
import com.bnd.math.domain.evo.EvoTask;
import com.bnd.math.task.EvoTaskParts.EvoRunHolder;
import com.bnd.math.task.EvoTaskParts.EvoTaskHolder;

public class EvoRunTask extends Task implements EvoTaskHolder, EvoRunHolder {

	// TODO: move gaSetting here

	private EvoTask evoTask;
	private EvoPopulationContentStoreOption populationContentStoreOption;
	private EvoPopulationSelection populationSelection;
	private boolean autoSave = true;

	private EvoRun<?> evoRun;
	private Collection<Chromosome<?>> initChromosomes;
	private Chromosome<?> initChromosome;            // optional, normally initial chromosomes are generated randomly

	public EvoRunTask() {
		super();
	}

	public EvoPopulationContentStoreOption getPopulationContentStoreOption() {
		return populationContentStoreOption;
	}

	public void setPopulationContentStoreOption(EvoPopulationContentStoreOption storeOption) {
		this.populationContentStoreOption = storeOption;
	}

	public EvoPopulationSelection getPopulationSelection() {
		return populationSelection;
	}

	public void setPopulationSelection(EvoPopulationSelection populationSelection) {
		this.populationSelection = populationSelection;
	}

	@Override
	public EvoTask getEvoTask() {
		return evoTask;
	}

	@Override
	public void setEvoTask(EvoTask task) {
		this.evoTask = task;
	}

	@Override
	public boolean isEvoTaskDefined() {
		return evoTask != null;
	}

	@Override
	public boolean isEvoTaskComplete() {
		return isEvoTaskDefined() && evoTask.getName() != null;
	}

	public void setEvoTaskId(Long id) {
		if (id != null) {
			evoTask = new EvoTask();
			evoTask.setId(id);
		}
	}

	public Long getEvoTaskId() {
		if (isEvoTaskDefined()) {
			return evoTask.getId();
		}
		return null;
	}

	public void setEvoRunId(Long id) {
		if (id != null) {
			evoRun = new EvoRun();
			evoRun.setId(id);
		}
	}

	public Long getEvoRunId() {
		if (evoRun != null) {
			return evoRun.getId();
		}
		return null;
	}

	@Override
	public EvoRun<?> getEvoRun() {
		return evoRun;
	}

	@Override
	public void setEvoRun(EvoRun<?> evoRun) {
		this.evoRun = evoRun;
	}

	@Override
	public boolean isEvoRunDefined() {
		return evoRun != null;
	}

	@Override
	public boolean isEvoRunComplete() {
		return isEvoRunDefined() && evoRun.getEvoTask() != null;
	}

	public Chromosome<?> getInitChromosome() {
		return initChromosome;
	}

	public void setInitChromosome(Chromosome<?> initChromosome) {
		this.initChromosome = initChromosome;
	}

	public boolean hasInitChromosome() {
		return initChromosome != null;
	}

	public Collection<Chromosome<?>> getInitChromosomes() {
		return initChromosomes;
	}

	public void setInitChromosomes(Collection<Chromosome<?>> initChromosomes) {
		this.initChromosomes = initChromosomes;
	}

	public boolean hasInitChromosomes() {
		return initChromosomes != null && !initChromosomes.isEmpty();
	}

	public boolean getAutoSave() {
		return autoSave;
	}

	public boolean isAutoSave() {
		return autoSave;
	}

	public void setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
	}

	public EvoGaSetting getGaSetting() {
		return evoTask != null ? evoTask.getGaSetting() : null;
	}
}