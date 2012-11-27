package eu.uberdust.virtualnodemanager;


import eu.uberdust.communication.UberdustClient;
import eu.uberdust.util.PropertyReader;
import eu.uberdust.virtualnodemanager.tasks.VirtualNodeChecker;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 */
public final class VirtualNodeManager {
    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(VirtualNodeManager.class);

    /**
     * Execution Timer.
     */
    private final Timer timer;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static VirtualNodeManager ourInstance = null;


    /**
     * VirtualNodeManager is loaded on the first execution of VirtualNodeManager.getInstance()
     * or the first access to VirtualNodeManager.ourInstance, not before.
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
        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));
        PropertyReader.getInstance().setFile("properties");

        UberdustClient.getInstance().setUberdustURL((String) PropertyReader.getInstance().getProperties().get("uberdustURL"));
        LOGGER.info("Virtual Node Checker initialized");
        timer = new Timer();
        timer.scheduleAtFixedRate(new VirtualNodeChecker(), 1000, 60000);
    }

    /**
     * Start the Virtual Node Managet Application.
     *
     * @param args unused
     */
    public static void main(final String[] args) {
        VirtualNodeManager.getInstance();
    }
}
