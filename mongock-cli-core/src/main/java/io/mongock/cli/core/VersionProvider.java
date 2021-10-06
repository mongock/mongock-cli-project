package io.mongock.cli.core;

import picocli.CommandLine;

import java.util.Properties;

public class VersionProvider implements CommandLine.IVersionProvider {
	@Override
	public String[] getVersion() throws Exception {
		final Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader().getResourceAsStream("cli.properties"));
		return new String[]{properties.getProperty("version")};
	}
}
