package eu.uberdust.lights;

import eu.uberdust.MainApp;
import eu.uberdust.communication.UberdustClient;
import eu.uberdust.communication.rest.RestClient;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import eu.uberdust.lights.tasks.LightTask;
import eu.uberdust.lights.tasks.TurnOffTask_2;
import eu.uberdust.lights.tasks.TurnOffTask_3;
import eu.uberdust.lights.tasks.TurnOffTask_4;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;

/**
 * Basic functionality.
 */
public final class FoiController {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FoiController.class);

    private boolean zone1;

    private boolean zone2;

    private boolean zone3;

    private long lastPirReading;

    private long zone1TurnedOnTimestamp;

    private long zone2TurnedOnTimestamp;

    private boolean isScreenLocked;

    private boolean flag;

    public static final int WINDOW = 10;

    public static Queue luminosityReadings = new PriorityQueue(WINDOW);

    private static int i = WINDOW - 1;

    public static final String URN_FOI = "urn:wisebed:ctitestbed:virtual:" + MainApp.FOI;

    public static final String SENSOR_SCREENLOCK_REST = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:" + MainApp.FOI + "/capability/urn:wisebed:ctitestbed:node:capability:lockScreen/tabdelimited/limit/1";

    public static final String USER_PREFERENCES = "http://150.140.16.31/api/v1/foi?identifier=" + MainApp.FOI;

    public static final String FOI_CAPABILITIES = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:" + MainApp.FOI + "/capabilities/json";

    public static final String ACTUATOR_URL = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:" + MainApp.FOI + "/capability/urn:wisebed:node:capability:lz" + MainApp.ZONES[0] + "/json/limit/1";

    public static final String FOI_ACTUATOR = GetJson.getInstance().callGetJsonWebService(ACTUATOR_URL, "nodeId").split("0x")[1];

    public static final String MODE = GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode");

    public static long LOCKSCREEN_DELAY = Long.parseLong(GetJson.getInstance().callGetJsonWebService(FoiController.USER_PREFERENCES, "lockscreen_delay")) * 1000;

    public static long PIR_DELAY = Long.parseLong(GetJson.getInstance().callGetJsonWebService(FoiController.USER_PREFERENCES, "pir_delay")) * 1000;

    private long firstCall = 0;

    /**
     * Pir timer.
     */
    private final Timer timer;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static FoiController ourInstance = null;

    public static double LUM_THRESHOLD_1 = Double.parseDouble(GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "illumination"));  //350

    public static double LUM_THRESHOLD_2 = Double.parseDouble(GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "illumination2"));  //350

    public static boolean BYPASS = false;

    private double lastLumReading;

    private double Median;

    /**
     * FoiController is loaded on the first execution of FoiController.getInstance()
     * or the first access to FoiController.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static FoiController getInstance() {
        synchronized (FoiController.class) {
            if (ourInstance == null) {
                ourInstance = new FoiController();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private FoiController() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        LOGGER.info("FOI Controller initializing...");
        UberdustClient.setUberdustURL("http://uberdust.cti.gr");
        timer = new Timer();

        LOGGER.info("lastLumReading -- " + lastLumReading);
        LOGGER.info("isScreenLocked -- " + isScreenLocked);

        zone1 = false;
        zone2 = false;
        zone3 = false;

        LOGGER.info(MainApp.FOI.split(":")[0]);

        WSReadingsClient.getInstance().setServerUrl("ws://uberdust.cti.gr:80/readings.ws");


        initLum();//RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_LIGHT_READINGS_REST));
        setLastLumReading(0);//setLastLumReading(Double.valueOf(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_LIGHT_READINGS_REST).split("\t")[1]));
        WSReadingsClient.getInstance().subscribe(this.URN_FOI, MainApp.CAPABILITY_LIGHT);

        //Subscription for notifications.
        if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("workstation")) {

            setScreenLocked((Double.valueOf(RestClient.getInstance().callRestfulWebService(this.SENSOR_SCREENLOCK_REST).split("\t")[1]) == 1) || (Double.valueOf(RestClient.getInstance().callRestfulWebService(this.SENSOR_SCREENLOCK_REST).split("\t")[1]) == 3));
            WSReadingsClient.getInstance().subscribe(this.URN_FOI, MainApp.CAPABILITY_SCREENLOCK);

        } else if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("room")) {

            //setScreenLocked(false);
            //DUPLICATE//setLum(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_LIGHT_READINGS_REST));
            WSReadingsClient.getInstance().subscribe(this.URN_FOI, MainApp.CAPABILITY_LIGHT);
            WSReadingsClient.getInstance().subscribe(this.URN_FOI, MainApp.CAPABILITY_PIR);               //this.URN_FOI

        } else if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("ichatz")) {

            //DUPLICATE//setLum(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_LIGHT_READINGS_REST));
            setScreenLocked((Double.valueOf(RestClient.getInstance().callRestfulWebService(this.SENSOR_SCREENLOCK_REST).split("\t")[1]) == 1) || (Double.valueOf(RestClient.getInstance().callRestfulWebService(this.SENSOR_SCREENLOCK_REST).split("\t")[1]) == 3));

            WSReadingsClient.getInstance().subscribe(this.URN_FOI, MainApp.CAPABILITY_SCREENLOCK);
            WSReadingsClient.getInstance().subscribe(this.URN_FOI, MainApp.CAPABILITY_LIGHT);
            WSReadingsClient.getInstance().subscribe(this.URN_FOI, MainApp.CAPABILITY_PIR);                           //"urn:wisebed:ctitestbed:virtual:room:0.I.2"

        }
        //Adding Observer for the last readings
        WSReadingsClient.getInstance().addObserver(new ReadingsObserver());
        LOGGER.info("FOIController Initialized");

    }

    public void setLum(final String readings) {
        for (int k = 0, j = 1; k <= WINDOW - 1; k++, j = j + 3) {

            if (luminosityReadings.size() == 10) {
                luminosityReadings.remove();
            }
            luminosityReadings.add(Double.valueOf(readings.split("\t")[j]));

            LOGGER.info("luminosityReadings[" + k + "]: " + Double.valueOf(readings.split("\t")[j]));

        }

    }

    public void initLum() {
        for (int k = 0; k <= WINDOW - 1; k++) {
            luminosityReadings.add(0.0);
            LOGGER.info(luminosityReadings.toString());
        }
    }


    public void setLastLumReading(final double thatReading) {

        LUM_THRESHOLD_1 = Double.parseDouble(GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "illumination"));  //350
        LUM_THRESHOLD_2 = Double.parseDouble(GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "illumination2"));  //350


        double sum = 0;

        LOGGER.info(" i : " + i);

        luminosityReadings.remove();
        luminosityReadings.add(thatReading);

        LOGGER.info("thatReading : " + thatReading);
        LOGGER.info(" i : " + i);

        LOGGER.info(luminosityReadings.toString());
        for (Object d : luminosityReadings.toArray()) {

            sum += (Double) d;
        }

        LOGGER.info("Median : " + (sum / WINDOW));
        this.Median = sum / WINDOW;

        this.lastLumReading = thatReading;

        if (!MODE.equals("room")) {

            if (!isScreenLocked) {

                if (Median < LUM_THRESHOLD_1 && lastLumReading > LUM_THRESHOLD_2) {

                    controlLight(true, Integer.parseInt(MainApp.ZONES[0]));

                    if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("workstation")) {

                        String[] temp = new String[MainApp.ZONES.length - 1];
                        System.arraycopy(MainApp.ZONES, 1, temp, 0, MainApp.ZONES.length - 1);

                        for (String z : temp) {
                            controlLight(false, Integer.parseInt(z));
                        }

                    } else if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("ichatz")) {

                        controlLight(false, Integer.parseInt(MainApp.ZONES[1]));
                    }

                } else if (Median < LUM_THRESHOLD_2) {

                    if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("ichatz")) {

                        controlLight(true, Integer.parseInt(MainApp.ZONES[0]));
                        controlLight(true, Integer.parseInt(MainApp.ZONES[1]));

                    } else {

                        for (String z : MainApp.ZONES)
                            controlLight(true, Integer.parseInt(z));
                    }

                } else if (Median > LUM_THRESHOLD_1) {

                    for (String z : MainApp.ZONES)
                        controlLight(false, Integer.parseInt(z));
                }
            }
        }

    }

    public void setScreenLocked(final boolean screenLocked) {
        this.isScreenLocked = screenLocked;
        updateLight3();
    }

    public synchronized void updateLight3() {

        LUM_THRESHOLD_1 = Double.parseDouble(GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "illumination"));  //350
        LUM_THRESHOLD_2 = Double.parseDouble(GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "illumination2"));  //350

        if (!isScreenLocked) {
            if (Median < LUM_THRESHOLD_1 && lastLumReading > LUM_THRESHOLD_2) {

                controlLight(true, Integer.parseInt(MainApp.ZONES[0]));

                if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("workstation")) {

                    String[] temp = new String[MainApp.ZONES.length - 1];
                    System.arraycopy(MainApp.ZONES, 1, temp, 0, MainApp.ZONES.length - 1);

                    for (String z : temp) {
                        controlLight(false, Integer.parseInt(z));
                    }

                } else if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("ichatz")) {

                    controlLight(false, Integer.parseInt(MainApp.ZONES[1]));
                }
            } else if (Median < LUM_THRESHOLD_2) {

                if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("ichatz")) {

                    controlLight(true, Integer.parseInt(MainApp.ZONES[0]));
                    controlLight(true, Integer.parseInt(MainApp.ZONES[1]));

                } else {

                    for (String z : MainApp.ZONES)
                        controlLight(true, Integer.parseInt(z));
                }

            } else if (Median > LUM_THRESHOLD_1) {

                for (String z : MainApp.ZONES)
                    controlLight(false, Integer.parseInt(z));
            }

        } else if (isScreenLocked) {

            LOCKSCREEN_DELAY = Long.parseLong(GetJson.getInstance().callGetJsonWebService(FoiController.USER_PREFERENCES, "lockscreen_delay")) * 1000;

            timer.schedule(new TurnOffTask_2(timer), LOCKSCREEN_DELAY);

            if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("workstation")) {

                String[] temp = new String[MainApp.ZONES.length - 1];
                System.arraycopy(MainApp.ZONES, 1, temp, 0, MainApp.ZONES.length - 1);

                for (String z : temp) {
                    controlLight(false, Integer.parseInt(z));
                }

            } else if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("ichatz")) {

                //timer.schedule(new TurnOffTask_2(timer), LOCKSCREEN_DELAY);
                controlLight(false, Integer.parseInt(MainApp.ZONES[1]));
            }
        }

    }

    public long getLastPirReading() {
        return lastPirReading;
    }

    public void setLastPirReading(final long thatReading) {

        this.lastPirReading = thatReading;

        if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("ichatz")) {
            updateLightsState();
        } else {
            pirHandler();
        }


    }


    public synchronized void updateLightsState() {

        if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("ichatz")) {

            LOGGER.info("ichatz MODE");

            if (isScreenLocked) {

                if (Median < LUM_THRESHOLD_1) {
                    //turn on lights
                    turnOnLights();

                }

            } else if (!isScreenLocked) {

                if (Median < LUM_THRESHOLD_1) {

                    turnOnLight_1();

                }

            }

        } else if (Median < LUM_THRESHOLD_1) {

            LOGGER.info("TURN LIGHTS OOOOOOOOOOOOOON");
            //turn on lights
            pirHandler();

        }

    }

    private synchronized void turnOnLights() {


        if (!zone1) {
            controlLight(true, Integer.parseInt(MainApp.ZONES[2]));
            zone1TurnedOnTimestamp = lastPirReading;
            timer.schedule(new LightTask(timer), LightTask.DELAY);
        } else if (!zone2) {
            controlLight(true, Integer.parseInt(MainApp.ZONES[2]));
            if (lastPirReading - zone1TurnedOnTimestamp > 15000) {
                controlLight(true, Integer.parseInt(MainApp.ZONES[1]));
                zone2TurnedOnTimestamp = lastPirReading;
            }
        } else {

            controlLight(true, Integer.parseInt(MainApp.ZONES[1]));
            controlLight(true, Integer.parseInt(MainApp.ZONES[2]));
        }

    }


    public synchronized void pirHandler() {

        if (!zone1) {

            if (Double.parseDouble(GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "pir_delay")) > 0)
                turnOnLight_1();
            else {
                controlLight(true, Integer.parseInt(MainApp.ZONES[0]));        //3

                if (MainApp.ZONES.length > 2) {
                    controlLight(true, Integer.parseInt(MainApp.ZONES[1]));
                }

                zone1TurnedOnTimestamp = lastPirReading;
                timer.schedule(new LightTask(timer), LightTask.DELAY);
            }

        } else if (!zone2 && (MainApp.ZONES.length > 1)) {

            controlLight(true, Integer.parseInt(MainApp.ZONES[0]));          //3

            if (MainApp.ZONES.length > 2) {
                controlLight(true, Integer.parseInt(MainApp.ZONES[1]));
            }

            if (lastPirReading - zone1TurnedOnTimestamp > 15000) {

                if (MainApp.ZONES.length > 2) {
                    controlLight(true, Integer.parseInt(MainApp.ZONES[2]));
                } else {
                    controlLight(true, Integer.parseInt(MainApp.ZONES[1]));
                }

                zone2TurnedOnTimestamp = lastPirReading;
            }
        } else if (MainApp.ZONES.length > 1) {

            controlLight(true, Integer.parseInt(MainApp.ZONES[0]));
            controlLight(true, Integer.parseInt(MainApp.ZONES[1]));

            if (MainApp.ZONES.length > 2) {
                controlLight(true, Integer.parseInt(MainApp.ZONES[2]));
            }

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

                if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("ichatz")) {
                    controlLight(true, Integer.parseInt(MainApp.ZONES[2]));
                } else {
                    controlLight(true, Integer.parseInt(MainApp.ZONES[0]));
                }
                PIR_DELAY = Long.parseLong(GetJson.getInstance().callGetJsonWebService(FoiController.USER_PREFERENCES, "pir_delay")) * 1000;
                timer.schedule(new TurnOffTask_4(timer), PIR_DELAY);
            }
        }

    }

    public double getLastLumReading() {
        return this.lastLumReading;
    }

    public double getMedian() {
        return this.Median;
    }


    public boolean isScreenLocked() {
        return this.isScreenLocked;
    }

    public boolean isZone1() {
        return zone1;
    }

    public boolean isZone2() {
        return zone2;
    }

    public boolean isZone3() {
        return zone3;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setFlag(final boolean thatFlag) {
        this.flag = thatFlag;

    }

    public synchronized void controlLight(final boolean value, final int zone) {

        if (GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode").equals("ichatz")) {

            if (zone == Integer.parseInt(MainApp.ZONES[2])) {
                zone1 = value;
            } else if (zone == Integer.parseInt(MainApp.ZONES[1])) {
                zone2 = value;
            } else {
                zone3 = value;
            }

        } else {

            if (zone == Integer.parseInt(MainApp.ZONES[0]) || (MainApp.ZONES.length > 2 && zone == Integer.parseInt(MainApp.ZONES[1]))) {

                zone1 = value;

            } else if ((MainApp.ZONES.length == 2 && zone == Integer.parseInt(MainApp.ZONES[1])) || (MainApp.ZONES.length > 2 && zone == Integer.parseInt(MainApp.ZONES[2]))) {

                zone2 = value;
            }
        }


        BYPASS = Boolean.parseBoolean(GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "bypass"));

        if (!BYPASS) {
            UberdustClient.getInstance().sendCoapPost(FOI_ACTUATOR, "lz" + zone, value ? "1" : "0"); //FOI_ACTUATOR
        }
    }

    public static void main(final String[] args) {
        FoiController.getInstance();
    }

}