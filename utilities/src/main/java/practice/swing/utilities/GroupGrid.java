package practice.swing.utilities;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;	
import javax.swing.GroupLayout;


public class GroupGrid {

	public interface Setup {
		void accept(
			GroupLayout layout,
			GroupLayout.Group horizontal,
			GroupLayout.Group vertical
		);
	}

	private record ParallelSpec(
		GroupLayout.Alignment alignment,
		boolean resizable
	) {}

	private final int columns;
	private final Queue<Component> cells = new ArrayDeque<>();;
	private final List<ParallelSpec> rowSpecs = new ArrayList<>();
	private final ParallelSpec[] colSpecs;
	private Setup after;

	public GroupGrid(int columns) {
		this.columns = columns;
		colSpecs = new ParallelSpec[columns];
	}

	public GroupGrid after(Setup callback) {
		after = callback;
		return this;
	}
	
	public GroupGrid withColumnAt(int index, GroupLayout.Alignment alignment) {
		return withColumnAt(index, alignment, true);
	}

	public GroupGrid withFixedWidthColumnAt(int index) {
		return withFixedWidthColumnAt(index, GroupLayout.Alignment.LEADING);
	}

	public GroupGrid withFixedWidthColumnAt(int index, GroupLayout.Alignment alignment) {
		return withColumnAt(index, alignment, false);
	}

	private GroupGrid withColumnAt(
		int index,
		GroupLayout.Alignment alignment,
		boolean resizable
	) {
		if (index < 0 || index >= columns) {
			throw new IllegalArgumentException(""
				+ "Bad column index; must be between 0 and "
				+ (columns - 1)
				+ " inclusive."
			);
		}
		colSpecs[index] = new ParallelSpec(alignment, resizable);
		return this;
	}

	public Container createRow() {
		return createRow(GroupLayout.Alignment.LEADING);
	}

	public Container createFixedHeightRow() {
		return createFixedHeightRow(GroupLayout.Alignment.LEADING);
	}

	public Container createRow(GroupLayout.Alignment alignment) {
		return createRow(alignment, true);
	}

	public Container createFixedHeightRow(GroupLayout.Alignment alignment) {
		return createRow(alignment, false);
	}

	private Container createRow(GroupLayout.Alignment alignment, boolean resizable) {
		rowSpecs.add(new ParallelSpec(alignment, resizable));
		return new Container() {
			int remaining = columns;

			@Override
			protected void addImpl(Component child, Object constraints, int index) {
				if (remaining == 0) {
					throw new IllegalStateException(""
						+ "Too many calls; this grid can only have "
						+ columns
						+ " columns."
					);
				}
				cells.add(child);
				remaining -= 1;
			}
		};
	}

	public GroupLayout attachTo(Container root) {
		var layout = new GroupLayout(root);
		root.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		var horiz = layout.createParallelGroup();
		var vert = layout.createSequentialGroup();
		var gridCols = layout.createSequentialGroup();
		var cols = new GroupLayout.Group[columns];
		for (var i = 0; i < columns; i++) {
			var spec = colSpecs[i];
			var col = spec == null ?
				layout.createParallelGroup() :
				layout.createParallelGroup(spec.alignment, spec.resizable);
			gridCols.addGroup(col);
			cols[i] = col;
		}
		for (var spec : rowSpecs) {
			var row = layout.createParallelGroup(spec.alignment, spec.resizable);
			for (var col : cols) {
				var cell = cells.remove();
				col.addComponent(cell);
				row.addComponent(cell);
			}
			vert.addGroup(row);
		}
		horiz.addGroup(gridCols);
		if (after != null) {
			after.accept(layout, horiz, vert);
		}
		layout.setHorizontalGroup(horiz);
		layout.setVerticalGroup(vert);
		return layout;
	}
}

