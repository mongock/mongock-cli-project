package io.mongock.cli.wrapper.util;

import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ArgsUtil {

	private static final CliLogger logger = CliLoggerFactory.getLogger(ArgsUtil.class);

	private ArgsUtil() {}

	public static String[] getCleanArgs(String[] args) {

		StringBuilder sb = new StringBuilder("cleaning arguments: ");
		Set<String> paramNamesSet = Arrays
				.stream(Arguments.values())
				.map(Arguments::getNames)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet())
				.stream()
				.peek(arg -> sb.append(arg).append(" "))
				.map(String::toLowerCase)
				.collect(Collectors.toSet());
		logger.debug(sb.toString());


		List<String> tempNewArgs = new ArrayList<>();

		for (int i = 0; i < args.length; i++) {
			if (!paramNamesSet.contains(args[i].toLowerCase())) {
				tempNewArgs.add(args[i]);
			} else {
				i++;
			}
		}

		String[] newArgs = new String[tempNewArgs.size()];
		tempNewArgs.toArray(newArgs);
		logger.debug("cleaned args size: " + newArgs.length);
		StringBuilder sb2 = new StringBuilder("cleaned args: ");
		Stream.of(newArgs).forEach(arg -> sb2.append(arg).append(" "));
		logger.debug(sb2.toString());
		return newArgs;
	}

	public static Optional<String> getOptionalParam(String[] args, String paramName) {
		return Optional.ofNullable(getParameter(args, paramName, false));
	}

	public static String getParameter(String[] args, String paramName) {
		return getParameter(args, paramName, true);
	}

	public static String getParameter(String[] args, String paramName, boolean throwException) {
		int i = 0;
		do {
			if (paramName.equalsIgnoreCase(args[i])) {
				if (args.length == i + 1) {
					if(throwException) {
						throw new RuntimeException(
								String.format("Found [%s] flag with missing value. Please follow the format \"%s value\"", paramName, paramName)
						);
					} else {
						return null;
					}

				}
				return args[i + 1];
			}
		} while ((++i) < args.length);
		if(throwException) {
			throw new RuntimeException(String.format("Missing jar parameter. Please follow the format \"%s jar_path\"", paramName));

		} else {
			return null;
		}

	}
}
