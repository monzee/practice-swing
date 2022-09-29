package practice.swing.ex2;

import java.net.URL;


public record Product(
	String name,
	URL image,
	Millis unitPrice
) {}

