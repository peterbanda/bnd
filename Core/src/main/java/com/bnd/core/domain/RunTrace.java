package com.bnd.core.domain;

public abstract class RunTrace {

    protected Double runTime;
    
    public Double runTime() {
    	return runTime;
    }

    public void runTime(Double runTime) {
    	this.runTime = runTime;
    }
}