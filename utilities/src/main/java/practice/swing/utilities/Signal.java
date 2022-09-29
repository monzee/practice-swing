package practice.swing.utilities;

import java.util.concurrent.Executor;


public class Signal extends Variable<Void> implements Pulse {
	public Signal(Executor uiExecutor) {
		super(null, uiExecutor);
	}

	public Signal() {
		this(Threading.ASAP);
	}

	public void send() {
		super.set(null);
	}

	@Override
	public Runnable receive(Runnable listener) {
		return super.onSet((ignored) -> listener.run());
	}
}

