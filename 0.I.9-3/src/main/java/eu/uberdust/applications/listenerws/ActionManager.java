package eu.uberdust.applications.listenerws;

import eu.uberdust.communication.UberdustClient;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 11/16/12
 * Time: 3:18 PM
 */
public class ActionManager {
    private static ActionManager instance = null;
    private String zone;
    private String actuator;

    public ActionManager() {
    }

    public static ActionManager getInstance() {
        synchronized (ActionManager.class) {
            if (instance == null) {
                instance = new ActionManager();
            }
            return instance;
        }
    }

    public void makeAction(boolean state) throws IOException {
        UberdustClient.getInstance().sendCoapPost(actuator, zone, state ? "0" : "1");

    }

    public void setActuator(String actuator, String zone) {
        this.actuator = actuator;
        this.zone = zone;
    }
}
