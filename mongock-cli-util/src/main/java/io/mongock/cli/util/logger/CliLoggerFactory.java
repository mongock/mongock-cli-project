package io.mongock.cli.util.logger;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.BLUE_TEXT;
import static com.diogonunes.jcolor.Attribute.CYAN_TEXT;
import static com.diogonunes.jcolor.Attribute.GREEN_TEXT;
import static com.diogonunes.jcolor.Attribute.MAGENTA_TEXT;
import static com.diogonunes.jcolor.Attribute.RED_TEXT;
import static com.diogonunes.jcolor.Attribute.WHITE_TEXT;
import static com.diogonunes.jcolor.Attribute.YELLOW_TEXT;
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


		private static final String pid;

		static {
			pid = getProcessId("<PID>");
		}

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

				String newFormat = format.contains("{}") ? format.replaceAll("\\{}", "%s") : format;
				String message = String.format(newFormat, arguments);

				String time = colorize(LocalDateTime.now().format(timeFormatter), WHITE_TEXT());
				String pidColored = colorize(pid, MAGENTA_TEXT());
				String levelMsg = getColoredLevel(level);

				PrintStream out = level.isGreaterEqual(ERROR) ? System.err : System.out;
				String application = colorize("--- [mongock-cli]", WHITE_TEXT());
				String classColored = colorize(className, CYAN_TEXT());
				out.println(time + " " + levelMsg + " " + pidColored + " " +  application + " " + classColored + "  :\t " + message );
			}
		}

		private String getColoredLevel(Level level) {
			switch (level) {
				case ERROR: return colorize(level.name(), RED_TEXT());
				case INFO: return colorize(level.name(), GREEN_TEXT()) + " ";
				case WARN: return colorize(level.name(), YELLOW_TEXT()) + " ";
				case DEBUG: return colorize(level.name(), BLUE_TEXT());
				case TRACE: return colorize(level.name(), CYAN_TEXT());
				default:  return level.name();
			}

		}


		private static String getProcessId(final String defaultValue) {
			final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
			final int index = jvmName != null ? jvmName.indexOf('@') : -1;

			if (index < 1) {
				return defaultValue;
			}

			try {
				return Long.toString(Long.parseLong(jvmName.substring(0, index)));
			} catch (Exception e) {
				return defaultValue;
			}
		}

	}


}
