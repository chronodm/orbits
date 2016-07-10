import acme.MainFrame;
import fourmilab.Energy;

import javax.swing.*;
import java.applet.Applet;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Orbits {
    public static void main(String[] args) {
        Applet applet = new Energy();
        MainFrame frame = new MainFrame(applet, args, 800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
