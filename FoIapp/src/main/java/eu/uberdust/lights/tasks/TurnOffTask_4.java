package eu.uberdust.lights.tasks;

import eu.uberdust.MainApp;
import eu.uberdust.lights.FoiController;
import eu.uberdust.lights.GetJson;
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

    public static final long DELAY = 30000;        //180000


    public TurnOffTask_4(final Timer thatTimer) {
        super();
        this.timer = thatTimer;
    }

    @Override
    public final void run() {

        LOGGER.info("TurnOffTask_4: initialized");
        LOGGER.info("TurnOffTask_4 DELAY : " + FoiController.getInstance().getPirDelay());

        if (FoiController.getInstance().isZone1()) {

            if (System.currentTimeMillis() - FoiController.getInstance().getLastPirReading() > FoiController.getInstance().getPirDelay()) {
                LOGGER.info("TurnOffTask_4: Turn off zone 1");
                if (GetJson.getInstance().callGetJsonWebService(FoiController.USER_PREFERENCES, "mode").equals("ichatz")) {
                    FoiController.getInstance().controlLight(false, Integer.parseInt(MainApp.ZONES[2]));
                } else {
                    FoiController.getInstance().controlLight(false, Integer.parseInt(MainApp.ZONES[0]));
                }

            } else {
                //Re-schedule this timer to run in 5000ms to turn off
                this.timer.schedule(new TurnOffTask_4(timer), FoiController.getInstance().getPirDelay() / 6);
            }


        }
    }

}

