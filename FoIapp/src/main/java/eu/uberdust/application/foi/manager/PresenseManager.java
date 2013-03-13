package eu.uberdust.application.foi.manager;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 11/16/12
 * Time: 3:18 PM
 */
public class PresenseManager implements Observer {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PresenseManager.class);
    /**
     * Contains all States for all known sensors.
     */
    private Map<String, Long> states;
    /**
     * Our Instance.
     */
    private static PresenseManager instance = null;
    /**
     * Delay for transition between States.
     */
    private long pirDelay;
    /**
     * EMPTY State Indicator.
     */
    public static final int EMPTY = 1;
    /**
     * NEW_ENTRY State Indicator.
     */
    public static final int NEW_ENTRY = 2;
    /**
     * OCCUPIED State Indicator.
     */
    public static final int OCCUPIED = 3;
    /**
     * LEFT State Indicator.
     */
    public static final int LEFT = 4;
    /**
     * Current State of the Presence Monitor.
     */
    private int currentState;
    /**
     * Timestamp of the first pir Event.
     */
    private long firstTimestamp;

    /**
     * Updates and Returns the current state of the operation.
     *
     * @return {EMPTY , NEW_ENTRY ,OCCUPIED ,LEFT}
     */
    public int getCurrentState() {
        //FSM
        //EMPTY -> NEW_ENTRY -> OCCUPIED -> LEFT -> EMPTY
        if (updateStatus()) {//true when empty now
            if (currentState == OCCUPIED) {
                currentState = LEFT;
                LOGGER.info("LEFT");
            } else {
                if (isLongAbsence()) {
                    currentState = EMPTY;
                    LOGGER.info("EMPTY");
                }
            }
        } else {
            if (currentState == EMPTY) {
                currentState = NEW_ENTRY;
                LOGGER.info("NEW_ENTRY");
            } else {
                if (isLongPresence()) {
                    currentState = OCCUPIED;
                    LOGGER.info("OCCUPIED");
                }
            }
        }
        return currentState;
    }

    /**
     * Default Constructor.
     */
    public PresenseManager() {
        reset();
        LOGGER.info("PirDelay:" + pirDelay);
    }

    /**
     * Singleton Get Instance.
     *
     * @return the single instance.
     */
    public static PresenseManager getInstance() {
        synchronized (PresenseManager.class) {
            if (instance == null) {
                instance = new PresenseManager();
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
        //interested only in pir events
        if ("urn:wisebed:node:capability:pir".equals(reading.getCapability())) {
            //interested only in Presence not absense
            if (reading.getDoubleReading() > 0) {
                //update the state
                states.put(reading.getNode(), reading.getTimestamp());
                //set as the first pir event of the sequence
                if (currentState == EMPTY) {
                    firstTimestamp = reading.getTimestamp();
                }
            }
            //calculate te current Status FSM
            getCurrentState();
        }
    }

    /**
     * Checks for the Status of Presence in a FOI.
     *
     * @return true if the FOI is empty , false if presence detected
     */
    private boolean updateStatus() {
        for (String point : states.keySet()) {
            LOGGER.info(point + "@" + states.get(point));
        }
        for (String host : states.keySet()) {
            if ((System.currentTimeMillis() - states.get(host)) < getPirDelay()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks for extended presence.
     *
     * @return true/false
     */
    private boolean isLongPresence() {
        return (System.currentTimeMillis() - firstTimestamp > getPirDelay());
    }

    /**
     * Checks for extended absence
     *
     * @return true/false
     */
    private boolean isLongAbsence() {
        for (String host : states.keySet()) {
            if ((System.currentTimeMillis() - states.get(host)) < getPirDelay()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Update and Get the Pir Delay used.
     *
     * @return the pirDelay in milliseconds.
     */
    public Long getPirDelay() {
        try {
            pirDelay = Long.parseLong(ProfileManager.getInstance().getElement("pir_delay")) * 1000;
        } catch (NumberFormatException nfe) {
            pirDelay = 1000;
        }
        return pirDelay;
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
        this.states = new HashMap<String, Long>();
        currentState = EMPTY;
        firstTimestamp = System.currentTimeMillis();
        getPirDelay();
    }
}
