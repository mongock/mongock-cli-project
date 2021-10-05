package io.mongock.professional.cli.core;

import java.util.Objects;
import java.util.concurrent.Callable;

class CommandDefinition {
    private final String name;
    private final Callable command;

    CommandDefinition(String name, Callable command) {
        this.name = name;
        this.command = command;
    }

    String getName() {
        return name;
    }

    Callable getCommand() {
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandDefinition)) return false;
        CommandDefinition that = (CommandDefinition) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
