package io.mongock.cli.springboot;

import io.mongock.cli.util.banner.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class MongockBanner implements org.springframework.boot.Banner {
	@Override
	public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
		Banner.print(out);
	}
}
