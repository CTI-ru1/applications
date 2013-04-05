package eu.uberdust.application.foi;

import eu.uberdust.application.foi.manager.ProfileManager;
import eu.uberdust.application.foi.manager.RoomZoneManager;
import eu.uberdust.application.foi.controller.FoiController;
import eu.uberdust.application.foi.manager.WorkstationZoneManager;
import eu.uberdust.util.PropertyReader;

/**
 * Entry point.
 */
public class MainApp {
    private final static String FOI_PROPERTY = "FOI";
    private final static String ZONES_PROPERTY = "ZONES";
    private final static String PROFILES_URL_PROPERTY = "profiles.url";
    public static String FOI;
    public static String MODE;
    public static String ACTUATOR_MODE;
    public static String[] ZONES;
    public static final String CAPABILITY_LIGHT = "urn:wisebed:node:capability:light";
    public static final String CAPABILITY_PIR = "urn:wisebed:node:capability:pir";
    public static final String CAPABILITY_SCREENLOCK = "urn:wisebed:ctitestbed:node:capability:lockScreen";


    //public static final String SENSOR_LIGHT_READINGS_REST = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:room:0.I.2/capability/urn:wisebed:node:capability:light/tabdelimited/limit/" + FoiController.WINDOW;

    public static void main(final String[] args) {

        //load a properties file
        PropertyReader.getInstance().setFile("config.properties");


        //get the property value and print it out
        FOI = PropertyReader.getInstance().getProperties().getProperty(FOI_PROPERTY);
        ZONES = PropertyReader.getInstance().getProperties().getProperty(ZONES_PROPERTY).split(" ");
        ProfileManager.getInstance().setAddress(PropertyReader.getInstance().getProperties().getProperty(PROFILES_URL_PROPERTY));
        ProfileManager.getInstance().setIdentifier(FOI);
        MODE = ProfileManager.getInstance().getElement("mode");

        if( MODE.equals("ichatzWorkstation") || MODE.equals("SingleLightWorkstation") ){

            ACTUATOR_MODE = "workstation";

        }  else  ACTUATOR_MODE = "room";

        RoomZoneManager.getInstance().setZones(PropertyReader.getInstance().getProperties().getProperty(ZONES_PROPERTY));
        WorkstationZoneManager.getInstance().setZones(PropertyReader.getInstance().getProperties().getProperty(ZONES_PROPERTY));
        FoiController.getInstance();

    }
}
