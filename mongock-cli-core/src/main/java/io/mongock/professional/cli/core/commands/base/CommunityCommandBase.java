package io.mongock.professional.cli.core.commands.base;

import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilder;

import java.util.concurrent.Callable;

public abstract class CommunityCommandBase<T, BUILDER extends RunnerBuilder> implements Callable<T> {

    protected final RunnerBuilder builder;

    public CommunityCommandBase(RunnerBuilder builder) {
        this.builder = builder;
    }

    @Override
    public T call() {
        return call((BUILDER) builder);
    }

    protected abstract T call(BUILDER mongockBuilder);
}
