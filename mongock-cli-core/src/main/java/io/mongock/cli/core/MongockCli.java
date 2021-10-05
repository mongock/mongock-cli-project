package io.mongock.cli.core;

import io.mongock.api.exception.MongockException;
import io.mongock.cli.core.commands.MigrateCommand;
import io.mongock.cli.core.commands.UndoCommand;
import io.mongock.runner.core.builder.RunnerBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

import static io.mongock.cli.core.commands.CommandName.MIGRATE;
import static io.mongock.cli.core.commands.CommandName.UNDO;
import static picocli.CommandLine.IFactory;

@Command(name = "mongock", description = "Mongock command line", mixinStandardHelpOptions = true, version = "1.0")
public class MongockCli {

    public static Builder builder() {
        return new Builder();
    }

    private MongockCli() {
    }

    public static class Builder {

        private Set<CommandDefinition> commands = new HashSet<>();
        private IFactory factory;
        private boolean allCommands = false;
        private RunnerBuilder builder;

        private Builder() {
        }

        public Builder allCommands() {
            allCommands = true;
            return this;
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
            validate();
            if (allCommands) {
                addCommand(UNDO , new UndoCommand(builder));
                addCommand(MIGRATE, new MigrateCommand(builder));
            }
            return getFactory()
                    .map(factory -> new CommandLineDecorator(new MongockCli(), factory))
                    .orElseGet(() -> new CommandLineDecorator(new MongockCli()))
                    .addSubCommands(commands);
        }

        private void validate() {
            if (builder == null) {
                throw new MongockException("builder needs to be provided to CLI");
            }
        }

        private Optional<IFactory> getFactory() {
            return Optional.ofNullable(factory);
        }

    }


}