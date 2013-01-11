package eu.uberdust;

import eu.uberdust.lights.FoiController;
import eu.uberdust.util.PropertyReader;

/**
 * Entry point.
 */
public class MainApp {

    public static String FOI;
    public static String[] ZONES;
    public static final String CAPABILITY_LIGHT = "urn:wisebed:node:capability:light";
    public static final String CAPABILITY_PIR = "urn:wisebed:node:capability:pir";
    public static final String CAPABILITY_SCREENLOCK = "urn:wisebed:ctitestbed:node:capability:lockScreen";

    //public static final String SENSOR_LIGHT_READINGS_REST = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:room:0.I.2/capability/urn:wisebed:node:capability:light/tabdelimited/limit/" + FoiController.WINDOW;

    public static void main(final String[] args) {

        //load a properties file
        PropertyReader.getInstance().setFile("config.properties");

        //get the property value and print it out
        FOI = PropertyReader.getInstance().getProperties().getProperty("FOI");
        ZONES = PropertyReader.getInstance().getProperties().getProperty("ZONES").split(" ");
        FoiController.getInstance();
    }
}
