package practice.swing.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.SwingUtilities;


public class Variable<T> implements Value<T> {
	private T value;
	private final Lock read;
	private final Lock write;
	private final Executor onUiThread;
	private final List<Consumer<? super T>> listeners = new ArrayList<>();

	public Variable(T initialValue, Executor uiExecutor) {
		value = initialValue;
		onUiThread = uiExecutor;
		var locks = new ReentrantReadWriteLock();
		read = locks.readLock();
		write = locks.writeLock();
	}

	public Variable(T initialValue) {
		this(initialValue, Threading.ASAP);
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public Runnable onSet(Consumer<? super T> listener) {
		write.lock();
		try {
			var index = listeners.size();
			listeners.add(listener);
			return () -> {
				write.lock();
				try {
					listeners.set(index, null);
				}
				finally {
					write.unlock();
				}
			};
		}
		finally {
			write.unlock();
		}
	}

  public void clear() {
    write.lock();
    try {
      listeners.clear();
    }
    finally {
      write.unlock();
    }
  }

	public void set(T newValue) {
		value = newValue;
		onUiThread.execute(() -> {
			read.lock();
			try {
				for (var listener : listeners) if (listener != null) {
					listener.accept(newValue);
				}
			}
			finally {
				read.unlock();
			}
		});
	}

	public void update(Function<? super T, ? extends T> modifier) {
		set(modifier.apply(get()));
	}
}

