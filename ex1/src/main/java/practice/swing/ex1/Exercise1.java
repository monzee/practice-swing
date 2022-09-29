package practice.swing.ex1;

import javax.swing.UIManager;

import practice.swing.utilities.Threading;


public class Exercise1 {
    // - text area with vertical scrollbar, "nice border"
    // - show horizontal scrollbar as needed
    // - two buttons below: append incrementing number to text, line break
    public static void main(String[] args) {
        Threading.LATER.execute(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception ex) {
                System.err.println("Failed to load system look and feel. Using default.");
            }
            new View();
        });
    }
}

