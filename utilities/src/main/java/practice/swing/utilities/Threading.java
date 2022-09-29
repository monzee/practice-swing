package practice.swing.utilities;

import java.util.concurrent.Executor;
import javax.swing.SwingUtilities;


public enum Threading implements Executor {
	ASAP {
		@Override
		public void execute(Runnable block) {
			if (SwingUtilities.isEventDispatchThread()) {
				block.run();
			}
			else {
				SwingUtilities.invokeLater(block);
			}
		}
	},

	LATER {
		@Override
		public void execute(Runnable block) {
			SwingUtilities.invokeLater(block);
		}
	},

	NOW {
		@Override
		public void execute(Runnable block) {
			block.run();
		}
	};
}

