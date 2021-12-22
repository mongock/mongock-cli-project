package io.mongock.cli.core.commands;

import io.mongock.cli.core.VersionProvider;
import picocli.CommandLine.Command;

@Command(name = "mongock", description = "Mongock command line", mixinStandardHelpOptions = true,
versionProvider = VersionProvider.class)
public class MainCommand {
  
}