package io.mongock.cli.app;

import io.mongock.api.annotations.MongockCliConfiguration;

@MongockCliConfiguration(sources = RunnerBuilderProviderImpl.class)
public class StandaloneSpringdataApp {


  public static void main(String[] args) {
    new RunnerBuilderProviderImpl()
            .getBuilder()
            .buildRunner()
            .execute();
  }

}
