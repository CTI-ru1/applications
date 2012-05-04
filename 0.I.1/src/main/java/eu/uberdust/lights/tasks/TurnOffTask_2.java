package eu.uberdust.lights.tasks;

/**
 * Created by IntelliJ IDEA.
 * User: dimitris
 * Date: 4/5/12
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */

import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple task to switch off lights.
 */
public class TurnOffTask_2 extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TurnOffTask_2.class);


    private final Timer timer;

    public static final long DELAY = 30000;

    public TurnOffTask_2(final Timer thatTimer) {
        super();
        this.timer = thatTimer;
    }

    @Override
    public final void run() {
        LOGGER.info("TurnOffTask_2: Task to turn off Light_2 initialized");
        if (LightController.getInstance().isScreenLocked()) {

            if (System.currentTimeMillis() - LightController.getInstance().getZone2TurnedOnTimestamp() > DELAY) {
                LOGGER.info("TurnOffTask_2: Turn off zone 2");
                LightController.getInstance().controlLight(false, 2);
            }

        }

    }
}