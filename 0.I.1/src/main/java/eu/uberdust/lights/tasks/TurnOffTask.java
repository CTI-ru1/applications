package eu.uberdust.lights.tasks;

import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple task to switch off lights.
 */
public class TurnOffTask extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TurnOffTask.class);


    private final Timer timer;

    public static final long DELAY = 60000;

    public TurnOffTask(final Timer thatTimer) {
        super();
        this.timer = thatTimer;
    }

    @Override
    public final void run() {
        LOGGER.info("Task to turn off Light_1 initialized");
   //     if (!LightController.getInstance().isScreenLocked()) {
        if (System.currentTimeMillis() - LightController.getInstance().getLastPirReading() > DELAY) {
                LOGGER.info("Turn off zone 1");
            LightController.getInstance().setFlag(false);
            LightController.getInstance().controlLight(false, 1);
        }else {
            //Re-schedule this timer to run in 5000ms to turn off
            this.timer.schedule(new TurnOffTask(timer), DELAY / 6);
        }

    }
}

