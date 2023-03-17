package io.mongock.cli.util.logger;

import static io.mongock.cli.util.logger.CliLogger.Level.DEBUG;
import static io.mongock.cli.util.logger.CliLogger.Level.ERROR;
import static io.mongock.cli.util.logger.CliLogger.Level.INFO;
import static io.mongock.cli.util.logger.CliLogger.Level.TRACE;
import static io.mongock.cli.util.logger.CliLogger.Level.WARN;

public interface CliLogger {

	enum Level{
		TRACE(1), DEBUG(2), WARN(3), INFO(4), ERROR(5), NONE(6);
		private final int level;
		Level(int level) {
			this.level = level;
		}

		boolean isGreaterEqual(Level that) {
			return this.level >= that.level;
		}

		public static Level fromStringDefaultInfo(String targetLevel) {
			return fromString(targetLevel, INFO);
		}

		public static Level fromString(String targetLevel, Level defaultLevel) {
			for(Level itemLevel : Level.values()) {
				if(itemLevel.name().equalsIgnoreCase(targetLevel)) {
					return itemLevel;
				}
			}
			return defaultLevel;
		}
	}

	default void trace(String format, Object... arguments) {
		print(TRACE, format, arguments);
	}


	default void debug(String format, Object... arguments) {
		print(DEBUG, format, arguments);
	}

	default void info(String format, Object... arguments) {
		print(INFO, format, arguments);
	}

	default void warn(String format, Object... arguments) {
		print(WARN, format, arguments);
	}



	default void warn(Throwable ex) {
		print(WARN, ex);
	}


	default void error(String format, Object... arguments) {
		print(ERROR, format, arguments);
	}

	default void error(Throwable ex) {
		print(ERROR, ex);
	}

	//	void error(String format, Throwable th);

	void print(Level level, String format, Object... arguments);

	void print(Level level, Throwable ex);
}
