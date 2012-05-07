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

    private boolean isScreenLocked;

    private int zone;

    final String node;

    public TurnOffTask_2(final Timer thatTimer, final int thatZone, final String thatNode) {
        super();
        this.timer = thatTimer;
        this.zone = thatZone;
        this.node = thatNode;

    }

    @Override
    public final void run() {
        LOGGER.info("TurnOffTask_2: Task to turn off Light_2 initialized");

        if (node.equals("amethyst")) {
            isScreenLocked = LightController.getInstance().isAmethystLocked();
        } else if (node.equals("silver")) {
            isScreenLocked = LightController.getInstance().isSilverLocked();
        } else if (node.equals("blanco")) {
            isScreenLocked = LightController.getInstance().isBlancoLocked();
        } else if (node.equals("yellow")) {
            isScreenLocked = LightController.getInstance().isYellowLocked();
        }

        if (isScreenLocked) {
            LOGGER.info("TurnOffTask_2: Turn off zone " + zone + " " + node + " " + isScreenLocked);
            LightController.getInstance().controlLight(false, zone);
        }

    }


}