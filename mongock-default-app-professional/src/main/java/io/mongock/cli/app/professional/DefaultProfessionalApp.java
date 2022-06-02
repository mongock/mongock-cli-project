package io.mongock.cli.app.professional;

import io.mongock.api.annotations.MongockCliConfiguration;

@MongockCliConfiguration(sources = RunnerBuilderProviderProfessionalImpl.class)
public class DefaultProfessionalApp {


  public static void main(String[] args) {
    new RunnerBuilderProviderProfessionalImpl()
            .getBuilder()
            .buildRunner()
            .execute();
  }

}
