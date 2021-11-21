package io.mongock.cli.core;

import java.util.Objects;
import java.util.concurrent.Callable;

public class CommandDefinition {
    private final String name;
    private final Callable command;

    public CommandDefinition(String name, Callable command) {
        this.name = name;
        this.command = command;
    }

    public String getName() {
        return name;
    }

    public Callable getCommand() {
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
