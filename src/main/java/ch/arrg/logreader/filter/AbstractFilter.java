package ch.arrg.logreader.filter;

public abstract class AbstractFilter {

	public abstract void reset();

	/**
	 * @param line
	 * @return
	 */
	public abstract boolean accepts(String line);

	private boolean enabled = true;

	private boolean isPrintOnAccept = true;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isPrintOnAccept() {
		return isPrintOnAccept;
	}

	public void setIsPrintOnAccept(boolean isPrintOnAccept) {
		this.isPrintOnAccept = isPrintOnAccept;
	}

	public boolean isDiscardOnReject() {
		return !isPrintOnAccept;
	}
}