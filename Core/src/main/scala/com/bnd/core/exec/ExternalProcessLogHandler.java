package com.bnd.core.exec;

import java.util.logging.Level;

import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.logging.Log;

public class ExternalProcessLogHandler extends LogOutputStream {

    private Log log;
    private Level level;

    public ExternalProcessLogHandler(Log log, Level level) {
        super(level.intValue());
        this.level = level;
        this.log = log;
    }

    @Override
    protected void processLine(String line, int l) {
    	if (level == Level.ALL) {
            log.info(line);    		 
    	}
    	if (level == Level.CONFIG) {
            log.trace(line);    		 
    	}
    	if (level == Level.FINE) {
            log.debug(line);    		 
    	}
    	if (level == Level.FINER) {
            log.debug(line);    		 
    	}
    	if (level == Level.FINEST) {
            log.trace(line);    		 
    	}
    	if (level == Level.INFO) {
            log.info(line);    		 
    	}
    	if (level == Level.SEVERE) {
            log.error(line);    		 
    	}
    	if (level == Level.WARNING) {
            log.warn(line);    		 
    	}
    }
}