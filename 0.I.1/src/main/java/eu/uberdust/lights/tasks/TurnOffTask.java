package eu.uberdust.lights.tasks;

import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;

import java.util.TimerTask;

/**
 * Simple task to switch off lights.
 */
public class TurnOffTask extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TurnOffTask.class);

    public static final long DELAY = 30000;

    public TurnOffTask() {
        super();
    }

    @Override
    public final void run() {
        LOGGER.debug("Task to turn off Lights initialized");
        if (LightController.getInstance().isScreenLocked()) {
            LightController.getInstance().controlLight(false, 1);
            LightController.getInstance().controlLight(false, 2);
            LightController.getInstance().controlLight(false, 3);
        }
    }
}

