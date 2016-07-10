import acme.MainFrame;
import fourmilab.Energy;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Orbits {
    public static void main(String[] args) {
        final MainFrame frame = new MainFrame(new Energy(), args, 800, 600);
        SwingUtilities.invokeLater(() -> {
            frame.setTitle("Orbits in Strongly Curved Spacetime");
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    System.exit(0);
                }
            });
        });
    }
}
