package practice.swing.utilities;


@FunctionalInterface
public interface Pulse {
	Runnable receive(Runnable listener);
}

