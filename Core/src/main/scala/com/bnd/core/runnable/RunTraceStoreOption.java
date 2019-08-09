package com.bnd.core.runnable;

public enum RunTraceStoreOption {
	UpdatedStates, AlteredStates, UpdatedAndAlteredStates, None;

	public boolean shouldStoreUpdatedStates() {
		return this == RunTraceStoreOption.UpdatedStates || this == RunTraceStoreOption.UpdatedAndAlteredStates;
	}

	public boolean shouldStoreAlteredStates() {
		return this == RunTraceStoreOption.AlteredStates || this == RunTraceStoreOption.UpdatedAndAlteredStates;
	}
}