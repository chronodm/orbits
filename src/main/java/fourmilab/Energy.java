package fourmilab;/*

                 Orbits in Strongly Curved Spacetime

                            by John Walker
                       http://www.fourmilab.ch/

                This program is in the public domain.

        This is the main applet program.  It is derived from the
        generic applet "experiment", which contains the framework
        for setting up the applet and managing the animation.

*/

import java.awt.*;

public class Energy extends Experiment {
    protected EnergyPlot bdisp;
    protected OrbitViewer odisp;
    protected EnergyPlot sdisp;
    TextField angMomentum, mass, maxRad;
    Rectangle r;

    //  MAKEDISPLAY  --  Create display components within window of given size

    protected void makeDisplay(int width, int height) {

        //  display: Effective potential plot

        display = (VisualFeedback) (bdisp = new EnergyPlot(width / 2, height / 2 - 15));

        //  odisp: Orbit viewer

        odisp = new OrbitViewer(width / 2, height - 30);

        //  sdisp: Schwarzschild embedding diagram plot

        sdisp = new Schwarzschild(width / 2, height / 2 - 15);

        //  Link each component to its peers

        bdisp.setOrbitViewer(odisp);
        bdisp.setSchwarzschild((EnergyPlot) sdisp);
        sdisp.setOrbitViewer(odisp);
        sdisp.setSchwarzschild((EnergyPlot) bdisp);
    }

    //  INIT  --  Applet is loaded--create our objects and display them

    public void init() {
        int pheight, i;
        String s;
        GridBagLayout gribble = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        Panel p;
        Component c;

        setLayout(gribble);
        process_parameters();         // Parse parameters and set mode variables

        r = bounds();
        pheight = r.height;

        makeDisplay(r.width, r.height);

        display.setBackground(getBackground());

        start_stop = new Button(" Pause ");

        if (!silent) {
            sound = getAudioClip(getCodeBase(), audioFile);
            display.setAudioClip(sound);
        }

        /* Lay out components in applet frame.  This code is
           completely ad hoc, having been tweaked to work in
           as many different Java environments as possible.  Do
           not assume that an "insignificant" change to the
           following won't break the window layout in some horrible
           way on some platform. */

        Panel p1 = new Panel();
        p1.setLayout(new GridLayout(2, 1));
        p1.add(display);
        p1.add(sdisp);

        gc.fill = GridBagConstraints.BOTH;
        gc.insets.right = 4;
        gc.gridheight = 1;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.weighty = 100;
        gc.gridx = 0;
        gc.gridy = 0;
        gribble.setConstraints(p1, gc);
        add(p1);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.gridheight = 1;
        gc.insets.right = 0;
        gribble.setConstraints(odisp, gc);
        add(odisp);

        p = new Panel();
        c = new Label("Angular momentum: ");
        p.add(c);
        angMomentum = new TextField("3.57", 7);
        p.add(angMomentum);
        c = new Label("Mass: ");
        p.add(c);
        mass = new TextField("1", 4);
        p.add(mass);
        c = new Label("Max radius: ");
        p.add(c);
        maxRad = new TextField("12", 4);
        p.add(maxRad);
        p.add(start_stop);

        gc.insets.top = 8;
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weighty = 1;
        gc.gridwidth = 2;
        gc.gridheight = 1;
        gc.anchor = GridBagConstraints.CENTER;
        gc.fill = GridBagConstraints.REMAINDER;
        gribble.setConstraints(p, gc);
        add(p);

        validate();

        bdisp.restart();

//      list(System.out, 0);          // Show what the layout mangler did to us

        animator = new Thread(this);
    }

    //  SETMAXRAD  --  Update maximum radius box when radius rescaled

    private final void setMaxRad() {
        double t = bdisp.TO;

        if (t < 1) {
            t = (((int) t) * 100) / 100.0;
        } else if (t < 10) {
            t = (((int) t) * 10) / 10.0;
        } else {
            t = (int) t;
        }
        maxRad.setText((new Double(t)).toString());
    }

    //  ACTION  --  Respond to events from child components

    public boolean action(Event e, Object arg) {
        if (e.target == angMomentum) {
            try {
                double v = (new Double(angMomentum.getText())).doubleValue();

                bdisp.ANG = v;
                sdisp.ANG = v;
                odisp.ANG = v;
                bdisp.restart();
                sdisp.restart();
                odisp.restart();
                setMaxRad();
            } catch (NumberFormatException x) {
                angMomentum.setText("");
            }
            return true;
        } else if (e.target == mass) {
            try {
                double v = (new Double(mass.getText())).doubleValue();

                bdisp.M = v;
                sdisp.M = v;
                odisp.M = v;
                bdisp.restart();
                sdisp.restart();
                odisp.restart();
                setMaxRad();
            } catch (NumberFormatException x) {
                mass.setText("");
            }
            return true;
        } else if (e.target == maxRad) {
            try {
                double v = (new Double(maxRad.getText())).doubleValue();

                bdisp.TO = v;
                sdisp.TO = v;
                odisp.TO = v;
                bdisp.restart();
                sdisp.restart();
                odisp.restart();
            } catch (NumberFormatException x) {
                maxRad.setText("");
            }
            return true;
        }
        return super.action(e, arg);
    }
}
