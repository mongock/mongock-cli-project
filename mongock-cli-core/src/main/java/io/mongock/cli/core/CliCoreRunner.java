package io.mongock.cli.core;

import io.mongock.cli.core.commands.MainCommand;
import io.mongock.cli.core.commands.migrate.MigrateCommand;
import io.mongock.cli.core.commands.undo.UndoCommand;
import io.mongock.cli.core.commands.state.StateCommand;
import io.mongock.runner.core.builder.RunnerBuilder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import picocli.CommandLine;

import static io.mongock.cli.core.commands.CommandName.MIGRATE;
import static io.mongock.cli.core.commands.CommandName.STATE;
import static io.mongock.cli.core.commands.CommandName.UNDO;

import static picocli.CommandLine.IFactory;

public class CliCoreRunner {

    public static Builder builder() {
        return new Builder();
    }

    private CliCoreRunner() {
    }

    public static class Builder {

        private final Set<CommandDefinition> commands = new HashSet<>();
        private IFactory factory;
        private RunnerBuilder builder;

        private Builder() {
        }

        public Builder addCommand(String name, Callable command) {
            commands.add(new CommandDefinition(name, command));
            return this;
        }

        public Builder factory(IFactory factory) {
            this.factory = factory;
            return this;
        }

        public Builder runnerBuilder(RunnerBuilder builder) {
            this.builder = builder;
            return this;
        }

        public CommandLine build() {
            addCommand(UNDO , new UndoCommand(builder));
            addCommand(MIGRATE, new MigrateCommand(builder));
            addCommand(STATE, new StateCommand(builder));
            return getFactory()
                    .map(f -> new CommandLineDecorator(new MainCommand(), f))
                    .orElseGet(() -> new CommandLineDecorator(new MainCommand()))
                    .addSubCommands(commands);
        }



        private Optional<IFactory> getFactory() {
            return Optional.ofNullable(factory);
        }

    }


}