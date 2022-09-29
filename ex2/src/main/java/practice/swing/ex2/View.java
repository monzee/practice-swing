package practice.swing.ex2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import practice.swing.utilities.Convenience;
import practice.swing.utilities.GroupGrid;
import practice.swing.utilities.Pulse;


public abstract class View implements ViewModel.EventListener, Convenience {

	private static class Row implements Convenience {
		final JButton doBuy;
		final JLabel theQuantity = new JLabel("0 pcs", SwingConstants.CENTER);
		final JLabel theSubtotal = new JLabel(Millis.ZERO.toString(), SwingConstants.TRAILING);
		final Pulse clicks;

		Row(String name, URL image, Millis price) {
			doBuy = new JButton(
				"<html><center><h2>" + name + "</h2>" + price + "</center></html>",
				rescaled(image)
			);
			doBuy.setIconTextGap(20);
			doBuy.setBorder(new EmptyBorder(10, 10, 10, 10));
			doBuy.setMaximumSize(new Dimension(500, Short.MAX_VALUE));
			theSubtotal.setBorder(new EmptyBorder(0, 5, 0, 0));
			theSubtotal.setAlignmentX(Component.RIGHT_ALIGNMENT);
			theSubtotal.setMinimumSize(new Dimension(100, -1));
			clicks = clicks(doBuy);
		}

		void attachTo(Container panel) {
			panel.add(doBuy);
			panel.add(Box.createHorizontalGlue());
			panel.add(theQuantity);
			panel.add(theSubtotal);
		}

		void update(int quantity, Millis subtotal) {
			var units = quantity == 1 ? " pc" : " pcs";
			theQuantity.setText(quantity + units);
			theSubtotal.setText(subtotal.toString());
		}

		static ImageIcon rescaled(URL imagePath) {
			var icon = new ImageIcon(imagePath);
			var image = icon.getImage();
			icon.setImage(image.getScaledInstance(100, -1, Image.SCALE_SMOOTH));
			return icon;
		}
	}

	private final Map<Product, Row> rows = new LinkedHashMap<>();
	protected JLabel totalAmount;

	/**
	 * Initialize the environment before any Swing component is created.
	 *
	 * This hook is called in the UI thread. Override this method to setup the
	 * look-and-feel. The label {@link #totalAmount} must also be set here,
	 * otherwise the {@link #onReady(Set<Product>, Intent)} method will throw an
	 * NPE later. Call {@code super.setup()} at the end to setup the default
	 * label component.
	 *
	 * You can also initialize here any other components you might want to add
	 * to the frame and use later in a view model hook.
	 */
	protected void setup() {
		totalAmount = new JLabel(Millis.ZERO.toString());
		totalAmount.setBorder(new EmptyBorder(15, 5, 15, 0));
		totalAmount.setFont(totalAmount.getFont().deriveFont(Font.BOLD));
	}

	/**
	 * Perform final setup on the window and show it.
	 *
	 * @param frame The window with all the components added in.
	 */
	abstract protected void show(JFrame frame);

	@Override
	public final void onReady(Set<Product> products, ViewModel.Intent will) {
		setup();
		var frame = new JFrame();
		var menuBar = new JMenuBar();
		var menu = menuBar.add(new JMenu("File"));
		var exit = new JMenuItem("Exit", KeyEvent.VK_X);
		onClick(menu.add(new JMenuItem("Settle order", KeyEvent.VK_S)), will::settle);
		onClick(menu.add(exit), frame::dispose);
		menu.setMnemonic(KeyEvent.VK_F);
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		frame.setJMenuBar(menuBar);
		var grid = new GroupGrid(4).withColumnAt(3, GroupLayout.Alignment.TRAILING);
		for (var product : products) {
			var row = new Row(product.name(), product.image(), product.unitPrice());
			rows.put(product, row);
			row.attachTo(grid.createRow(GroupLayout.Alignment.CENTER));
			row.clicks.receive(() -> will.order(product));
		}
		var lastRow = grid.createRow(GroupLayout.Alignment.BASELINE);
		var empty = new Dimension(0, 0);
		lastRow.add(Box.createRigidArea(empty));
		lastRow.add(Box.createRigidArea(empty));
		lastRow.add(new JLabel("Total"));
		lastRow.add(totalAmount);
		grid.attachTo(frame.getContentPane());
		show(frame);
	}

	@Override
	public void onUpdate(Product key, int quantity) {
		rows.get(key).update(quantity, key.unitPrice().times(quantity));
	}

	@Override
	public void onUpdate(Millis total) {
		totalAmount.setText(total.toString());
	}
}

