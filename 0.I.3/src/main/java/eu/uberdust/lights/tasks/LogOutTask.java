package eu.uberdust.lights.tasks;

import eu.uberdust.MainApp;
import eu.uberdust.communication.rest.RestClient;
import eu.uberdust.lights.LightController;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: dimitris
 * Date: 5/15/12
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogOutTask extends TimerTask{


 private static final Logger LOGGER = Logger.getLogger(LogOutTask.class);


    private final Timer timer;

    public static final long DELAY = 1800000;



    public LogOutTask(final Timer thatTimer) {
        super();
        this.timer = thatTimer;

    }

    @Override
    public final void run() {
        LOGGER.info("LogOutTask: Initiated ");
       /*
        LightController.getInstance().setLastStatus("silver", Long.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.STATUS_SILVER_REST).split("\t")[0]));
        LightController.getInstance().setLastStatus("amethyst", Long.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.STATUS_AMETHYST_REST).split("\t")[0]));
        LightController.getInstance().setLastStatus("blanco", Long.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.STATUS_BLANCO_REST).split("\t")[0]));
        LightController.getInstance().setLastStatus("yellow", Long.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.STATUS_YELLOW_REST).split("\t")[0]));
        */
        this.timer.schedule(new LogOutTask(timer), DELAY);
    }


}