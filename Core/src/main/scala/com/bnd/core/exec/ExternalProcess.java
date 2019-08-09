package com.bnd.core.exec;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.exec.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExternalProcess {

	private final Long timeout;
	private final boolean runInBackground;

	private static final int EXPECTED_EXIT_VALUE = 0;
	protected final Log log = LogFactory.getLog(getClass());

	public ExternalProcess(boolean runInBackground) {
		this.runInBackground = runInBackground;
		this.timeout = null;
	}

	public ExternalProcess(boolean runInBackground, long timeout) {
		this.runInBackground = runInBackground;
		this.timeout = timeout;
	}

	public String execute(
		String command, Collection<String> args
	) throws ExternalProcessFailedException {
		return execute(command, args, null);
	}

	public String execute(
		String command, Collection<String> args, Collection<String> envVars
	) throws ExternalProcessFailedException {
		ExecuteWatchdog watchdog = null;
		ExecuteResultInfoHandler resultHandler;

		Map<String, String> map = null;
		if (envVars == null) {
			map = System.getenv();
		} else {
			map = new HashMap<String, String>();
			for (String envVar : envVars) {
				map.put(envVar, System.getenv(envVar));
			}
		}
		CommandLine commandLine = new CommandLine(command);
		for (String arg : args) {
			commandLine.addArgument(arg, false);
		}
		commandLine.setSubstitutionMap(map);

		// create the executor and consider the exitValue 'expectedExitValue' as success
		OutputStream outputStream = new ByteArrayOutputStream();		
		Executor executor = new DefaultExecutor();
		executor.setExitValue(EXPECTED_EXIT_VALUE);
		executor.setStreamHandler(new PumpStreamHandler(outputStream, new ExternalProcessLogHandler(log, Level.SEVERE)));

		// create a watchdog if requested
		if (timeout != null && timeout > 0) {
			watchdog = new ExecuteWatchdog(timeout);
			executor.setWatchdog(watchdog);
		}

		try {
			if (runInBackground) {
				log.info("Executing non-blocking external system job...");
				resultHandler = new ExecuteResultInfoHandler(watchdog);
				executor.execute(commandLine, resultHandler);
			} else {
				log.info("Executing blocking external system job...");
				int exitValue = executor.execute(commandLine);
				resultHandler = new ExecuteResultInfoHandler(exitValue);
			}
			resultHandler.waitFor();

			StringBuilder sb = new StringBuilder();
	        BufferedReader bufferedReader = new BufferedReader(new StringReader(outputStream.toString()));
	        String line;
	        while ((line = bufferedReader.readLine()) != null) {
	        	sb.append(line + '\n');
	        }
	        return sb.toString();
		} catch (ExecuteException e) {
			throw new ExternalProcessFailedException("An execution of the external job failed.", e);
		} catch (IOException e) {
			throw new ExternalProcessFailedException("An execution of the external job failed.", e);
		} catch (InterruptedException e) {
			throw new ExternalProcessFailedException("An execution of the external job failed.", e);
		}
	}
}