package practice.swing.ex1;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import practice.swing.utilities.Variable;


class View extends JFrame {
    private final JButton doAppend = new JButton("append '1'");
    private final JButton doBreak = new JButton("new line");
    private final JTextArea theText = new JTextArea("");

    View() {
        var col = new Box(BoxLayout.PAGE_AXIS);
        var row = new Box(BoxLayout.LINE_AXIS);
        var scroller = new JScrollPane(
            theText,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        var number = new Variable<Integer>(1);
        number.onSet((n) -> {
            var prev = n - 1;
            theText.append("" + prev);
            doAppend.setText("append '" + n + "'");
        });
        doAppend.addActionListener(refocusAfter(() -> number.update((n) -> n + 1)));
        doBreak.addActionListener(refocusAfter(() -> theText.append("\n")));
        scroller.setPreferredSize(new Dimension(250, 350));
        scroller.setBorder(new BevelBorder(BevelBorder.LOWERED));
        col.setBorder(new EmptyBorder(12, 12, 12, 12));
        col.add(scroller);
        col.add(Box.createVerticalStrut(12));
        col.add(row);
        row.add(doAppend);
        row.add(Box.createHorizontalStrut(12));
        row.add(doBreak);

        setTitle("Exercise 1");
        setContentPane(col);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private ActionListener refocusAfter(Runnable action) {
        return (_e) -> {
            action.run();
            theText.requestFocusInWindow();
        };
    }
}

