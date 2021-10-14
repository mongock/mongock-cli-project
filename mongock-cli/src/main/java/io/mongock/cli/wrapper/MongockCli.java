package io.mongock.cli.wrapper;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import io.mongock.cli.util.banner.Banner;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.util.ArgsUtil;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.util.stream.Stream;

import static io.mongock.cli.util.logger.CliLogger.Level.INFO;
import static io.mongock.cli.wrapper.CliJarLauncher.Type.SPRINGBOOT;
import static io.mongock.cli.wrapper.util.ArgsUtil.getCleanArgs;

public class MongockCli {

	private static final CliLogger logger = CliLoggerFactory.getLogger(MongockCli.class);
	public static final String LOG_LEVEL_ARG = "-logLevel";
	public static final String APP_JAR_ARG = "-appJar";
	public static final String CLI_SPRING_JAR_ARG = "-cliSpringJar";
	public static final String CLI_CORE_JAR_ARG = "-cliCoreJar";

	static {
		Banner.print(System.out);
	}

	public static void main(String... args) throws Exception {
		setLogger(args);
		printArgs(args);
		String appJar = ArgsUtil.getParameter(args, APP_JAR_ARG);
		CliJarLauncher launcher = LauncherFactory.getLauncher(new JarFileArchive(new File(appJar)));
		launcher.appJar(appJar)
				.cliJar(ArgsUtil.getParameter(args, launcher.getType() == SPRINGBOOT ? CLI_SPRING_JAR_ARG : CLI_CORE_JAR_ARG))
				.loadClasses()
				.launch(getCleanArgs(args, APP_JAR_ARG, CLI_SPRING_JAR_ARG, CLI_CORE_JAR_ARG, LOG_LEVEL_ARG));

	}

	private static void printArgs(String[] args) {
		StringBuilder sb = new StringBuilder("CLI arguments: ");
		Stream.of(args).forEach(arg -> sb.append(arg).append(" "));
		logger.debug(sb.toString());
	}

	private static void setLogger(String[] args) {
		CliLoggerFactory.setLevel(ArgsUtil.getOptionalParam(args, LOG_LEVEL_ARG)
				.map(CliLogger.Level::fromStringDefaultInfo)
				.orElse(INFO));
	}


}
