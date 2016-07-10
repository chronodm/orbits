package fourmilab;/*

                 Orbits in Strongly Curved Spacetime

                            by John Walker
                       http://www.fourmilab.ch/

                This program is in the public domain.

        This abstract class is derived from Canvas and provides
        general drawing surface services for the specific display
        components derived from it.

*/

import java.applet.AudioClip;
import java.awt.*;

public abstract class VisualFeedback extends Canvas {
    protected int cWidth, cHeight;
    protected AudioClip sound = null;
    protected Dimension size;
    protected int maxamp = Integer.MIN_VALUE, minamp = Integer.MAX_VALUE;
    
    // Constructors

    public VisualFeedback(int width, int height) {
        cWidth = width;
        cHeight = height;
        setSize(width, height);
    }

    //  SETAUDIOCLIP  --  Specify the audio clip to be played (if any)

    public void setAudioClip(AudioClip clip) {
        sound = clip;
    }

    //  REFRESHDISPLAY  --  Refresh the on-screen display

    public void refreshDisplay() {
        change();
    }

    /*  SETSIZE  --  Change the size of the display component.  */

    public void setSize(int width, int height) {
        cWidth = width;
        cHeight = height;
        size = new Dimension(width, height);
    }

    //  Layout manager interface functions

    //  PREFERREDSIZE  --  Indicate how large we'd like to be

    public Dimension preferredSize() {
        return new Dimension(cWidth, cHeight);
    }
    
    //  MINIMUMSIZE  --  Indicate the minimum we can squeeze into

    public Dimension minimumSize() {
        return new Dimension(320, 200);
    }

    //  CHANGE --  Update the component's state

    protected void change() {
        repaint();
    }

    /*  UPDATE  --  Update the image.  We override the default
                    implementation since we know we're repainting
                    the entire component every time and thus don't
                    need the clear to background colour before the
                    paint.  Using the default would result in flicker
                    during image update.  */

    public void update(Graphics g) {
        paint(g);
    }

    /*  PAINTWINDOW  --  Generate the visual feedback image.
                         This is separate from paint() to allow it
                         to be overridden in derived classes without
                         replicating the final status display logic
                         in paint().  */

    protected abstract void paintWindow(Graphics g);

    //  PAINT  --  Paint the component
    
    public void paint(Graphics g) {
        paintWindow(g);
    }
}
