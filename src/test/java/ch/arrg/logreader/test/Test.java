package ch.arrg.logreader.test;

public class Test {

	public static void main(String... args) {
		RuntimeException e = new RuntimeException("A");
		for (int i = 0; i < 5; i++) {
			e = roll(e, i);
		}

		throw e;
	}

	private static RuntimeException roll(RuntimeException e, int i) {
		e = new RuntimeException("a" + i, e);
		return e;
	}
}
