package ch.arrg.logreader.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	private static Pattern findTabs = Pattern.compile("^(\\s*)");

	public static int getTabDepth(String line) {
		Matcher matcher = findTabs.matcher(line);
		if (matcher.find()) {
			String tabs = matcher.group(1);
			int depth = tabs.length();
			return depth;
		} else {
			return -1;
		}
	}

	public static void main(String... args) {
		System.out.println(getTabDepth("\t\t    	\tCompile with"));
	}
}
