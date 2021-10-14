package io.mongock.cli.springboot;

import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class MongockBanner implements org.springframework.boot.Banner {
	@Override
	public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {

		String logoSmallShadow = "   \\  |                                |   \n" +
				"  |\\/ |   _ \\    \\    _` |   _ \\   _|  | / \n" +
				" _|  _| \\___/ _| _| \\__, | \\___/ \\__| _\\_\\ \n" +
				"                    ____/                  ";

		out.println(logoSmallShadow);

	}
}
