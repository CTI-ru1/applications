package eu.uberdust.application.foi.task;

import eu.uberdust.MainApp;
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
//        LOGGER.info("Task to turn off Lights initialized");
//        if (FoiController.getInstance().isZone2()) {
//            if (System.currentTimeMillis() - FoiController.getInstance().getLastPirReading() > DELAY) {
//                //turn off zone 2
//                    LOGGER.info("Turn off zone 2");
//                if("ichatz".equals(ProfileManager.getInstance().getElement("mode"))){
//                    if (FoiController.getInstance().isScreenLocked()) {
//                        LOGGER.info("LightTask: Turn off zone 2");
//                        FoiController.getInstance().controlLight(false, Integer.parseInt(MainApp.ZONES[1]));
//
//                        //Re-schedule this timer to run in 30000ms to turn off
//                        this.timer.schedule(new LightTask(timer), DELAY);
//
//                    } else{
//                        timer.schedule(new TurnOffTask_4(timer), TurnOffTask_4.DELAY);
//                        LOGGER.info("EXIT LightTask.");
//                    }
//
//
//                }  else {
//                         if(MainApp.ZONES.length > 2)
//                            { FoiController.getInstance().controlLight(false, Integer.parseInt(MainApp.ZONES[2]));}
//                         else{
//                             FoiController.getInstance().controlLight(false, Integer.parseInt(MainApp.ZONES[1]));
//                            }
//
//                        //Re-schedule this timer to run in 30000ms to turn off
//                        this.timer.schedule(new LightTask(timer), DELAY);
//                   }
//            } else {
//                //Re-schedule this timer to run in 5000ms to turn off
//                this.timer.schedule(new LightTask(timer), DELAY / 6);
//            }
//        } else if (FoiController.getInstance().isZone1()) {
//            if (System.currentTimeMillis() - FoiController.getInstance().getLastPirReading() > 30000) {
//
//                //turn off zone 1
//                if("ichatz".equals(ProfileManager.getInstance().getElement("mode"))){
//
//                    FoiController.getInstance().controlLight(false, Integer.parseInt(MainApp.ZONES[2]));
//
//                }else{
//                    FoiController.getInstance().controlLight(false, Integer.parseInt(MainApp.ZONES[0]));
//                    if(MainApp.ZONES.length > 2){
//                        FoiController.getInstance().controlLight(false, Integer.parseInt(MainApp.ZONES[1]));
//                    }
//                    LOGGER.info("Turn off zone 1");
//                }
//            } else {
//                //Re-schedule this timer to run in 5000ms to turn off
//                this.timer.schedule(new LightTask(timer), DELAY / 6);
//            }
//        }
    }
}
