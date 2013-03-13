package eu.uberdust.application.foi.manager;

import eu.uberdust.application.foi.manager.ProfileManager;
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
    private Map<String, Long> states;
    private static PresenseManager instance = null;
    private long pirDelay;
    public static final int EMPTY = 1;
    public static final int NEW_ENTRY = 2;
    public static final int OCCUPIED = 3;
    public static final int LEFT = 4;
    private int prevState;
    private long firstTimestamp;

    public int getPrevState() {
        //true when empty now
        if (updateStatus()) {
            if (prevState == OCCUPIED) {
                prevState = LEFT;
                LOGGER.info("LEFT");
            } else {
                if (isLongAbsense()) {
                    prevState = EMPTY;
                    LOGGER.info("EMPTY");
                }
            }
        } else {
            if (prevState == EMPTY) {
                prevState = NEW_ENTRY;
                LOGGER.info("NEW_ENTRY");
            } else {
                if (isLongPresence()) {
                    prevState = OCCUPIED;
                    LOGGER.info("OCCUPIED");
                }
            }
        }
        return prevState;
    }

    public PresenseManager() {
        this.states = new HashMap<String, Long>();
        prevState = EMPTY;
        firstTimestamp = System.currentTimeMillis();
        getPirDelay();
        LOGGER.info("PirDelay:" + pirDelay);
    }

    public static PresenseManager getInstance() {
        synchronized (PresenseManager.class) {
            if (instance == null) {
                instance = new PresenseManager();
            }
            return instance;
        }
    }

    public void addReading(Message.NodeReadings.Reading reading) {
        if ("urn:wisebed:node:capability:pir".equals(reading.getCapability())) {
            if (reading.getDoubleReading() > 0) {
                states.put(reading.getNode(), reading.getTimestamp());
                if (prevState == EMPTY) {
                    firstTimestamp = reading.getTimestamp();
                }
            }
            //true when empty now
            if (updateStatus()) {
                if (prevState == OCCUPIED) {
                    prevState = LEFT;
                    LOGGER.info("LEFT");
                } else {
                    if (isLongAbsense()) {
                        prevState = EMPTY;
                        LOGGER.info("EMPTY");
                    }
                }
            } else {
                if (prevState == EMPTY) {
                    prevState = NEW_ENTRY;
                    LOGGER.info("NEW_ENTRY");
                } else {
                    if (isLongPresence()) {
                        prevState = OCCUPIED;
                        LOGGER.info("OCCUPIED");
                    }
                }
            }
        }
    }

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

    private boolean isLongPresence() {
        return (System.currentTimeMillis() - firstTimestamp > 2 * getPirDelay());
    }

    private boolean isLongAbsense() {
        for (String host : states.keySet()) {
            if ((System.currentTimeMillis() - states.get(host)) < 2 * getPirDelay()) {
                return false;
            }
        }
        return true;
    }

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
}
