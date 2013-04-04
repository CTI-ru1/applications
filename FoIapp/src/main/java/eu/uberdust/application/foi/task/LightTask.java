package eu.uberdust.application.foi.task;

import eu.uberdust.application.foi.manager.PresenceManageR;
import eu.uberdust.application.foi.manager.RoomZoneManager;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/14/11
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class LightTask extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LightTask.class);

    public static final long DELAY = 30000;

    private final Timer timer;

    public LightTask(final Timer thatTimer) {
        super();
        this.timer = thatTimer;
    }

    @Override
    public final void run() {
        LOGGER.info("Task to turn off Lights initialized");
        if (RoomZoneManager.getInstance().getLastStatus()) {
            if (System.currentTimeMillis() - PresenceManageR.getInstance().getLastPirReading() > DELAY) {
                //turn off zone 2
                LOGGER.info("Turn off last light level");

                RoomZoneManager.getInstance().switchLastOff();

                //Re-schedule this timer to run in 30000ms to turn off
                this.timer.schedule(new LightTask(timer), DELAY);
            } else {
                //Re-schedule this timer to run in 5000ms to turn off
                this.timer.schedule(new LightTask(timer), DELAY / 6);
            }
        } else if (RoomZoneManager.getInstance().getFirstStatus()) {
            if (System.currentTimeMillis() - PresenceManageR.getInstance().getLastPirReading() > 30000) {

                RoomZoneManager.getInstance().switchOffAll();

                LOGGER.info("Turn off first light level");
            } else {
                //Re-schedule this timer to run in 5000ms to turn off
                this.timer.schedule(new LightTask(timer), DELAY / 6);
            }
        }
    }
}
