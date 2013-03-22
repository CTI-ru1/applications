package eu.uberdust.application.foi.controller;


import eu.uberdust.application.foi.MainApp;
import eu.uberdust.application.foi.manager.*;
import eu.uberdust.application.foi.task.TurnOffTask_2;
import eu.uberdust.communication.UberdustClient;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import eu.uberdust.util.PropertyReader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.*;

/**
 * Basic functionality.
 */
public final class FoiController implements Observer {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FoiController.class);
    private static final String HTTP_PREFIX = "http://";
    private static final String WS_PREFIX = "ws://";

    private static String mode = "room";//GetJson.getInstance().callGetJsonWebService(USER_PREFERENCES, "mode");

    private static final String URN_FOI = "urn:wisebed:ctitestbed:virtual:" + mode + ":" + MainApp.FOI;

    private static final String SENSOR_SCREENLOCK_REST = "/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:" + mode + ":" + MainApp.FOI + "/capability/urn:wisebed:ctitestbed:node:capability:lockScreen/tabdelimited/limit/1";

    private static final String FOI_CAPABILITIES = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:" + mode + ":" + MainApp.FOI + "/capabilities/json";

    private static final String ACTUATOR_URL = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:" + mode + ":" + MainApp.FOI + "/capability/urn:wisebed:node:capability:lz" + MainApp.ZONES[0] + "/json/limit/1";

    private long lockscreenDelay;

    public long getLockscreenDelay() {
        return lockscreenDelay;
    }

    private long pirDelay;

    public long getPirDelay() {
        return pirDelay;
    }

    /**
     * Pir timer.
     */
    private final Timer timer;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static FoiController ourInstance = null;

    private double lumThreshold1;

    private double lumThreshold2;

    private long zone1TurnedOnTimestamp;


    private String uberdustUrl;

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

    private void updateLum1Threshold() {
        try {
            lumThreshold1 = Double.parseDouble(ProfileManager.getInstance().getElement("illumination"));  //350
        } catch (NullPointerException npe) {
            lumThreshold1 = 350;
        } catch (NumberFormatException nfe) {
            lumThreshold1 = 350;
        }
    }

    private void updateLum2Threshold() {
        try {
            lumThreshold2 = Double.parseDouble(ProfileManager.getInstance().getElement("illumination2"));  //350
        } catch (NullPointerException npe) {
            lumThreshold2 = 350;
        } catch (NumberFormatException nfe) {
            lumThreshold2 = 350;
        }
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private FoiController() {

        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        LOGGER.info("FOI Controller initializing...");
        //mode = ProfileManager.getInstance().getElement("mode");

        timer = new Timer();

        LockManager.getInstance().addObserver(this);
        //PresenseManager.getInstance().addObserver(this);
        PresenceManageR.getInstance().addObserver(this);

        LOGGER.info(mode);
        try {
            lockscreenDelay = Long.parseLong(ProfileManager.getInstance().getElement("lockscreen_delay")) * 1000;
        } catch (NumberFormatException npe) {
            lockscreenDelay = 1000;
        }
        try {
            pirDelay = Long.parseLong(ProfileManager.getInstance().getElement("pir_delay")) * 1000;
        } catch (NumberFormatException npe) {
            pirDelay = 1000;
        }
        updateLum1Threshold();
        updateLum2Threshold();


        String uberdustUrl = PropertyReader.getInstance().getProperties().getProperty("uberdust.url") != null ?
                PropertyReader.getInstance().getProperties().getProperty("uberdust.url") : "uberdust.cti.gr:80";
        uberdustUrl = uberdustUrl.replaceAll(HTTP_PREFIX, "");

        UberdustClient.setUberdustURL(HTTP_PREFIX + uberdustUrl);

        LOGGER.info(MainApp.FOI);

        WSReadingsClient.getInstance().setServerUrl(WS_PREFIX + uberdustUrl + "/readings.ws");


        //Subscription for notifications.
//        String mode = ProfileManager.getInstance().getElement("mode");
        if ("workstation".equals(mode)) {

            //setScreenLocked((Double.valueOf(RestClient.getInstance().callRestfulWebService(HTTP_PREFIX + uberdustUrl + SENSOR_SCREENLOCK_REST).split("\t")[1]) == 1) || (Double.valueOf(RestClient.getInstance().callRestfulWebService(HTTP_PREFIX + uberdustUrl + SENSOR_SCREENLOCK_REST).split("\t")[1]) == 3));

            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_SCREENLOCK);


        } else if ("room".equals(mode)) {
            //setScreenLocked(false);
            //DUPLICATE of (1) + not needed//setLum(RestClient.getInstance().callRestfulWebService(MainApp.SENSOR_LIGHT_READINGS_REST));
            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_PIR);               //this.URN_FOI
//            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_LIGHT);

        } else if ("ichatz".equals(mode)) {

            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_SCREENLOCK);
            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_LIGHT);
            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_PIR);                           //"urn:wisebed:ctitestbed:virtual:room:0.I.2"

        }
        //Adding Observer for the last readings

        //WSReadingsClient.getInstance().addObserver(PresenseManager.getInstance());
        WSReadingsClient.getInstance().addObserver(PresenceManageR.getInstance());
        WSReadingsClient.getInstance().addObserver(LockManager.getInstance());
        LOGGER.info("FOIController Initialized");

    }




    public static void main(final String[] args) {
        FoiController.getInstance();
    }



    @Override
    public void update(Observable o, Object arg) {
        //To change body of implemented methods use File | Settings | File Templates.

        if (o instanceof LockManager) {

            if(LockManager.getInstance().getCurrentState() == LockManager.LOCKED)
            {
                //ZoneManager.getInstance().switchOffAll();
                timer.schedule(new TurnOffTask_2(timer), lockscreenDelay);


            } else if (LockManager.getInstance().getCurrentState() == LockManager.UNLOCKED){

                ZoneManager.getInstance().switchOnFirst();

            }

        }else if(o instanceof PresenceManageR){

            switch (PresenceManageR.getInstance().getCurrentState()) {
                case PresenceManageR.EMPTY:
                    ZoneManageR.getInstance().switchOffAll();
                    break;
                case PresenceManageR.LEFT:
                    ZoneManageR.getInstance().switchLastOff();
                    break;
                case PresenceManageR.NEW_ENTRY:
                    ZoneManageR.getInstance().switchOnFirst();
                    break;
                case PresenceManageR.OCCUPIED:
                    ZoneManageR.getInstance().switchOnAll();
                    break;
            }

        }
    }
}
