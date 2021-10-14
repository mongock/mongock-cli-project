package io.mongock.cli.util.banner;

import com.diogonunes.jcolor.Attribute;

import java.io.PrintStream;

import static com.diogonunes.jcolor.Ansi.colorize;

public final class Banner {

	private static boolean active = true;

	private Banner() {
	}

	public static boolean isActive() {
		return active;
	}

	public static void setActive(boolean active) {
		Banner.active = active;
	}

	private static final String banner =
			"   \\  |                                |   \n" +
					"  |\\/ |   _ \\    \\    _` |   _ \\   _|  | / \n" +
					" _|  _| \\___/ _| _| \\__, | \\___/ \\__| _\\_\\ \n" +
					"                    ____/                  ";


	public static void print(PrintStream out) {
		if(active)out.println(colorize(banner, Attribute.CYAN_TEXT()));
	}
}
