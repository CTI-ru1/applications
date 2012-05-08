package eu.uberdust.lights.tasks;

import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple task to switch off lights.
 */
public class TurnOffTask_4 extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TurnOffTask_4.class);


    private final Timer timer;

    public static final long DELAY = 30000;

    public TurnOffTask_4(final Timer thatTimer) {
        super();
        this.timer = thatTimer;
    }

    @Override
    public final void run() {

        LOGGER.info("TurnOffTask_4: initialized");

        if (LightController.getInstance().isZone1()) {

            if (System.currentTimeMillis() - LightController.getInstance().getLastPirReading() > DELAY) {
                LOGGER.info("TurnOffTask_4: Turn off zone 1");
                LightController.getInstance().controlLight(false, 4);

            } else {
                //Re-schedule this timer to run in 5000ms to turn off
                this.timer.schedule(new TurnOffTask_4(timer), DELAY / 6);
            }


        }
    }

}

