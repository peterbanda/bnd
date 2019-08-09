package com.bnd.core.parallel;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

public abstract class Worker<T> implements Runnable {

	private RunnableWith<T> runnableUnit;
	private final CountDownLatch doneSignal;
	private Collection<T> objectsToProcess;

	/**
	 * Creates new instance of the class Worker and initializes it.
	 * 
	 * @param doneSignal
	 */
	public Worker(CountDownLatch doneSignal, RunnableWith<T> runnableUnit, Collection<T> objectsToProcess) {
		this.doneSignal = doneSignal;
		this.runnableUnit = runnableUnit;
		this.objectsToProcess = objectsToProcess;
	}

	public void run() {
		for (T objectToProcess : objectsToProcess) {
			runnableUnit.run(objectToProcess);
		}
		doneSignal.countDown();
	}
}