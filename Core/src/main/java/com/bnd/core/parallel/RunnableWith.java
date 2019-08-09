package com.bnd.core.parallel;

/**
 * Interface characterizing a unit processing passed object during its run.
 * 
 * @author © Peter Banda
 * @since 2011   
 */
public interface RunnableWith<O> {

	public void run(O objectToProcess);
}