package eu.uberdust.application.foi.manager;

import eu.uberdust.application.foi.MainApp;
import eu.uberdust.application.foi.util.GetJson;
import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.rest.RestClient;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


public class LockManager extends Observable implements Observer {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LockManager.class);

    /**
     * Contains all States for all known sensors.
     */
    private Map<String, Double> states;

    /**
     * Our Instance.
     */
    private static LockManager instance = null;

    /**
     * SCREEN_UNLOCKED State Indicator.
     */
    public static final int SCREEN_UNLOCKED = 0;

    /**
     * SCREEN_LOCKED State Indicator.
     */
    public static final int SCREEN_LOCKED = 1;

    /**
     * WORKSTATION_START_SESSION State Indicator.
     */
    public static final int WORKSTATION_START_SESSION = 2;

    /**
     * POWER_OFF State Indicator.
     */
    public static final int WORKSTATION_END_SESSION = 3;


    /**
     * Current State of the Presence Monitor.
     */
    private int currentState;


    private static final String SCREENLOCK_REST = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:" + MainApp.ACTUATOR_MODE + ":" + MainApp.FOI + "/capability/urn:wisebed:ctitestbed:node:capability:lockScreen/tabdelimited/limit/1";

    private static final String SCREENLOCK_NODE = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:" + MainApp.ACTUATOR_MODE + ":" + MainApp.FOI + "/capability/urn:wisebed:ctitestbed:node:capability:lockScreen/json/limit/1";





    public int getCurrentState() {

        return  currentState;

    }


    /**
     * Converts int state value to string.
     *
     * @param state the new state.
     */
    public String stateToString(int state){

        String s = "";

        switch (state){

            case 0:
                s = "SCREEN_UNLOCKED";
                break;

            case 1:
                s = "SCREEN_LOCKED";
                break;

            case 2:
                s = "WORKSTATION_START_SESSION";
                break;

            case 3:
                s = "WORKSTATION_END_SESSION";
                break;
        }

        return s;

    }


    /**
     * Updates and Returns the current state of the operation.
     *
     * @return {SCREEN_LOCKED , UNOLOKED}
     */

    public void setCurrentState(int newState) {

        LOGGER.info("Setting next state: "+stateToString(currentState)+" -------->> "+stateToString(newState));

        currentState = newState;

        this.setChanged();
        this.notifyObservers();

    }

    /**
     * Default Constructor.
     */
    public LockManager() {

        LOGGER.info("-----LockManager initializing------");
        reset();

    }

    /**
     * Singleton Get Instance.
     *
     * @return the single instance.
     */
    public static LockManager getInstance() {
        synchronized (LockManager.class) {
            if (instance == null) {
                instance = new LockManager();
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

        if ("urn:wisebed:ctitestbed:node:capability:lockScreen".equals(reading.getCapability())) {

            LOGGER.info("New Reading for Screen Lock Capability");

            //update the state
            states.put(reading.getNode(), reading.getDoubleReading());

            //calculate te current Status FSM
            updateStatus();
        }
    }

    /**
     * Checks for the Status of lockScreen in a FOI.
     *
     * @return true if the FOIs screen is locked , false if unlocked
     */
    private void updateStatus() {

        for (String point : states.keySet()) {
            LOGGER.info(point + "@" + states.get(point));
        }


        for (String host : states.keySet()) {         // only one entry for now

            switch ( (states.get(host)).intValue()){
                case 0:
                    setCurrentState(SCREEN_UNLOCKED);
                    break;
                case 1:
                    setCurrentState(SCREEN_LOCKED);
                    break;
                case 2:
                    setCurrentState(WORKSTATION_START_SESSION);
                    break;
                case 3:
                    setCurrentState(WORKSTATION_END_SESSION);
                    break;

            }

        }
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
        states.put(GetJson.getInstance().callGetJsonWebService(SCREENLOCK_NODE,"nodeId"), Double.valueOf(RestClient.getInstance().callRestfulWebService(this.SCREENLOCK_REST).split("\t")[1]));
        updateStatus();

    }
}
