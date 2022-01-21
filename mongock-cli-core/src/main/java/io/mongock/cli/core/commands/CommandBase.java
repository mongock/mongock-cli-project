package io.mongock.cli.core.commands;

import io.mongock.runner.core.builder.RunnerBuilder;
import java.util.concurrent.Callable;

public abstract class CommandBase<T> implements Callable<T> {

    protected final RunnerBuilder builder;

    public CommandBase(RunnerBuilder builder) {
        this.builder = builder;
    }

    @Override
    public T call() throws Exception {
        return null;
    }

    abstract T execution() throws Exception;
}
