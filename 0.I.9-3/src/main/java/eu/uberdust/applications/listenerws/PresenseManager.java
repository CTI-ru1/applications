package eu.uberdust.applications.listenerws;

import eu.uberdust.communication.protobuf.Message;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 11/16/12
 * Time: 3:18 PM
 */
public class PresenseManager {
    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PresenseManager.class);
    private Map<String, Long> states;
    private static PresenseManager instance = null;

    public PresenseManager() {
        this.states = new HashMap<String, Long>();
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
            }
        }
    }

    public boolean isEmpty() {
        for (String point : states.keySet()) {
            LOGGER.info(states.get(point) + "@" + states.get(point));
        }
        for (String host : states.keySet()) {
            if ((System.currentTimeMillis() - states.get(host)) < 60000) {
                return false;
            }
        }
        return true;
    }
}
