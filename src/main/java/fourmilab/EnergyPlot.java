package fourmilab;/*

                 Orbits in Strongly Curved Spacetime

                            by John Walker
                       http://www.fourmilab.ch/

                This program is in the public domain.

        This module implements the Effective Potential energy plot.
        It also serves as the master for the Gravity Well and Orbit
        Plot displays, passing updates it receives to these peers so
        they can update synchronously.

*/

import java.awt.*;

public class EnergyPlot extends VisualFeedback {
    private final String build = "";

    boolean initialised = false;
    Image chart;
    OrbitViewer ov = null;
    EnergyPlot sv = null;
    int reentry = 0;

    public double ANG = 3.57,         // Angular momentum
                  M = 1.0,            // Mass
                  FROM = 0.1,         // Minimum radius to plot
                  TO = 12.0;          // Maximum radius to plot
    private double LOW = 0.92, HIGH = 1.05; // Range of chart

    public double currentR = 4.86, Eparticle = 0;
    public double phi = 0.0, phiscale = 1;
    public double tee = 0.0, teescale = 0.1;

    private double eminv = HIGH;
    private double eminR = 0;
    private int direction = -1;
    
    // Constructors

    public EnergyPlot(int width, int height) {
        super(width, height);
        initialised = true;
    }

    //  SETORBITVIEWER  --  Set peer orbit viewer

    public void setOrbitViewer(OrbitViewer o) {
        ov = o;
    }

    //  SETSCHWARZSCHILD  --  Set peer Schwarzschild metric viewer

    public void setSchwarzschild(EnergyPlot s) {
        sv = s;
    }

    //  UPDATEPEERS  --  Update peer displays, if any

    public void updatePeers(double rn, double phin, double teen) {
        if (ov != null) {
            ov.currentR = rn;
            ov.setParticlePosition(rn, phin, teen);
        }
        if (sv != null) {
            sv.currentR = rn;
            sv.setParticlePosition(rn, phin, teen);
        }
    }

    /*  SETPARTICLEPOSITION  --  This is called by peers to
                                 reset our state when a mouse click
                                 occurs in a peer component.  */

    public void setParticlePosition(double srad, double sphi, double stee) {
        currentR = srad;
        Eparticle = gEnergy(currentR);
        direction = currentR > eminR ? 1 : -1;
        phi = sphi;
        tee = stee;
        refreshDisplay();
        initialised = true;
    }

    //  RESTART  --  Reset when global parameter changed

    public void restart() {
        if (ANG * ANG < 12 * (M * M)) {
            eminR = Double.MAX_VALUE;
            eminv = 0.9;
         } else {
             eminR = ANG * (ANG + Math.sqrt(ANG * ANG - 12 * (M * M))) / (2 * M);
             eminv = gEnergy(eminR);
            TO = eminR * (7 / 4.0);
             if (sv != null && ov != null) {
                 sv.TO = ov.TO = TO;
                 ov.remake();
             }
        }
        validate();
        repaint();
    }

    //  MOUSEDOWN  --  Reset radius by mouse click in window

    public boolean mouseDown(Event evt, int x, int y) {
        currentR = FROM + (x * (TO - FROM)) / (size.width - 1);
//System.out.println("currentR = " + currentR);
        phi = 0;
        tee = 0;
        Eparticle = gEnergy(currentR);
        direction = currentR > eminR ? 1 : -1;
        updatePeers(currentR, phi, tee);
        if (ov != null) {
            ov.validate();
        }
        repaint();
        return true;
    }

    //  GENERGY  --  Calculate effective potential for a given radius

    private final double gEnergy(double r) {
        return Math.sqrt((1.0 - ((2.0 * M) / r)) * (1.0 + (ANG * ANG) / (r * r)));
    }

    //  FILLCIRCLE  --  Draw a filled circle with a given centre and radius

