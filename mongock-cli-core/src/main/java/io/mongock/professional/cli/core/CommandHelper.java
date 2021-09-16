package io.mongock.professional.cli.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static io.mongock.professional.cli.core.commands.CommandName.UNDO;

public final class CommandHelper {

	private static final Set<String> PROFESSIONAL_COMMANDS = new HashSet<>(
			Arrays.asList(UNDO)
	);

	private CommandHelper() {
	}

	public static boolean isProfessionalCommand(String command) {
		return PROFESSIONAL_COMMANDS
				.stream()
				.anyMatch(command::equalsIgnoreCase);
	}
}