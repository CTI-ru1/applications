package eu.uberdust;

import eu.uberdust.lights.LightController;

/**
 * Entry point.
 */
public class MainApp {

    public static final String URN_SENSOR_PIR = "urn:wisebed:ctitestbed:0x42f";

    public static final String URN_SENSOR_LIGHT = "urn:wisebed:ctitestbed:0x585";

    public static final String URN_AMETHYST = "urn:ctinetwork:amethyst";

    public static final String URN_SILVER = "urn:ctinetwork:silver";

    public static final String URN_BLANCO = "urn:ctinetwork:blanco";

    public static final String URN_YELLOW = "urn:ctinetwork:yellow";

    public static final String CAPABILITY_PIR = "urn:wisebed:node:capability:pir";

    public static final String CAPABILITY_LIGHT = "urn:wisebed:node:capability:light";

    public static final String CAPABILITY_SCREENLOCK = "urn:ctinetwork:node:capability:lockScreen";

    public static final String CAPABILITY_STATUS ="urn:ctinetwork:node:capability:status";
    //0xca3    //giannhs 0x99c , kouzina 0x494 h 0x1ccd
    public static final String SENSOR_PIR_REST = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:0x585/capability/urn:wisebed:node:capability:pir/latestreading";
    //0xca3
    public static final String SENSOR_LIGHT_EXT_REST = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:0x585/capability/urn:wisebed:node:capability:light/latestreading";

    public static final String STATUS_YELLOW_REST = "http://uberdust.cti.gr/rest/testbed/3/node/urn:ctinetwork:yellow/capability/urn:ctinetwork:node:capability:status/latestreading";

    public static final String SENSOR_SCREENLOCK_AMETHYST_REST = "http://uberdust.cti.gr/rest/testbed/3/node/urn:ctinetwork:amethyst/capability/urn:ctinetwork:node:capability:lockScreen/latestreading";

    public static final String SENSOR_SCREENLOCK_SILVER_REST = "http://uberdust.cti.gr/rest/testbed/3/node/urn:ctinetwork:silver/capability/urn:ctinetwork:node:capability:lockScreen/latestreading";

    public static final String SENSOR_SCREENLOCK_BLANCO_REST = "http://uberdust.cti.gr/rest/testbed/3/node/urn:ctinetwork:blanco/capability/urn:ctinetwork:node:capability:lockScreen/latestreading";

    public static final String SENSOR_SCREENLOCK_YELLOW_REST = "http://uberdust.cti.gr/rest/testbed/3/node/urn:ctinetwork:yellow/capability/urn:ctinetwork:node:capability:lockScreen/latestreading";
    //0x99c
    public static final String LIGHT_CONTROLLER = "http://uberdust.cti.gr/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x42f/payload/7f,69,70,1,";

    public static void main(final String[] args) {
        LightController.getInstance();
    }
}
