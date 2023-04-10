package practice.swing.ex2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import practice.swing.utilities.Signal;
import practice.swing.utilities.Threading;


/**
 * Owner of the state being shown by a view.
 */
public class ViewModel {

	/**
	 * Methods that might be called back when events occur.
	 * 
	 * <p> All methods of implementors of this interface are called in the Swing
	 * UI (event dispatch) thread.
	 */
	public interface EventListener {
		/**
		 * Called at the very beginning and probably never after that.
		 * 
		 * @param products The products being sold.
		 * @param will The operations that could be invoked through user actions.
		 */
		void onReady(Set<Product> products, Intent will);

		/**
		 * Called when this product's count has changed.
		 * 
		 * @param key The product.
		 * @param quantity The new quantity.
		 */
		void onUpdate(Product key, int quantity);

		/**
		 * Called when at least one product has its count increased.
		 * 
		 * @param total The total cost of all products.
		 */
		void onUpdate(Millis total);
	}

	/**
	 * Actions a user might take which cause state changes in the model.
	 */
	public interface Intent {
		/**
		 * Increments the count of this item in the transaction.
		 *
		 * <p> Also causes the total amount to update.
		 * 
		 * @param key The product to order.
		 */
		void order(Product key);

		/**
		 * Finish this transaction and start a new one.
		 * 
		 * <p> Causes all counts and amounts to reset to zero.
		 */
		void settle();
	}

	private enum Tag { NOTHING, UPDATED, ALL_UPDATED }
	private final Map<Product, Integer> orders = new LinkedHashMap<>();
	private final Signal events = new Signal(Threading.NOW);
	private Tag event = Tag.NOTHING;
	private Product lastOrder;
	private Millis total = Millis.ZERO;

	final Intent actions = new Intent() {
		@Override
		public void order(Product key) {
			// TODO: validate the key
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

	/**
	 * Initializes and attaches a listener for update events.
	 * 
	 * <p> The listener is initialized by calling its
	 * {@link EventListener#onReady(Set, Intent)} method.
	 * 
	 * @param listener The listener to invoke when an event occurs.
	 */
	public void attach(EventListener listener) {
		events.receive(() -> dispatch(listener));
		listener.onReady(orders.keySet(), actions);
	}

	void dispatch(EventListener listener) {
		switch (event) {
			case NOTHING:
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

