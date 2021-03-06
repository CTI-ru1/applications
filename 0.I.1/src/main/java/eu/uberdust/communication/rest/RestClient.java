package eu.uberdust.communication.rest;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Simple Client for REST calls.
 */
public final class RestClient {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RestClient.class);


    /**
     * static instance(ourInstance) initialized as null.
     */
    private static RestClient ourInstance = null;

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
    }

    /**
     * Call Remote  Rest Interface.
     *
     * @param address the address
     * @return the return String
     */
    public String callRestfulWebService(final String address) {
        try {
           // System.err.println(address);
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
            if (e.getMessage().contains("406")) {
                return "0\t0";
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                LOGGER.error(e1);
            }
            callRestfulWebService(address);
        }
        return "0\t0";
    }
}
