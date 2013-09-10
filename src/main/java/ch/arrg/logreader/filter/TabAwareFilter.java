package ch.arrg.logreader.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * If a line is filtered, all lines with a greated indentation that follow are
 * filtered too.
 */
public class TabAwareFilter extends AbstractFilter {

	private boolean inBlock = false;
	private int rejectDepth = 0;
	private AbstractFilter innerFilter;

	private static Pattern findTabs = Pattern.compile("^(\\s*)");

	public TabAwareFilter(AbstractFilter innerFilter) {
		this.innerFilter = innerFilter;
	}

	@Override
	public void reset() {
		inBlock = false;
	}

	private void whenInBlock(String line) {
		int lineDepth = getTabDepth(line);
		if (lineDepth <= rejectDepth) {
			inBlock = false;
			whenNotInBlock(line);
		}
	}

	private void whenNotInBlock(String line) {
		if (innerFilter.accepts(line)) {
			inBlock = true;
			rejectDepth = getTabDepth(line);
		} else {
			inBlock = false;
		}
	}

	@Override
	public boolean accepts(String line) {
		if (inBlock) {
			whenInBlock(line);
		} else {
			whenNotInBlock(line);
		}

		// Reject when in block
		if (inBlock) {
			return false;
		} else {
			return true;
		}
	}

	public static int getTabDepth(String line) {
		Matcher matcher = findTabs.matcher(line);
		if (matcher.find()) {
			String tabs = matcher.group(1);
			int depth = tabs.length();
			return depth;
		} else {
			return 0;
		}
	}

}
