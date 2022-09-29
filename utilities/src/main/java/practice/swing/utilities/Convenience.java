package practice.swing.utilities;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import javax.swing.AbstractButton;
import javax.swing.GroupLayout;


public interface Convenience {
  default void mark(Runnable dispose) {
  }

  default void dispose() {
  }

  default void onClick(AbstractButton button, Runnable listener) {
    ActionListener actionListener = (ignored) -> listener.run();
    button.addActionListener(actionListener);
    mark(() -> button.removeActionListener(actionListener));
  }

  default Pulse clicks(AbstractButton button) {
    var signal = new Signal();
    onClick(button, signal::send);
    return signal;
  }

  default void onType(Component component, Consumer<Character> listener) {
    var keyListener = new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        listener.accept(e.getKeyChar());
      }
    };
    component.addKeyListener(keyListener);
    mark(() -> component.removeKeyListener(keyListener));
  }

  default void onAdd(Container container, Consumer<Component> listener) {
	  var adapter = new ContainerAdapter() {
		  @Override
		  public void componentAdded(ContainerEvent e) {
			  listener.accept(e.getChild());
		  }
	  };
	  container.addContainerListener(adapter);
	  mark(() -> container.removeContainerListener(adapter));
  }
}

