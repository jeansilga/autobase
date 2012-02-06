package liquibase.ext.logging;

import liquibase.logging.core.AbstractLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Liquibase logging implementation that uses slf4j
 *
 * @author Chris Derham
 */
public class LiquibaseSlf4jLogger extends AbstractLogger {
	private final static Logger log = LoggerFactory.getLogger(LiquibaseSlf4jLogger.class);

	@Override
	public void setName(String name) {
		// ignored
	}

	@Override
	public void setLogLevel(String logLevel, String logFile) {
		super.setLogLevel(logLevel);
	}

	@Override
	public void severe(String message) {
		// route to error
		log.error(message);
	}

	@Override
	public void severe(String message, Throwable e) {
		// route to error
		log.error(message, e);
	}

	@Override
	public void warning(String message) {
		log.warn(message);
	}

	@Override
	public void warning(String message, Throwable e) {
		log.warn(message, e);
	}

	@Override
	public void info(String message) {
		System.out.println("info message " + message);
		log.info(message);
	}

	@Override
	public void info(String message, Throwable e) {
		log.info(message, e);
	}

	@Override
	public void debug(String message) {
		log.debug(message);
	}

	@Override
	public void debug(String message, Throwable e) {
		log.debug(message, e);
	}

	@Override
	public int getPriority() {
		System.out.println("priority was requested");
		return 100;
	}
}
