package eu.uberdust.evaluation;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import eu.uberdust.evaluation.tasks.BlinkTask;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;

public final class RestClient implements Observer {
    /**
     * Evaluation String.
     */
    private static final String EVALUATION_NODE = "urn:wisebed:ctitestbed:0xa4a";
    private static final String EVALUATION_CAPABILITY = "urn:wisebed:node:capability:test";


    public static final String EVALUATION_URL = "http://uberdust.cti.gr/rest/sendCommand/destination/urn:wisebed:ctitestbed:0xa4a/payload/1,1,1";

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RestClient.class);


    /**
     * static instance(ourInstance) initialized as null.
     */
    private static RestClient ourInstance = null;
    private Timer timer;
    private static final long DELAY = 60000;

    /**
     * RestClient is loaded on the first execution of RestClient.getInstance()
     * or the first access to RestClient.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static RestClient getInstance() {
        synchronized (RestClient.class) {
            if (ourInstance == null) {
                ourInstance = new RestClient();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private RestClient() {
        WSReadingsClient.getInstance().setServerUrl("ws://uberdust.cti.gr:80/readings.ws");
        WSReadingsClient.getInstance().subscribe(EVALUATION_NODE, EVALUATION_CAPABILITY);
        WSReadingsClient.getInstance().addObserver(this);
        timer = new Timer();
        timer.schedule(new BlinkTask(timer), DELAY);

    }

    /**
     * Call Remote  Rest Interface.
     *
     * @param address the address
     * @return the return String
     */
    public String callRestfulWebService(final String address) {
        try {
            final URL url = new URL(address);
            final URLConnection yc;

            yc = url.openConnection();

            final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

            final StringBuilder inputLine = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null) {
                inputLine.append(str);
            }
            in.close();

            if (address.contains("payload")) {
                if (!inputLine.toString().contains("OK")) {
                    throw new RuntimeException("Bad Response");
                }
            }
            LOGGER.info(inputLine.toString());
            return inputLine.toString();
        } catch (final Exception e) {
            LOGGER.error(e);
            callRestfulWebService(address);
        }
        return "0\t0";
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof WSReadingsClient)) {
            return;
        }
        if (!(arg instanceof Message.NodeReadings)) {
            return;
        }
        Message.NodeReadings readings = (Message.NodeReadings) arg;
        for (Message.NodeReadings.Reading reading : readings.getReadingList()) {
            if (EVALUATION_NODE.equals(reading.getNode())) {
                if (EVALUATION_CAPABILITY.equals(reading.getCapability())) {
                    callRestfulWebService(EVALUATION_URL);
                }
            }
        }
    }
}
