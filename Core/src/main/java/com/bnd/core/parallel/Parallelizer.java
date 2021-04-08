package com.bnd.core.parallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import com.bnd.core.BndRuntimeException;

/**
 * Utility class providing parallel execution of given work load
 * on several threads at the same time.
 * 
 * @author © Peter Banda
 * @since 2011   
 */
public class Parallelizer<O> {

	private RunnableWith<O> runnableUnit;
	private Collection<O> workerInputs;
	private int threadsNum;
	private int inputsPerThread = 1;
	private Integer runsNum;
	protected transient Semaphore semaphore;
	protected transient CountDownLatch doneSignal;

	private Parallelizer(RunnableWith<O> runnableUnit, int threadsNum) {
		this.runnableUnit = runnableUnit;
		this.threadsNum = threadsNum;
		semaphore = new Semaphore(threadsNum);
	}

	public Parallelizer(RunnableWith<O> runnableUnit, int threadsNum, Collection<O> workerInputs) {
		this(runnableUnit, threadsNum);
		this.workerInputs = workerInputs;
		doneSignal = new CountDownLatch(workerInputs.size());
	}

	public Parallelizer(RunnableWith<O> runnableUnit, int threadsNum, Collection<O> workerInputs, int runsThreadsRatio) {
		this(runnableUnit, threadsNum);
		this.workerInputs = workerInputs;
		int runsNum = workerInputs.size();
		setInputsPerThread(runsThreadsRatio, runsNum);		
	}

	public Parallelizer(RunnableWith<O> runnableUnit, int threadsNum, O workerInput, int runsNum) {
		this(runnableUnit, threadsNum);
		this.workerInputs = Collections.singleton(workerInput);
		this.runsNum = runsNum;
		doneSignal = new CountDownLatch(runsNum);
	}

	public Parallelizer(RunnableWith<O> runnableUnit, int threadsNum, O workerInput, int runsNum, int runsThreadsRatio) {
		this(runnableUnit, threadsNum);
		this.workerInputs = Collections.singleton(workerInput);
		this.runsNum = runsNum;
		setInputsPerThread(runsThreadsRatio, runsNum);
	}

	private void setInputsPerThread(int runsThreadsRatio, int runsNum) {
		this.inputsPerThread = runsNum / (runsThreadsRatio * threadsNum);
		if (inputsPerThread < 1) {
			throw new BndRuntimeException("The number of threads " + threadsNum + " too high for current workload!");
		}
		int threadInstancesNum = runsNum / inputsPerThread;
		if (runsNum % inputsPerThread != 0) {
			threadInstancesNum++;
		}
		doneSignal = new CountDownLatch(threadInstancesNum);
	}

	public void run() {
		try {
			if (runsNum != null) {
				launchWorkersWithSameInput();
			} else {
				launchWorkersWithInputs();
			}
			doneSignal.await();
		} catch (InterruptedException exception) {
			throw new RuntimeException(exception);
		}			
	}

	private void launchWorkersWithSameInput() {
		O workerInput = workerInputs.iterator().next();
		Collection<O> actualWorkerInputs = new ArrayList<O>();
		for (int i = 1; i <= runsNum; i++) {
			actualWorkerInputs.add(workerInput);
			if (i % inputsPerThread == 0 || i == runsNum) {
				launchWorker(actualWorkerInputs);	
				actualWorkerInputs = new ArrayList<O>();
			}
		}
	}

	private void launchWorkersWithInputs() {
		Collection<O> actualWorkerInputs = new ArrayList<O>();
		int i = 1;
		Iterator<O> workerInputIterator = workerInputs.iterator();
		while (workerInputIterator.hasNext()) {
			actualWorkerInputs.add(workerInputIterator.next());
			if (i % inputsPerThread == 0 || !workerInputIterator.hasNext()) {
				launchWorker(actualWorkerInputs);
				actualWorkerInputs = new ArrayList<O>();
			}
			i++;
		}
	}

	protected void launchWorker(Collection<O> actualWorkerInputs) {
		SemaphoreWorker<O> worker = new SemaphoreWorker<O>(doneSignal, runnableUnit, actualWorkerInputs, semaphore);
		new Thread(worker).start();		
	}
}