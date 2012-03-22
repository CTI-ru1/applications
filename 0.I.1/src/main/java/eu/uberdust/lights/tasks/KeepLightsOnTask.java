package eu.uberdust.lights.tasks;

import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple timer to control the lights.
 */
public class KeepLightsOnTask extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(KeepLightsOnTask.class);

    public static final long DELAY = 30000;

    private final Timer timer;

    public KeepLightsOnTask(final Timer thatTimer) {
        super();
        this.timer = thatTimer;
    }

    @Override
    public final void run() {
        LOGGER.debug("Task to keep Lights on initialized");

        LightController.getInstance().updateLightsState();

        //Re-schedule this timer to run in 5000ms to turn off
        this.timer.schedule(new KeepLightsOnTask(timer), DELAY);
    }
}


