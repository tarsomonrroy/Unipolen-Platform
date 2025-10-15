package com.unipolen.webserver.controller;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.PatternSyntaxException;

import static com.unipolen.webserver.controller.FormSanitizer.Handler.CROSS_SITE_SCRIPTING;
import static com.unipolen.webserver.controller.FormSanitizer.Handler.SQL_INJECTION;

public class FormSanitizer {
	public static enum Handler {
		SQL_INJECTION,
		CROSS_SITE_SCRIPTING;

		public static int size() {
			return Handler.values().length;
		}
	}

	public static final Function<String, String>[] appliers;
	public static final Predicate<String>[] checkers;

	public static final String[][] patterns;

	static {
		patterns = new String[Handler.size()][];

		patterns[SQL_INJECTION.ordinal()] = new String[] {
				"'",
				"\"",
				"\\",
				"1=1",
				"0=0",
				";",
				",",
				"\n",
				"\0"
		};

		patterns[CROSS_SITE_SCRIPTING.ordinal()] = new String[] {
				"'",
				"&#27;",
				"\"",
				"&quot;",
				"\\",
				">",
				"&gt;",
				"<",
				"&lt;",
				"&amp;"
		};

		appliers = new Function[Handler.size()];
		checkers = new Predicate[Handler.size()];

		appliers[SQL_INJECTION.ordinal()] = input -> {
			String output = input;
			for (String pattern : patterns[SQL_INJECTION.ordinal()]) {
				output = output.replace(pattern, " ");
			}
			return output;
		};

		appliers[CROSS_SITE_SCRIPTING.ordinal()] = input -> {
			String output = input;
			for (String pattern : patterns[CROSS_SITE_SCRIPTING.ordinal()]) {
				output = output.replace(pattern, " ");
			}
			return output;
		};

		checkers[SQL_INJECTION.ordinal()] = input -> {
			for (String pattern : patterns[SQL_INJECTION.ordinal()]) {
				if (input.contains(pattern)) return false;
				try {
					if (input.matches(pattern)) return false;
				} catch (PatternSyntaxException ignored) {}
			}

			return true;
		};

		checkers[CROSS_SITE_SCRIPTING.ordinal()] = input -> {
			for (String pattern : patterns[CROSS_SITE_SCRIPTING.ordinal()]) {
				if (input.contains(pattern)) return false;
				try {
					if (input.matches(pattern)) return false;
				} catch (PatternSyntaxException ignored) {}
			}

			return true;
		};
	}

	private final EnumSet<Handler> handlers;

	public FormSanitizer(Handler... handlers) {
		this.handlers = EnumSet.copyOf(Arrays.asList(handlers));
	}

	public FormSanitizer() {
		this.handlers = EnumSet.allOf(Handler.class);
	}

	public String apply(String input) {
		String output = input;
		while (!isSafe(output)) {
			for (Handler handler : handlers) output = appliers[handler.ordinal()].apply(output);
		}
		return output;
	}

	public boolean isSafe(String input) {
		for (Handler handler : handlers) {
			if (!checkers[handler.ordinal()].test(input)) return false;
		}

		return true;
	}
}
