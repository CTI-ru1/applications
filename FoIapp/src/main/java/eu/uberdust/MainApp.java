package eu.uberdust;

import eu.uberdust.lights.FoiController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

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

        Properties prop = new Properties();

        try {
            //load a properties file
            prop.load(new FileInputStream("config.properties"));
            //get the property value and print it out
            FOI = prop.getProperty("FOI");
            ZONES = prop.getProperty("ZONES").split(" ");
            FoiController.getInstance();
        } catch (FileNotFoundException fnfe) {
            System.out.println("Could not Find File");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
