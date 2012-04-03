package eu.uberdust;

import eu.uberdust.evaluation.RestClient;
import org.apache.log4j.Logger;

/**
 * Evaluation Application Main Class.
 */
public class MainApp {



    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MainApp.class);

    /**
     * Main routine of application.
     *
     * @param args input arguments
     */
    public static void main(final String[] args) {
        RestClient.getInstance();
    }
}
