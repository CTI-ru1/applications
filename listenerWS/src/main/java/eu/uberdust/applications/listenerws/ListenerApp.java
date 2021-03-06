package eu.uberdust.applications.listenerws;

import eu.uberdust.communication.UberdustClient;
import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.rest.UberdustRestClient;
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
    private long lastEvent;
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
//        server = "ws://uberdust.cti.gr:80/readings.ws";
        server = "ws://uberdust.cti.gr:80/readings.ws";
        // Call http://carrot.cti.gr:8080/uberdust/rest/testbed/1/node/urn:wisebed:ctitestbed:0x9979/capability/urn:wisebed:node:capability:pir/insert/timestamp/1731231000/reading/1/
        // to add a reading

        WSReadingsClient.getInstance().setServerUrl(server);
//        WSReadingsClient.getInstance().subscribe("urn:testing:0x3ef", "urn:node:capability:get1");
//        WSReadingsClient.getInstance().subscribe("urn:wisebed:ctitestbed:odyssey", "urn:wisebed:ctitestbed:node:capability:lockScreen");
//        WSReadingsClient.getInstance().subscribe("urn:wisebed:ctitestbed:virtual:room:0.I.2", "urn:wisebed:node:capability:light");
//        WSReadingsClient.getInstance().subscribe("urn:wisebed:ctitestbed:virtual:0x295", "urn:wisebed:node:capability:light");
        WSReadingsClient.getInstance().subscribe("urn:amaxilat:virtual:room:1", "urn:node:capability:lz1");
        WSReadingsClient.getInstance().subscribe("urn:amaxilat:virtual:room:1", "urn:node:capability:pir");
//        WSReadingsClient.getInstance().subscribe("urn:wisebed:ctitestbed:0x180", "urn:wisebed:node:capability:temperature");
//        WSReadingsClient.getInstance().subscribe("urn:wisebed:ctitestbed:0x153d", "urn:wisebed:node:capability:temperature");

        LOGGER.info("Starting connection with Server:" + server);
        LOGGER.info("Starting connection with protocol:" + protocol);

        UberdustClient.setUberdustURL("http://uberdust.cti.gr:80");
        WSReadingsClient.getInstance().addObserver(this);
        lastEvent = 0;
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
            if (reading.getReading(0).getCapability().contains("pir")) {
                if (reading.getReading(0).getDoubleReading() > 0) {
                    UberdustClient.getInstance().sendCoapPost("dfa", "lz1", "1");
                    lastEvent = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - lastEvent > 20000) {
                        UberdustClient.getInstance().sendCoapPost("dfa", "lz1", "0");
                    }
                }
            }
        }
    }
}
