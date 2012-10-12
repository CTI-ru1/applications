package eu.uberdust.applications.listenerws;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Observable;
import java.util.Observer;

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
    private String protocol;

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
        LOGGER.info("hello world!");
        server = "ws://localhost:8081/uberdust/readings.ws";
        // Call http://carrot.cti.gr:8080/uberdust/rest/testbed/1/node/urn:wisebed:ctitestbed:0x9979/capability/urn:wisebed:node:capability:pir/insert/timestamp/1731231000/reading/1/
        // to add a reading

        WSReadingsClient.getInstance().setServerUrl(server);
        WSReadingsClient.getInstance().subscribe("urn:wisebed:ctitestbed:0x7061", "urn:wisebed:node:capability:pir");

        LOGGER.info("Starting connection with Server:" + server);
        LOGGER.info("Starting connection with protocol:" + protocol);


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
            LOGGER.info(reading.getReading(0).getNode());
        }
    }
}
