package practice.swing.utilities;

import java.util.function.Consumer;
import java.util.function.Function;


public interface Value<T> {
	T get();
	Runnable onSet(Consumer<? super T> listener);

	default <U, V extends Value<U>> V pipe(Function<? super T, ? extends V> transform) {
		return transform.apply(get());
	}
}
