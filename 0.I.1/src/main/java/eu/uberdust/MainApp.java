package eu.uberdust;

import eu.uberdust.lights.LightController;

/**
 * Entry point.
 */
public class MainApp {
    
    public static final String SENSOR_PIR = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:0x99c/capability/urn:wisebed:node:capability:pir/latestreading";
    
    public static final String SENSOR_LIGHT_EXT = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:0xca3/capability/urn:wisebed:node:capability:light/latestreading";
    
    public static final String SENSOR_SCREENLOCK = "http://uberdust.cti.gr/rest/testbed/3/node/urn:ctinetwork:black/capability/urn:ctinetwork:node:capability:lockScreen/latestreading";

    public static final String LIGHT_CONTROLLER = "http://uberdust.cti.gr/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x99c/payload/1,";
    
    public static void main(final String[] args) {
        LightController.getInstance();
    }
}
