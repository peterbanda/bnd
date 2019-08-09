package com.bnd.math.task;

import com.bnd.math.domain.evo.EvoGaSetting;
import com.bnd.math.domain.evo.EvoRun;
import com.bnd.math.domain.evo.EvoTask;

public interface EvoTaskParts {

	public interface GaSettingHolder {
		public EvoGaSetting getGaSetting();
		public void setGaSetting(EvoGaSetting gaSetting);
		public boolean isGaSettingDefined();
		public boolean isGaSettingComplete();
	}

	public interface EvoTaskHolder {
		public EvoTask getEvoTask();
		public void setEvoTask(EvoTask evoTask);
		public boolean isEvoTaskDefined();
		public boolean isEvoTaskComplete();
	}

	public interface EvoRunHolder {
		public EvoRun<?> getEvoRun();
		public void setEvoRun(EvoRun<?> evoRun);
		public boolean isEvoRunDefined();
		public boolean isEvoRunComplete();
	}
}