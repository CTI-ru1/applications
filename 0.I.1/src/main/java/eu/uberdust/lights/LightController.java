package eu.uberdust.lights;

import eu.uberdust.MainApp;
import eu.uberdust.communication.rest.RestClient;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import eu.uberdust.lights.tasks.KeepLightsOnTask;
import eu.uberdust.lights.tasks.TurnOffTask;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Timer;

/**
 * Basic functionality.
 */
public final class LightController {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LightController.class);

    private boolean zone1;

    private boolean zone2;

    private boolean isScreenLocked;

    /**
     * Pir timer.
     */
    private final Timer timer;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static LightController ourInstance = null;

    private static final double LUM_THRESHOLD = 200;

    private double lastLumReading;


    /**
     * LightController is loaded on the first execution of LightController.getInstance()
     * or the first access to LightController.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static LightController getInstance() {
        synchronized (LightController.class) {
            if (ourInstance == null) {
                ourInstance = new LightController();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private LightController() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        LOGGER.info("Light Controller initialized");
        timer = new Timer();

        setLastReading(Double.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_LIGHT_EXT_REST).split("\t")[1]));
        setScreenLocked(Double.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_SCREENLOCK_REST).split("\t")[1]) == 1);

        LOGGER.info("lastLumReading -- " + lastLumReading);
        LOGGER.info("isScreenLocked -- " + isScreenLocked);
        zone1 = false;
        zone2 = false;


        WSReadingsClient.getInstance().setServerUrl("ws://uberdust.cti.gr:80/readings.ws");

        //Subscription for notifications.
        //WSReadingsClient.getInstance().subscribe(MainApp.URN_SENSOR_PIR, MainApp.CAPABILITY_PIR);
        WSReadingsClient.getInstance().subscribe(MainApp.URN_SENSOR_LIGHT, MainApp.CAPABILITY_LIGHT);
        WSReadingsClient.getInstance().subscribe(MainApp.URN_SENSOR_SCREENLOCK, MainApp.CAPABILITY_SCREENLOCK);

        //Adding Observer for the last readings
        WSReadingsClient.getInstance().addObserver(new LastReadingsObserver());

        timer.schedule(new KeepLightsOnTask(timer), KeepLightsOnTask.DELAY);
    }


    public void setLastReading(final double thatReading) {
        this.lastLumReading = thatReading;
        updateLightsState();

    }

    public void setScreenLocked(final boolean screenLocked) {
        isScreenLocked = screenLocked;
        updateLightsState();
    }

    public synchronized void updateLightsState() {
        if (isScreenLocked) {
            //turn off lights
            turnOffLights();

        } else {
            if (lastLumReading > LUM_THRESHOLD) {
                //turn off lights
                turnOffLights();

            } else {
                //turn on lights
                turnOnLights();
            }
        }
    }

    private void turnOnLights() {
        controlLight(true, 2);
        controlLight(true, 3);
    }

    private void turnOffLights() {
        if (zone1 || zone2) {
            controlLight(false, 2);
            timer.schedule(new TurnOffTask(), TurnOffTask.DELAY);
        }
    }

    public boolean isScreenLocked() {
        return isScreenLocked;
    }

    public synchronized void controlLight(final boolean value, final int zone) {
        if (zone == 1) {
            zone1 = value;
        } else {
            zone2 = value;
        }
        final String link = new StringBuilder(MainApp.LIGHT_CONTROLLER).append(zone).append(",").append(value ? 1 : 0).toString();
        LOGGER.info(link);

        RestClient.getInstance().callRestfulWebService(link);

    }

    public static void main(final String[] args) {
        LightController.getInstance();
    }
}