    static final void fillCircle(Graphics g, int x, int y, int radius) {
        g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    //  PAINTWINDOW  --  Paint the component window
    
    public void paintWindow(Graphics g) {
        if (initialised) {
            int s, x, y, px = 0, py = 0;
            double dr;
            double dr_dt;


            if (chart == null) {
                Graphics gfx;
                double plot;
                boolean scaling;
                String title = "Effective Potential" + build;
                FontMetrics fm;

                //  Create chart image

                chart = createImage(size.width, size.height);
                gfx = chart.getGraphics();
                gfx.setColor(Color.lightGray);
                gfx.fillRect(0, 0, size.width, size.height);

                gfx.setColor(Color.yellow);

                scaling = true;
                LOW = Double.MAX_VALUE;
                HIGH = Double.MIN_VALUE;
                eminv = Double.MAX_VALUE;
                if (ANG * ANG < 12 * (M * M)) {
                    eminR = Double.MAX_VALUE;
                    eminv = 0.9;
                } else {
                    eminR = ANG * (ANG + Math.sqrt(ANG * ANG - 12 * (M * M))) / (2 * M);
                    eminv = gEnergy(eminR);
                    TO = sv.TO = ov.TO = eminR * (7 / 4.0);
                    ov.remake();
                }
                while (true) {

                    /* Plot the effective potential curve.  We have to
                       explicitly exclude points within the event horizon
                       because the test for infinite values doesn't work
                       on some browser implementations of Java. */

                    for (x = 0; x < size.width; x++) {
                        double sx;

                        plot = FROM + (x * (TO - FROM)) / (size.width - 1);
                        if (plot > (2.0 * M)) {
                            plot = gEnergy(plot);
                            if (scaling && !Double.isInfinite(plot) &&
                                !Double.isNaN(plot)) {
                                LOW = Math.min(LOW, plot);
                                HIGH = Math.max(HIGH, plot);
                            } else {
                                if (plot > HIGH) {
                                    plot = HIGH;
                                }
                                if (plot >= LOW) {
                                    y = (int) ((size.height - 1) -
                                        (size.height - 1) * (plot - LOW) / (HIGH - LOW));
                                    gfx.drawLine(x, size.height - 1, x, y);
                                }
                            }
                        }
                    }
                    if (scaling) {
                        scaling = false;
                        LOW = eminv - (HIGH - eminv) / 10;
                        HIGH += (HIGH - LOW) / 10;
                    } else {
                        break;
                    }
                }

                //  Plot minimum energy point on chart

                if (eminR < Double.MAX_VALUE) {
                    int ex = (int) (((eminR - FROM) * size.width) / (TO - FROM));

                    gfx.setColor(Color.green);
                    gfx.drawLine(ex, 0, ex, size.height - 1);
                }

                //  Paint title at top of window

                gfx.setColor(Color.black);
                gfx.setFont(new Font("Helvetica", Font.BOLD, size.height / 12));
                fm = gfx.getFontMetrics();
                gfx.drawString(title,
                    (size.width - fm.stringWidth(title)) / 2,
                    fm.getAscent());

                Eparticle = gEnergy(currentR);
            }

            //  Paint the underlying chart image.

            g.drawImage(chart, 0, 0, this);

            //  Plot particle at current radius on effective potential curve

            s = (int) (((currentR - FROM) * size.width) / (TO - FROM));
            g.setColor(Color.red);
            y = (int) ((size.height - 1) -
                    (size.height - 1) * (gEnergy(currentR) - LOW) / (HIGH - LOW));
            fillCircle(g, s, y, 5);

            //  Update current R, phi, and tee

            if (currentR > 0) {

                updatePeers(currentR, phi, tee);
                dr_dt = Math.sqrt(Eparticle * Eparticle - gEnergy(currentR) * gEnergy(currentR));
                currentR += direction * dr_dt;

                /* If the particle fell into the black hole, play the
                    sound accompanying that event. */

                if (currentR <= (2 * M)) {
                    currentR = -1;    // Mark particle as having fallen in
                    if (sound != null) {
                        sound.play();
                    }
                    updatePeers(currentR, phi, tee);   // Tell peers to hide particle

                /* When particle's energy increases compared to the last
                   time and we were in the direction of increased radius,
                   inform the orbit viewer that we've just passed aphelion,
                   which should be marked on the plot to show precession. */

                } else if (gEnergy(currentR) >= Eparticle) {
                    if (direction > 0) {
                        if (ov != null) {
                            ov.markApAstron();
                        }
                    }
                    direction *= -1;
                    s = (int) (((currentR - FROM) * size.width) / (TO - FROM));
                    while (gEnergy(FROM + ((TO - FROM) * s) / size.width) >=
                        Eparticle) {
                        s += direction;
                    }
                    currentR = FROM + ((TO - FROM) * s) / size.width;
                }
                double dPhi = ANG / (currentR * currentR);
                phi += dPhi;

                if (currentR > 2 * M) {
                    double dTee = (1 - (2 * M) / currentR);
                    tee += dTee;
                }
            }
        }
    }

    /*  VALIDATE  --  We need to know when the layout manager
                      changes our size so we can regenerate the
                      face bitmap on the next paint request.  */

    public void validate() {
        super.validate();
        size = new Dimension(size());
        chart = null;
    }
}
