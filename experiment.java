/*

                 Orbits in Strongly Curved Spacetime

                            by John Walker
                       http://www.fourmilab.ch/

                This program is in the public domain.

        This abstract class contains the general applet framework.
        It is derived from Applet and manages the interface between
        the applet and the invoking application, starting and
        stopping the animation thread as required.

*/

import java.applet.*;
import java.awt.*;
import java.util.*;
import java.net.*;
import VisualFeedback;

public abstract class experiment extends Applet implements Runnable {
    static String appName = "experiment";       // Experiment name

    VisualFeedback display;
    AudioClip sound;
    Thread animator;
    String audioFile = "bonk.au";
    Button start_stop;
    boolean error = false, running = true; // , quitIt = false;

    //  Parameters from applet invocation

    boolean silent;

    //  RUN  --  Main thread processing

    public void run() {
        while (true) {
//System.out.println("Running = " + running);
            if (running) {
                display.refreshDisplay();
            }
            try {
                animator.sleep(30);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /*  GETPARAMETERINFO  --  Standard applet method that returns an
                              array of arrays of strings describing the
                              parameters this applet accepts.  We append
                              any parameters accepted by the visual
                              feedback program to the end of the list.
                              In doing so we make the assumption that
                              init() will always be called before
                              getParameterInfo().  */

    public String[][] getParameterInfo() {
        String[][] info = {
            { "Sound", "-s/-n", "Sound for fall-in?  Default -n" },
        };
        String[][] merged = new String[info.length][3];
        int i;

        System.arraycopy(info, 0, merged, 0, info.length);
        return merged;
    }

    /*  GETAPPLETINFO  --  Return information about this applet.  */

    public String getAppletInfo() {
        return appName +
               " by John Walker.  Public domain.";
    }

    /*  PROCESS_PARAMETERS  --  Process applet parameters.  All parameters
                                are kept in instance variables of the
                                applet.  */

    protected void process_parameters() {
        String s;

        //  Generate sound ?

        s = getParameter("Sound");
        if (s == null) {
            s = "Silent";
        }
        silent = !s.equalsIgnoreCase("-s");
    }

    //  MAKEDISPLAY  --  Create visual feedback component

    protected abstract void makeDisplay(int width, int height);

    //  INIT  --  Applet is loaded--create our objects and display them

    public abstract void init();

    //  ACTION  --  Respond to events from child components

    public boolean action(Event e, Object arg) {
        if (e.target == start_stop) {
            start_stop.setLabel(running ? " Start " : " Pause ");
            running = !running;
            return true;
        }
        return false;
    }

    //  START  --  Applet loading complete; start animation thread

    public void start() {
        if (animator != null) {
            if (animator.isAlive()) {
                animator.resume();
            } else {
                animator.start();
            }
        }
    }

    //  STOP  --  We're leaving this page--stop animation thread

    public void stop() {
        if (animator != null) {
            animator.suspend();
        }
    }

    //  DESTROY  --  Endsville--destroy the animation thread

    public void destroy() {
        if (animator != null) {
            animator.stop();
        }
    }
}
