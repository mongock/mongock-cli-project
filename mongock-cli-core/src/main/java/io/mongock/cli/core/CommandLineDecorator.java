package io.mongock.cli.core;

import picocli.CommandLine;

import java.util.Collection;

public class CommandLineDecorator extends CommandLine {

    public CommandLineDecorator(Object command, IFactory factory) {
        super(command, factory);
    }

    public CommandLineDecorator(Object command) {
        super(command);
    }

    public CommandLineDecorator addSubCommands(Collection<CommandDefinition> commandDefinitions) {
        commandDefinitions.forEach(this::addSubCommand);
        return this;
    }

    @Override
    public int execute(String[] args) {
        return super.execute(args);
    }


    private void addSubCommand(CommandDefinition commandDefinition) {
        addSubcommand(commandDefinition.getName(), commandDefinition.getCommand());
    }
}