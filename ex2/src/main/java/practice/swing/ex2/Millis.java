package practice.swing.ex2;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public record Millis(
	long amount
) {
	public static Millis of(double amountInUSD) {
		return new Millis((long) (1000 * amountInUSD * EXCHANGE_RATE));
	}

	public static final Millis ZERO = new Millis(0);
	private static final double EXCHANGE_RATE = 58.75;
	private static final DecimalFormat FORMATTER;

	static {
		var nf = NumberFormat.getInstance(Locale.forLanguageTag("en-PH"));
		if (nf instanceof DecimalFormat df) {
			FORMATTER = df;
		}
		else {
			FORMATTER = new DecimalFormat();
		}
		FORMATTER.applyPattern("¤ #,##0.00;¤ (#)");
	}

	public Millis neg() {
		return new Millis(-amount);
	}

	public Millis times(double count) {
		return new Millis((long) (amount * count));
	}

	public Millis plus(Millis that) {
		return new Millis(amount + that.amount);
	}

	@Override
	public String toString() {
		return FORMATTER.format(amount / 1_000d);
	}
}

