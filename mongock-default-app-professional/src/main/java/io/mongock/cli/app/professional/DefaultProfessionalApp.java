package io.mongock.cli.app.professional;

import io.mongock.api.annotations.MongockCliConfiguration;
import io.mongock.cli.util.MongockDefaultApp;


@MongockDefaultApp
@MongockCliConfiguration(sources = RunnerBuilderProviderProfessionalImpl.class)
public class DefaultProfessionalApp {


  public static void main(String[] args) {
    new RunnerBuilderProviderProfessionalImpl()
            .getBuilder()
            .buildRunner()
            .execute();
  }

}
