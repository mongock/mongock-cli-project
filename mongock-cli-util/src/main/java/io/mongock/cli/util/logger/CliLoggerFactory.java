package io.mongock.cli.util.logger;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.mongock.cli.util.logger.CliLogger.Level.ERROR;
import static io.mongock.cli.util.logger.CliLogger.Level.INFO;

public final class CliLoggerFactory {

	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

	private CliLoggerFactory() {

	}
	public static CliLogger getLogger(Class<?> clazz) {
		return new Logger(clazz);
	}

	public static void setLevel(CliLogger.Level level) {
		Logger.setLevel(level);
	}

	public static class  Logger implements CliLogger {

		private static Level level = INFO;

		private final String className;

		static void setLevel(Level newLevel) {
			level = newLevel;
		}

		Logger(Class<?> targetClass) {
			this.className = targetClass.getName();
		}


		private boolean shouldLog(Level targetLevel) {
			return targetLevel.isGreaterEqual(level);
		}

		@Override
		public void print(Level level, String format, Object... arguments) {
			if(shouldLog(level)) {
				String time = LocalDateTime.now().format(timeFormatter);
				String newFormat = format.contains("{}") ? format.replaceAll("\\{}", "%s") : format;
				String message = String.format(newFormat, arguments);
				PrintStream out = level.isGreaterEqual(ERROR) ? System.err : System.out;
				out.println(time + " [mongock-cli] " + level.name() + " " + className + "  - " + message);
			}
		}
	}


}
