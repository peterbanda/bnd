package com.bnd.core.parallel;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import com.bnd.core.BndRuntimeException;

/**
 * Semaphore worker is a worker whose execution is managed by semaphore and it is shared by several threads.
 * Worker also uses <code>CountDownLatch</code> to indicate that work is done.
 * 
 * @author © Peter Banda
 * @since 2011   
 */
public class SemaphoreWorker<T> extends Worker<T> {

	private final Semaphore semaphore;

	/**
	 * Creates new instance of the class SemaphoreWorker and initializes it.
	 * 
	 * @param doneSignal
	 */
	public SemaphoreWorker(CountDownLatch doneSignal, RunnableWith<T> runnableUnit, Collection<T> objectsToProcess, Semaphore semaphore) {
		super(doneSignal, runnableUnit, objectsToProcess);
		this.semaphore = semaphore;
	}

	public void run() {
		try {
			semaphore.acquire();
			super.run();
			semaphore.release();
		} catch (InterruptedException e) {
			throw new BndRuntimeException("Semaphore interrupted!", e);
		}
	}
}