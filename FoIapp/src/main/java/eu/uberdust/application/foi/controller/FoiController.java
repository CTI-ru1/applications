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

    private static final String URN_FOI = "urn:wisebed:ctitestbed:virtual:" + MainApp.MODE + ":" + MainApp.FOI;

    private static final String SENSOR_SCREENLOCK_REST = "/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:" + MainApp.MODE + ":" + MainApp.FOI + "/capability/urn:wisebed:ctitestbed:node:capability:lockScreen/tabdelimited/limit/1";

    private static final String FOI_CAPABILITIES = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:" + MainApp.MODE + ":" + MainApp.FOI + "/capabilities/json";

    private long lockscreenDelay;

    public long getLockscreenDelay() {
        return lockscreenDelay;
    }

    /**
     * Pir timer.
     */
    private final Timer timer;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static FoiController ourInstance = null;


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


    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private FoiController() {

        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        LOGGER.info("FOI Controller initializing...");

        timer = new Timer();

        LOGGER.info(MainApp.MODE);

        try {
            lockscreenDelay = Long.parseLong(ProfileManager.getInstance().getElement("lockscreen_delay")) * 1000;
        } catch (NumberFormatException npe) {
            lockscreenDelay = 1000;
        }


        String uberdustUrl = PropertyReader.getInstance().getProperties().getProperty("uberdust.url") != null ?
                PropertyReader.getInstance().getProperties().getProperty("uberdust.url") : "uberdust.cti.gr:80";

        uberdustUrl = uberdustUrl.replaceAll(HTTP_PREFIX, "");

        UberdustClient.setUberdustURL(HTTP_PREFIX + uberdustUrl);

        LOGGER.info(MainApp.FOI);

        WSReadingsClient.getInstance().setServerUrl(WS_PREFIX + uberdustUrl + "/readings.ws");


        if ("workstation".equals(MainApp.MODE)) {

            //Subscription for notifications.
            LockManager.getInstance().addObserver(this);
            LuminosityManager.getInstance().addObserver(this);


            // subscribe to specific readings
            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_SCREENLOCK);
            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_LIGHT);

            //Adding Observer for the last readings
            WSReadingsClient.getInstance().addObserver(LockManager.getInstance());
            WSReadingsClient.getInstance().addObserver(LuminosityManager.getInstance());


        } else if ("room".equals(MainApp.MODE)) {

            PresenceManageR.getInstance().addObserver(this);

            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_PIR);               //this.URN_FOI

//            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_LIGHT);

            //Adding Observer for the last readings

            WSReadingsClient.getInstance().addObserver(PresenceManageR.getInstance());

        } else if ("ichatz".equals(MainApp.MODE)) {

            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_SCREENLOCK);
            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_LIGHT);
            WSReadingsClient.getInstance().subscribe(URN_FOI, MainApp.CAPABILITY_PIR);                           //"urn:wisebed:ctitestbed:virtual:room:0.I.2"

        }


        LOGGER.info("FOIController Initialized");

    }


    public void WorkstationHandler(){

        if(LockManager.getInstance().getCurrentState() == LockManager.LOCKED || LuminosityManager.getInstance().getCurrentState() == LuminosityManager.BRIGHT)
        {
            //ZoneManager.getInstance().switchOffAll();
            timer.schedule(new TurnOffTask_2(timer), lockscreenDelay);


        } else if (LockManager.getInstance().getCurrentState() == LockManager.UNLOCKED &&
                    (LuminosityManager.getInstance().getCurrentState() == LuminosityManager.DARKLY || LuminosityManager.getInstance().getCurrentState() == LuminosityManager.TOTAL_DARKNESS )){

            ZoneManageR.getInstance().switchOnFirst();

        }


    }



    @Override
    public void update(Observable o, Object arg) {
        //To change body of implemented methods use File | Settings | File Templates.

        if (o instanceof LockManager) {

            WorkstationHandler();

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

        }else if(o instanceof LuminosityManager){

            WorkstationHandler();


        }
    }



    public static void main(final String[] args) {
        FoiController.getInstance();
    }


}
