package io.mongock.cli.core.commands;

import io.mongock.api.exception.MongockException;
import io.mongock.runner.core.builder.RunnerBuilder;
import java.util.concurrent.Callable;

public abstract class CommandBase<T> implements Callable<T> {

    protected final RunnerBuilder builder;

    public CommandBase(RunnerBuilder builder) {
        this.builder = builder;
    }

    @Override
    public T call() throws Exception {
        if(builder == null) {
            throw new MongockException("Mongock builder needs to be provided for this command");
        }
        return execution();
    }

    public abstract T execution() throws Exception;
}
