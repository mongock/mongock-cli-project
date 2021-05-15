package io.mongock.professional.cli.springboot.config;

public class WrapperProperties {

    private MongockWrapper mongock;

    public MongockWrapper getMongock() {
        return mongock;
    }

    public void setMongock(MongockWrapper mongock) {
        this.mongock = mongock;
    }

    public static class MongockWrapper {
        private CliProperties cli;

        public CliProperties getCli() {
            return cli;
        }

        public void setCli(CliProperties cli) {
            this.cli = cli;
        }
    }
}
