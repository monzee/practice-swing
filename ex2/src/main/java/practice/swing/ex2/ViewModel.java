package practice.swing.ex2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import practice.swing.utilities.Signal;


public class ViewModel {

	public interface EventListener {
		void onReady(Set<Product> products, Intent will);
		void onUpdate(Product key, int quantity);
		void onUpdate(Millis total);
	}

	public interface Intent {
		void order(Product key);
		void settle();
	}

	private enum Tag { READY, UPDATED, ALL_UPDATED }
	private final Map<Product, Integer> orders = new LinkedHashMap<>();
	private final Signal events = new Signal();
	private Tag event = Tag.READY;
	private Product lastOrder;
	private Millis total = Millis.ZERO;

	final Intent actions = new Intent() {
		@Override
		public void order(Product key) {
			orders.put(key, orders.get(key) + 1);
			total = total.plus(key.unitPrice());
			lastOrder = key;
			event = Tag.UPDATED;
			events.send();
		}

		@Override
		public void settle() {
			for (var key : orders.keySet()) {
				orders.put(key, 0);
			}
			total = Millis.ZERO;
			event = Tag.ALL_UPDATED;
			events.send();
		}
	};

	public ViewModel(List<Product> products) {
		for (var product : products) {
			orders.put(product, 0);
		}
	}

	public void listen(EventListener listener) {
		events.receive(() -> inspect(listener));
	}

	public void refresh() {
		events.send();
	}

	void inspect(EventListener listener) {
		switch (event) {
			case READY:
				listener.onReady(orders.keySet(), actions);
				break;
			case UPDATED:
				listener.onUpdate(lastOrder, orders.get(lastOrder));
				listener.onUpdate(total);
				break;
			case ALL_UPDATED:
				for (var key : orders.keySet()) {
					listener.onUpdate(key, orders.get(key));
				}
				listener.onUpdate(total);
				break;
		}
	}
}

