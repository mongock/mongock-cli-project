package io.mongock.professional.cli.core;

import io.mongock.professional.cli.core.commands.ListCommand;
import io.mongock.professional.cli.core.commands.UndoCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import static picocli.CommandLine.IFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

@Command(name = "mongock", description = "Mongock cli")
public class MongockCli {

    public static Builder builder() {
        return new Builder();
    }

    private MongockCli() {
    }

    public static class Builder {

        private Set<CommandDefinition> commands = new HashSet<>();
        private IFactory factory;

        private Builder() {
        }

        public Builder allCommands() {
            return addCommand("undo", new UndoCommand())
                    .addCommand("list", new ListCommand());
        }

        public Builder addCommand(String name, Callable command) {
            commands.add(new CommandDefinition(name, command));
            return this;
        }

        public Builder factory(IFactory factory) {
            this.factory = factory;
            return this;
        }

        public CommandLine build() {
            return getFactory()
                    .map(factory -> new CommandLineDecorator(new MongockCli(), factory))
                    .orElseGet(() -> new CommandLineDecorator(new MongockCli()))
                    .addSubCommands(commands);
        }

        private Optional<IFactory> getFactory() {
            return Optional.ofNullable(factory);
        }

    }


}