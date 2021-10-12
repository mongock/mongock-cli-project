package io.mongock.cli.wrapper;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ArgsUtil {

	private ArgsUtil() {}

	public static String[] getCleanArgs(String[] args, String... paramNames) {

		Set<String> paramNamesSet = Stream.of(paramNames)
				.map(String::toLowerCase)
				.collect(Collectors.toSet());

		String[] newArgs = new String[args.length - (paramNames.length * 2)];
		int newArgsIndex = 0;
		for (int i = 0; i < args.length; i++) {
			if (!paramNamesSet.contains(args[i].toLowerCase())) {
				newArgs[newArgsIndex++] = args[i];
			} else {
				i++;
			}
		}
		return newArgs;
	}

	public static String getParameter(String[] args, String paramName) {
		int i = 0;
		do {
			if (paramName.equalsIgnoreCase(args[i])) {
				if (args.length == i + 1) {
					throw new RuntimeException(
							String.format("Found [%s] flag with missing value. Please follow the format \"%s value\"", paramName, paramName)
					);
				}
				return args[i + 1];
			}
		} while ((++i) < args.length);
		throw new RuntimeException(String.format("Missing jar parameter. Please follow the format \"-%s jar_path\"", paramName));
	}
}
