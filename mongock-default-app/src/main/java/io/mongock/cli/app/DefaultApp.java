package io.mongock.cli.app;

import io.mongock.api.annotations.MongockCliConfiguration;
import io.mongock.cli.util.MongockDefaultApp;

@MongockDefaultApp
@MongockCliConfiguration(sources = RunnerBuilderProviderImpl.class)
public class DefaultApp {


  public static void main(String[] args) {
    new RunnerBuilderProviderImpl()
            .getBuilder()
            .buildRunner()
            .execute();
  }

}
