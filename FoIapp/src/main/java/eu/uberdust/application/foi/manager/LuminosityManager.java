package eu.uberdust.application.foi.manager;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import org.apache.log4j.Logger;

import java.util.*;


public class LuminosityManager extends Observable implements Observer {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LuminosityManager.class);

    /**
     * Contains all States for all known sensors.
     */
    private Map<String, Double> states;

    /**
     * Our Instance.
     */
    private static LuminosityManager instance = null;

    /**
     * DARKLY State Indicator.
     */
    public static final int DARKLY = 1;

    /**
     * TOTAL_DARKNESS State Indicator.
     */
    public static final int TOTAL_DARKNESS = 2;

    /**
     * BRIGHT State Indicator.
     */
    public static final int BRIGHT = 3;


    /**
     * Current State of the Luminosity level.
     */
    private int currentState;

    /**
     * First Luminosity Threshold
     */
    private double lumThreshold1;

    /**
     * Second Luminosity Threshold
     */
    private double lumThreshold2;

    /**
     * Latest Light Reading
     */
    private double LatestLightReading;

    public static final int WINDOW = 10;

    public static Queue luminosityReadings = new PriorityQueue(WINDOW);


    public int getCurrentState() {

        return  currentState;

    }




    /**
     * Updates and Returns the current state of the operation.
     *
     * @return {DARKLY , TOTAL_DARKNESS}
     */

    public int setCurrentState() {
        //FSM
        //BRIGHT-->DARKLY-->TOTAL_DARKNESS

        if ( lumThreshold1 < LatestLightReading && LatestLightReading < lumThreshold2 ) {

            currentState = DARKLY;
            LOGGER.info("DARKLY");

        } else if(LatestLightReading < lumThreshold2) {

            currentState = TOTAL_DARKNESS;
            LOGGER.info("TOTAL_DARKNESS");

        } else if(LatestLightReading > lumThreshold1){

            currentState = BRIGHT;
            LOGGER.info("BRIGHT");

        }
        this.setChanged();
        this.notifyObservers();
        return currentState;
    }

    /**
     * Default Constructor.
     */
    public LuminosityManager() {
        reset();
        LOGGER.info("-----LuminosityManager initializing------");
    }

    /**
     * Singleton Get Instance.
     *
     * @return the single instance.
     */
    public static LuminosityManager getInstance() {
        synchronized (LuminosityManager.class) {
            if (instance == null) {
                instance = new LuminosityManager();
            }
            return instance;
        }
    }

    /**
     * Adds a new reading from Uberdust.
     *
     * @param reading the new reading.
     */
    public void addReading(Message.NodeReadings.Reading reading) {
        //interested only in lockScreen events

        if ("urn:wisebed:node:capability:light".equals(reading.getCapability())) {       //urn:wisebed:ctitestbed:node:capability:light

            LOGGER.info("New Reading for Light Capability");

            //update the state
            states.put(reading.getNode(), reading.getDoubleReading());
            LatestLightReading = reading.getDoubleReading();

            //calculate te current Status FSM

            updateLum1Threshold();
            updateLum2Threshold();

            setCurrentState();
        }
    }



    private void updateLum1Threshold() {
        try {
            lumThreshold1 = Double.parseDouble(ProfileManager.getInstance().getElement("illumination"));  //350
        } catch (NullPointerException npe) {
            lumThreshold1 = 350;
        } catch (NumberFormatException nfe) {
            lumThreshold1 = 350;
        }
        LOGGER.info("lumThreshold1 : "+lumThreshold1);
    }

    private void updateLum2Threshold() {
        try {
            lumThreshold2 = Double.parseDouble(ProfileManager.getInstance().getElement("illumination2"));  //350
        } catch (NullPointerException npe) {
            lumThreshold2 = 350;
        } catch (NumberFormatException nfe) {
            lumThreshold2 = 350;
        }
        LOGGER.info("lumThreshold2 : "+lumThreshold2);
    }


    @Override
    public void update(Observable o, Object arg) {

        if (!(o instanceof WSReadingsClient)) {
            return;
        }

        if (!(arg instanceof Message.NodeReadings)) {
            return;
        }

        final Message.NodeReadings readings = (Message.NodeReadings) arg;
        for (final Message.NodeReadings.Reading reading : readings.getReadingList()) {
            addReading(reading);
        }
    }

    /**
     * Reset the internal state.
     */
    public void reset() {
        this.states = new HashMap<String, Double>();
        updateLum1Threshold();
        updateLum2Threshold();
        currentState = TOTAL_DARKNESS;

    }
}
