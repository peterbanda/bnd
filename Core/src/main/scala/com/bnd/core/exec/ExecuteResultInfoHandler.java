package com.bnd.core.exec;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExecuteResultInfoHandler extends DefaultExecuteResultHandler {

	private ExecuteWatchdog watchdog;
    private final Log log = LogFactory.getLog(getClass());

	public ExecuteResultInfoHandler(ExecuteWatchdog watchdog) {
		this.watchdog = watchdog;
	}

	public ExecuteResultInfoHandler(int exitValue) {
		super.onProcessComplete(exitValue);
	}

	public void onProcessComplete(int exitValue) {
		super.onProcessComplete(exitValue);
		log.info("The external execute process successfully finished.");
	}

	public void onProcessFailed(ExecuteException e) {
		super.onProcessFailed(e);
		if (watchdog != null && watchdog.killedProcess()) {
			log.error("The external execute process timed out.");
		} else {
			log.error("The external execute process failed.", e);
		}
	}
}