package practice.swing.ex2;

/*
 * Specs
 * =====
 * - three image buttons: phone, printer, laptop
 * - labels with price, quantity, subtotal for each product
 * - clicking increases quantity by one; update line subtotal and total
 * - menu item ends sale, resets all totals and quantities
 *
 * ---
 *
 * This is quite a bit of a jump in complexity compared to exercise 1. Not only
 * because there's more components to place in the frame, but there's also state
 * that isn't exactly shared but rather linked and it feels awkward to keep the
 * state within each component like in ex1. The implementation here might be
 * more complicated that it has to be, but this is similar to my favored state
 * modelling approach in android apps.
 *
 * The `ViewModel`
 * ===============
 * This class owns all the state that drives the UI. It exposes an interface to
 * change its state and another interface to allow observation of said changes.
 *
 * Despite the name, this is quite far from the view model concept of the MVVM
 * pattern. Perhaps I should have chosen a different name to avoid overloading
 * the term. I chose the name because this class literally models what the view
 * does. It holds the state being reflected by the view and decides how and
 * when to change it. In pattern-speak, this is halfway between MVVM and
 * MVP-Passive View. If the model events were more declarative or descriptive
 * rather than imperative (the model calling multiple listener methods per event
 * is the indicator of imperativeness), then this would be the procedural
 * version of the MVI pattern. If I had used streams of state and intent sealed
 * interfaces instead of callbacks (event listeners), this would be MVI.
 *
 * The `View`
 * ==========
 * Straightforward enough. The object in charge of laying out components in a
 * window and keeps references to dynamic components so they could be updated
 * as the model changes. The bit I'm not sure I like yet is the fact that the
 * actions object is only accessible in a method. I usually have them as part
 * of the constructor. I think that should still be the case because later on
 * I might need an action in a model listener method.
 * 
 * Also it's a bit weird for me to make the view an abstract class. I usually
 * define an interface and make it a constructor dependency, being composition-
 * over-inheritance person. Here it looks awkward to use composition because
 * there's a view behavior I want to invoke in the setup.  I think a
 * `super.setup()` call makes sense here.
 */

import java.net.URL; 
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;


public interface Exercise2 {
	static void main(String[] args) {
		var products = List.of(
			new Product("Cellphone", locate("/Phone.png"), Millis.of(499.99)),
			new Product("Laserjet", locate("/Printer.png"), Millis.of(119.99)),
			new Product("Laptop", locate("/Laptop.png"), Millis.of(999.99)),
			new Product("iPod Touch XS Pro Max", locate("/Phone.png"), Millis.of(99.99)),
			new Product("Dot Matrix", locate("/Printer.png"), Millis.of(19.99)),
			new Product("Typewriter", locate("/Laptop.png"), Millis.of(9.99))
		);
		var model = new ViewModel(products);
		model.listen(new View() {
			@Override
			protected void setup() {
				try {
					UIManager.setLookAndFeel(new FlatLightLaf());
					JFrame.setDefaultLookAndFeelDecorated(true);
				}
				catch (Exception ex) {
					ex.printStackTrace();
					System.err.println("Failed to load look and feel; using default.");
				}
				super.setup();
			}

			@Override
			protected void show(JFrame frame) {
				frame.getJMenuBar()
					.add(new JMenu("Help"))
					.add(new JMenuItem("About"));
				frame.setTitle("Exercise 2");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.pack();
				frame.setMinimumSize(frame.getSize());
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
		model.refresh();
	}

	private static URL locate(String path) {
		return Exercise2.class.getResource(path);
	}
}
