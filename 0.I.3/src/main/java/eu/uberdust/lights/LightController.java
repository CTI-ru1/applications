package eu.uberdust.lights;

import eu.uberdust.MainApp;
import eu.uberdust.communication.rest.RestClient;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import eu.uberdust.lights.tasks.KeepLightsOn;
import eu.uberdust.lights.tasks.TurnOffTask_2;
import eu.uberdust.lights.tasks.TurnOffTask_3;
import eu.uberdust.lights.tasks.TurnOffTask_4;
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

    private boolean zone3;

    private boolean isAmethystLocked;

    private boolean isSilverLocked;

    private boolean isBlancoLocked;

    private boolean isYellowLocked;

    private boolean flag;

    public static final int MAX_TRIES = 3;


    /**
     * Pir timer.
     */
    private final Timer timer;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static LightController ourInstance = null;

    public static final double LUM_THRESHOLD_1 = 350;                       //350

    private double lastLumReading;

    private long lastPirReading;

    private long zone1TurnedOnTimestamp = 0;

    private long zone2TurnedOnTimestamp = 0;

    private long firstCall = 0;

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

        //setLastPirReading(Long.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_PIR_REST).split("\t")[0]));
        setLastLumReading(Double.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_LIGHT_EXT_REST).split("\t")[1]));
        setAmethystLocked(Double.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_SCREENLOCK_SILVER_REST).split("\t")[1]) == 1);
        setSilverLocked(Double.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_SCREENLOCK_AMETHYST_REST).split("\t")[1]) == 1);
        setBlancoLocked(Double.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_SCREENLOCK_BLANCO_REST).split("\t")[1]) == 1);
        setYellowLocked(Double.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_SCREENLOCK_YELLOW_REST).split("\t")[1]) == 1);

        LOGGER.info("lastLumReading -- " + lastLumReading);
        LOGGER.info("isYellowLocked -- " + isYellowLocked);
        LOGGER.info("isBlancoLocked -- " + isBlancoLocked);
        LOGGER.info("isAmethystLocked -- " + isAmethystLocked);
        LOGGER.info("isSilverLocked -- " + isSilverLocked);
        zone1 = false;
        zone2 = false;
        zone3 = false;
        flag = false;


        WSReadingsClient.getInstance().setServerUrl("ws://uberdust.cti.gr:80/readings.ws");

        //Subscription for notifications.
       // WSReadingsClient.getInstance().subscribe(MainApp.URN_SENSOR_PIR, MainApp.CAPABILITY_PIR);
        WSReadingsClient.getInstance().subscribe(MainApp.URN_SENSOR_LIGHT, MainApp.CAPABILITY_LIGHT);
        WSReadingsClient.getInstance().subscribe(MainApp.URN_SENSOR_SCREENLOCK_AMETHYST, MainApp.CAPABILITY_SCREENLOCK);
        WSReadingsClient.getInstance().subscribe(MainApp.URN_SENSOR_SCREENLOCK_SILVER, MainApp.CAPABILITY_SCREENLOCK);
        WSReadingsClient.getInstance().subscribe(MainApp.URN_SENSOR_SCREENLOCK_BLANCO, MainApp.CAPABILITY_SCREENLOCK);
        WSReadingsClient.getInstance().subscribe(MainApp.URN_SENSOR_SCREENLOCK_YELLOW, MainApp.CAPABILITY_SCREENLOCK);

        //Adding Observer for the last readings
        WSReadingsClient.getInstance().addObserver(new LastReadingsObserver());

        timer.schedule(new KeepLightsOn(timer), KeepLightsOn.DELAY);

    }


    public void setLastLumReading(final double thatReading) {
        this.lastLumReading = thatReading;

        if (lastLumReading < LUM_THRESHOLD_1) {
            if (!isYellowLocked) {
                controlLight(true, 1);
            }
            if (!isBlancoLocked) {
                controlLight(true, 2);
            }
            if (!isAmethystLocked) {
                controlLight(true, 3);
            }
            if (!isSilverLocked) {
                controlLight(true, 4);
            }
        } else {
            controlLight(false, -1);
        }
    }


    public void setFlag(final boolean thatFlag) {
        this.flag = thatFlag;

    }

    public void setLastPirReading(final long thatReading) {

        this.lastPirReading = thatReading;
        updateLightsState();

    }


    public void setAmethystLocked(final boolean screenLocked) {
        this.isAmethystLocked = screenLocked;

        if (!isAmethystLocked) {
            if (lastLumReading < LUM_THRESHOLD_1) {
                controlLight(true, 3);
            }
        } else if (isAmethystLocked) {
           // timer.schedule(new TurnOffTask_2(timer, 3, "amethyst"), TurnOffTask_2.DELAY);
            controlLight(false, 3);
        }

    }

    public void setSilverLocked(final boolean screenLocked) {
        this.isSilverLocked = screenLocked;

        if (!isSilverLocked) {
            if (lastLumReading < LUM_THRESHOLD_1) {
                controlLight(true, 4);
            }
        } else if (isSilverLocked) {
            //timer.schedule(new TurnOffTask_2(timer, 4, "silver"), TurnOffTask_2.DELAY);
            controlLight(false, 4);
        }
    }

    public void setBlancoLocked(final boolean screenLocked) {
        this.isBlancoLocked = screenLocked;

        if (!isBlancoLocked) {
            if (lastLumReading < LUM_THRESHOLD_1) {
                controlLight(true, 2);
            }
        } else if (isBlancoLocked) {
            //timer.schedule(new TurnOffTask_2(timer, 2, "blanco"), TurnOffTask_2.DELAY);
            controlLight(false, 2);
        }
    }

    public void setYellowLocked(final boolean screenLocked) {
        this.isYellowLocked = screenLocked;

        if (!isYellowLocked) {
            if (lastLumReading < LUM_THRESHOLD_1) {
                controlLight(true, 1);
            }
        } else if (isYellowLocked) {
            //timer.schedule(new TurnOffTask_2(timer, 1, "yellow"), TurnOffTask_2.DELAY);
            controlLight(false, 1);
        }
    }

    public long getLastPirReading() {
        return this.lastPirReading;
    }

    public double getLastLumReading() {
        return this.lastLumReading;
    }


    public synchronized void updateLightsState() {

        if (lastLumReading < LUM_THRESHOLD_1) {
            //turn on lights
            turnOnLight_1();


        } else {
            //turn off lights
            controlLight(false, -1);
        }

    }

    public synchronized void turnOnLight_1() {

        LOGGER.info("turnOnLight_1()");

        if (!flag) {
            firstCall = lastPirReading;
            flag = true;
            timer.schedule(new TurnOffTask_3(timer), TurnOffTask_3.DELAY);
        } else if (!zone1) {
            LOGGER.info("lastPirReading - firstCall = " + (lastPirReading - firstCall));
            if (lastPirReading - firstCall > 15000) {
                controlLight(true, 5);
                timer.schedule(new TurnOffTask_4(timer), TurnOffTask_4.DELAY);
            }
        }

    }


    public boolean isAmethystLocked() {
        return this.isAmethystLocked;
    }

    public boolean isSilverLocked() {
        return this.isSilverLocked;
    }

    public boolean isBlancoLocked() {
        return this.isBlancoLocked;
    }

    public boolean isYellowLocked() {
        return this.isYellowLocked;
    }

    public boolean isZone1() {
        return zone1;
    }

    public boolean isZone2() {
        return zone2;
    }

    public synchronized void controlLight(final boolean value, final int zone) {
        if (zone == 1) {
            zone1 = value;
        } else if (zone == 2) {
            zone2 = value;
        } else {
            zone3 = value;
        }

        final String zonef;

        if (zone == -1) {
            zonef = "ff";
        } else {
            zonef = "" + zone;
        }

        final String link = new StringBuilder(MainApp.LIGHT_CONTROLLER).append(zonef).append(",").append(value ? 1 : 0).toString();
        LOGGER.info(link);
        try {
            restCall(link);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void restCall(final String link) throws InterruptedException {

        for (int i = 0; i < MAX_TRIES; i++) {
            RestClient.getInstance().callRestfulWebService(link);
            Thread.sleep(200);
        }

    }

    public static void main(final String[] args) {
        LightController.getInstance();
    }

}

