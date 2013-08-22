package ch.arrg.logreader.filter;

public class BlockFilter extends AbstractFilter {
	private final WordFilter enabler;
	private final WordFilter disabler;
	private boolean matched = false;

	public BlockFilter(WordFilter enabler, WordFilter disabler) {
		this.enabler = enabler;
		this.disabler = disabler;
	}

	@Override
	public void reset() {
		matched = false;
	}

	@Override
	public boolean accepts(String line) {
		// If enabler is empty, the filter always accepts
		if (enabler.isEmpty()) {
			return true;
		}

		if (matched) {
			// If previous match, see whether we now reject
			if (disabler.accepts(line)) {
				matched = false;
			}
		} else {
			// If no previous match, see whether we now accept
			if (enabler.accepts(line)) {
				matched = true;
			}
		}

		// Then invert the match: other wise it doesn't work as a block filter
		return !matched;
	}

}
