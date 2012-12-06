package eu.uberdust;

import eu.uberdust.lights.FoiController;

/**
 * Entry point.
 */
public class MainApp {

    public static String FOI;
    public static String[] ZONES;
    public static final String CAPABILITY_LIGHT = "urn:wisebed:node:capability:light";
    public static final String CAPABILITY_PIR = "urn:wisebed:node:capability:pir";
    public static final String CAPABILITY_SCREENLOCK = "urn:wisebed:ctitestbed:node:capability:lockScreen";
    public static final String SENSOR_LIGHT_READINGS_REST = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:room:0.I.2/capability/urn:wisebed:node:capability:light/tabdelimited/limit/"+FoiController.WINDOW;

    public static void main(final String[] args) {

        FOI = args[0];
        ZONES = new String[args.length - 1];
        System.arraycopy(args, 1, ZONES, 0, args.length - 1);
        FoiController.getInstance();
    }
}
