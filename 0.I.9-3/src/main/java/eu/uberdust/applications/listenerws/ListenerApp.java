package eu.uberdust.applications.listenerws;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import eu.uberdust.util.PropertyReader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 2/7/12
 * Time: 1:51 PM
 */
public class ListenerApp implements Observer {
    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ListenerApp.class);

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static ListenerApp ourInstance = null;
    private String server;
    private String workstation;
    private String actuator;
    private String zone;

    public static ListenerApp getInstance() {
        synchronized (ListenerApp.class) {
            if (ourInstance == null) {
                ourInstance = new ListenerApp();
            }
        }
        return ourInstance;
    }

    public ListenerApp() {
        PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties"));
        PropertyReader.getInstance().setFile("properties");
        Properties prop = PropertyReader.getInstance().getProperties();
        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream("properties"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        LOGGER.info("Loading Properties...");
        server = (String) prop.get("server");
        workstation = (String) prop.get("workstation");
        actuator = (String) prop.get("actuator");
        zone = (String) prop.get("zone");
        LOGGER.info(server);

        ActionManager.getInstance().setActuator(actuator, zone);

        WSReadingsClient.getInstance().setServerUrl(server);
//        WSReadingsClient.getInstance().subscribe("urn:wisebed:ctitestbed:virtual:workstation:" + workstation, "urn:wisebed:ctitestbed:node:capability:lockScreen");
        WSReadingsClient.getInstance().subscribe("urn:wisebed:ctitestbed:virtual:workstation:" + workstation, "urn:wisebed:node:capability:pir");
//        WSReadingsClient.getInstance().subscribe("urn:wisebed:ctitestbed:0x9979", "urn:wisebed:node:capability:pir");
//        WSReadingsClient.getInstance().subscribe("urn:wisebed:ctitestbed:odyssey", "urn:wisebed:ctitestbed:node:capability:lockScreen");
        LOGGER.info("Starting connection with Server:" + server);


        WSReadingsClient.getInstance().addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof WSReadingsClient)) {
            return;
        }
        LOGGER.info("observed reading");
        if (arg instanceof Message.NodeReadings) {
            Message.NodeReadings reading = (Message.NodeReadings) arg;
//            LockManager.getInstance().addReading(reading.getReading(0));
            PresenseManager.getInstance().addReading(reading.getReading(0));
            try {
                LOGGER.info("isEmpty:" + PresenseManager.getInstance().isEmpty());
                ActionManager.getInstance().makeAction(
                        //LockManager.getInstance().isLocked() &&
                        PresenseManager.getInstance().isEmpty()
                );
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
