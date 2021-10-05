package io.mongock.cli.core;

import picocli.CommandLine;

import java.util.Collection;

class CommandLineDecorator extends CommandLine {

    CommandLineDecorator(Object command, IFactory factory) {
        super(command, factory);
    }

    CommandLineDecorator(Object command) {
        super(command);
    }

    CommandLineDecorator addSubCommands(Collection<CommandDefinition> commandDefinitions) {
        commandDefinitions.forEach(this::addSubCommand);
        return this;
    }

    private void addSubCommand(CommandDefinition commandDefinition) {
        addSubcommand(commandDefinition.getName(), commandDefinition.getCommand());
    }
}