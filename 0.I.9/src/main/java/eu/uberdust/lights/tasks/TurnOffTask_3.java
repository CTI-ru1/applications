package eu.uberdust.lights.tasks;

import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple task to switch off lights.
 */
public class TurnOffTask_3 extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TurnOffTask_3.class);


    private final Timer timer;

    public static final long DELAY = 16000;

    public TurnOffTask_3(final Timer thatTimer) {
        super();
        this.timer = thatTimer;
    }

    @Override
    public final void run() {

        LOGGER.info("TurnOffTask_3: set flag to false");
        LightController.getInstance().setFlag(false);


    }
}
