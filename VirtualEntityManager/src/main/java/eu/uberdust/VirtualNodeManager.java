package eu.uberdust;


import eu.uberdust.communication.UberdustClient;
import eu.uberdust.tasks.VirtualNodeChecker;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/7/11
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public final class VirtualNodeManager {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(VirtualNodeManager.class);

    /**
     * Pir timer.
     */
    private final Timer timer;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static VirtualNodeManager ourInstance = null;


    /**
     * LightController is loaded on the first execution of LightController.getInstance()
     * or the first access to LightController.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static VirtualNodeManager getInstance() {
        synchronized (VirtualNodeManager.class) {
            if (ourInstance == null) {
                ourInstance = new VirtualNodeManager();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private VirtualNodeManager() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));

        UberdustClient.getInstance().setUberdustURL("http://uberdust.cti.gr/");
        LOGGER.info("Virtual Node Checker initialized");
        timer = new Timer();
        timer.scheduleAtFixedRate(new VirtualNodeChecker(), 1000, 60000);
    }

    public static void main(final String[] args) {
        VirtualNodeManager.getInstance();
    }
}
