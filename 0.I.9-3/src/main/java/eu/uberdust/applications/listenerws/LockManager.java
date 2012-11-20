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
public class LockManager {
    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LockManager.class);
    private Map<String, Double> states;
    private static LockManager instance = null;

    public LockManager() {
        this.states = new HashMap<String, Double>();
    }

    public static LockManager getInstance() {
        synchronized (LockManager.class) {
            if (instance == null) {
                instance = new LockManager();
            }
            return instance;
        }
    }

    public void addReading(Message.NodeReadings.Reading reading) {
        if ("urn:wisebed:ctitestbed:node:capability:lockScreen".equals(reading.getCapability())) {
            states.put(reading.getNode(), reading.getDoubleReading());
        }
    }

    public boolean isLocked() {
        for (String host : states.keySet()) {
            if (states.get(host) == 0) {
                return false;
            }
        }
        return true;
    }
}
