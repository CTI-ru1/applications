package eu.uberdust.application.foi.manager;

import eu.uberdust.communication.protobuf.Message;
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
     * LOCKED State Indicator.
     */
    public static final int LOCKED = 1;

    /**
     * UNLOCKED State Indicator.
     */
    public static final int UNLOCKED = 2;


    /**
     * Current State of the Presence Monitor.
     */
    private int currentState;


    public int getCurrentState() {

        return  currentState;

    }




    /**
     * Updates and Returns the current state of the operation.
     *
     * @return {LOCKED , UNOLOKED}
     */

    public int setCurrentState() {
        //FSM
        //LOCKED -> UNLOCKED -> LOCKED
        if (updateStatus()) {//true when locked now

            currentState = LOCKED;
            LOGGER.info("SCREEN_LOCKED");

        } else {

            currentState = UNLOCKED;
            LOGGER.info("SCREEN_UNLOCKED");

        }
        this.setChanged();
        this.notifyObservers();
        return currentState;
    }

    /**
     * Default Constructor.
     */
    public LockManager() {
        reset();
        LOGGER.info("-----LockManager initializing------");
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
            setCurrentState();
        }
    }

    /**
     * Checks for the Status of lockScreen in a FOI.
     *
     * @return true if the FOIs screen is locked , false if unlocked
     */
    private boolean updateStatus() {

        for (String point : states.keySet()) {
            LOGGER.info(point + "@" + states.get(point));
        }

        for (String host : states.keySet()) {

            if ( states.get(host) == 0.0 ) {
                return false;
            }
        }

        return true;
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
        currentState = LOCKED;

    }
}
