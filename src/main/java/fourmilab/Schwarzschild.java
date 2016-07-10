package fourmilab;/*

                 Orbits in Strongly Curved Spacetime

                            by John Walker
                       http://www.fourmilab.ch/

                This program is in the public domain.

        This file creates the Schwarzschild geometry plot.  It is
        called from energyPlot to update the position of the test
        particle along the section of the throat.

*/

import java.awt.*;

public class Schwarzschild extends EnergyPlot {
    private final String build = "";

    //  Constructor

    public Schwarzschild(int width, int height) {
        super(width, height);
        chart = null;
    }

    //  SETPARTICLEPOSITION  --  Update radius, angle, and proper time

    public void setParticlePosition(double srad, double sphi, double stee) {
        currentR = srad;
        phi = sphi;
        tee = stee;
        refreshDisplay();
        initialised = true;
    }

    //  PARABOLA  --  Plot parabolic Schwarzschild geometry for given radius

    private final int parabola(double r) {
        if (r < 2 * M) {
            return -1;
        }
        return (int) (Math.sqrt(r - 2 * M) * (size.height * 0.9) /
                        Math.sqrt(TO - 2 * M));
    }

    //  PAINTWINDOW  --  Paint the component window
    
    public void paintWindow(Graphics g) {
        if (initialised) {
            int s, x, y, px = 0, py = 0;

            if (chart == null) {
                Graphics gfx;
                double plot;
                String title = "Gravity Well" + build;
                FontMetrics fm;

                //  Create chart image

                chart = createImage(size.width, size.height);
                gfx = chart.getGraphics();
                gfx.setColor(Color.lightGray);
                gfx.fillRect(0, 0, size.width, size.height);

                gfx.setColor(Color.blue);
                for (x = 0; x < size.width; x++) {
                    double sx;

                    plot = FROM + (x * (TO - FROM)) / (size.width - 1);
                    py = parabola(plot);
                    if (py >= 0) {
                        y = (size.height - 1) - py;
                        gfx.drawLine(x, size.height - 1, x, y);
                    }
                }

                //  Paint title at top of window

                gfx.setColor(Color.black);
                gfx.setFont(new Font("Helvetica", Font.BOLD, size.height / 12));
                fm = gfx.getFontMetrics();
                gfx.drawString(title,
                    (size.width - fm.stringWidth(title)) / 2,
                    fm.getAscent());
            }

            /*  Refresh the chart image.  If we have a new chart,
                if this is a repaint when the window is uncovered,
                or if it's the first time we get here, repaint the
                entire background.  Otherwise, set the clip region
                to only repaint the area covered by the old
                position marker.  */

            g.drawImage(chart, 0, 0, this);

            //  Paint test particle at current energy level

            if (currentR >= FROM) {
                s = (int) (((currentR - FROM) * size.width) / (TO - FROM));
                y = (size.height - 1) - parabola(currentR);
                g.setColor(Color.red);
                fillCircle(g, s, y, 5);
            }
        }
    }
}
