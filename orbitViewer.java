/*

                 Orbits in Strongly Curved Spacetime

                            by John Walker
                       http://www.fourmilab.ch/

                This program is in the public domain.

        This file creates the orbital plot window.  It is derived
        from energyPlot, which handles interface with the applet
        and housekeeping.

*/

import java.awt.*;

public class orbitViewer extends energyPlot {
    private final String build = "";

    Polygon orbiTrail, apAstron;
    int dimension = 0, lPx = -1, lPy = -1;
    Image offscreen = null;

    //  Constructor

    public orbitViewer(int width, int height) {
        super(width, height);
    }

    //  SETPARTICLEPOSITION  --  Update radius, angle, and proper time

    public void setParticlePosition(double srad, double sphi, double stee) {
        currentR = srad;
        phi = sphi;
        tee = stee;
        refreshDisplay();
        initialised = true;
    }

    //  MOUSEDOWN  --  Override to clear trails

    public boolean mouseDown(Event evt, int x, int y) {
        validate();
        repaint();
        return true;
    }

    //  PAINTWINDOW  --  Paint the component window
    
    public void paintWindow(Graphics g) {
        if (initialised) {
            int s, px, py;
            boolean fulltrail = false;
            double dr;
            Graphics gos;

            if (chart == null) {
                Graphics gfx;
                int x, y;
                double plot;
                String title = "Orbit Plot" + build;
                FontMetrics fm;

                if (offscreen != null) {
                    offscreen.flush();
                    offscreen = null;
                }

                lPx = lPy = -1;       // Mark no image to refresh

                //  Create chart image

                dimension = Math.min(size.width, size.height);

                chart = createImage(size.width, size.height);
                gfx = chart.getGraphics();
                gfx.setColor(Color.black);
                gfx.fillRect(0, 0, size.width, size.height);
                orbiTrail = new Polygon();
                apAstron = new Polygon();

                //  Paint black hole in the centre

                gfx.setColor(Color.lightGray);
                fillCircle(gfx, size.width / 2, size.height / 2,
                           Math.max(3, (int) ((2 * M) * (dimension) / (TO - FROM))));
System.err.println("Width = " + size.width +
    " Height = " + size.height +
    " M = " + M + " dimension = " + dimension +
    " TO = " + TO + " FROM = " + FROM);

                //  Paint title at top of window

                gfx.setColor(Color.green);
                gfx.setFont(new Font("Helvetica", Font.BOLD, size.height / 24));
                fm = gfx.getFontMetrics();
                gfx.drawString(title,
                    (size.width - fm.stringWidth(title)) / 2,
                    fm.getAscent());

                offscreen = createImage(size.width, size.height);
                gos = offscreen.getGraphics();
                gos.drawImage(chart, 0, 0, this);
                lPx = -1;
                fulltrail = true;
            }

            gos = offscreen.getGraphics();

            //  Paint orbital trail

            if (orbiTrail.npoints > 0) {
                gos.setColor(Color.green);
                /*  Why don't we use drawPolygon?  Because it doesn't
                    work in some brain-dead browser Java implementations! */
//              gos.drawPolygon(orbiTrail);
                if (fulltrail) {
                    for (int i = 0; i < orbiTrail.npoints - 1; i++) {
                        gos.drawLine(orbiTrail.xpoints[i], orbiTrail.ypoints[i],
                                   orbiTrail.xpoints[i + 1], orbiTrail.ypoints[i + 1]);
                    }
                } else {
                    if (orbiTrail.npoints > 1) {
                        gos.drawLine(orbiTrail.xpoints[orbiTrail.npoints - 2], orbiTrail.ypoints[orbiTrail.npoints - 2],
                            orbiTrail.xpoints[orbiTrail.npoints - 1], orbiTrail.ypoints[orbiTrail.npoints - 1]);
                    }
                }
            }

            //  Mark apastra so far

            if (apAstron.npoints > 0) {
                gos.setColor(Color.yellow);
                gos.drawPolygon(apAstron);
            }

            g.drawImage(offscreen, 0, 0, null);

            /*  Paint test particle in its orbit.  Note that the test particle
                is painted directly onto the screen, on top of the underlying
                image.  Since we refresh the base image for every frame, this
                avoids damage to the trail caused by the test particle. */

            if (currentR >= 0) {
                px = (int) (Math.cos(phi * phiscale) * ((currentR - FROM) * (dimension / 2)) / TO);
                py = (int) (Math.sin(phi * phiscale) * ((currentR - FROM) * (dimension / 2)) / TO);
                lPx = size.width / 2 + px;
                lPy = size.height / 2 + py;

                g.setColor(Color.red);
                fillCircle(g, lPx, lPy, 5);
                orbiTrail.addPoint(lPx, lPy);
            }
        }
    }

    //  MARKAPASTRON  --  Mark current point as apastron

    public void markApAstron() {
        int px, py;

        px = (int) (Math.cos(phi * phiscale) * ((currentR - FROM) * (dimension / 2)) / TO);
        py = (int) (Math.sin(phi * phiscale) * ((currentR - FROM) * (dimension / 2)) / TO);
        apAstron.addPoint(size.width / 2, size.height / 2);
        apAstron.addPoint(size.width / 2 + px, size.height / 2 + py);
        apAstron.addPoint(size.width / 2, size.height / 2);
    }

    //  REMAKE  --  Remake display when parameter changes

    public void remake() {
        if (chart != null) {
            chart.flush();
            chart = null;
            repaint();
        }
    }
}
