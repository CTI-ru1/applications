package eu.uberdust.application.foi.manager;


import eu.uberdust.application.foi.MainApp;
import eu.uberdust.application.foi.util.GetJson;
import eu.uberdust.communication.UberdustClient;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class WorkstationZoneManager {

    private static final String ACTUATOR_URL = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:" + MainApp.MODE + ":" + MainApp.FOI + "/capability/urn:wisebed:node:capability:lz" + MainApp.ZONES[0] + "/json/limit/1";

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(WorkstationZoneManager.class);
    /**
     * Singleton instance.
     */
    private static WorkstationZoneManager ourInstance = new WorkstationZoneManager();
    /**
     * Stores all available and controllable zones.
     */
    private List<Zone> zones;

    /**
     * Singleton Get Instance.
     *
     * @return the single instance.
     */
    public static WorkstationZoneManager getInstance() {
        return ourInstance;
    }

    //TODO: Switch odd zones first and then the even as the second level of light

    /**
     * Default Constructor.
     */
    private WorkstationZoneManager() {
    }

    /**
     * Displays the status of all zones.
     *
     * @return String representation of the zones' status.
     */
    public String showStatus() {
        StringBuilder sb = new StringBuilder("Zones:");
        for (Zone zone : zones) {
            sb.append("{" + zone.getName() + "," + zone.getStatus() + "}");
        }
        return sb.toString();
    }

    /**
     * Parses and generates zones list.
     *
     * @param myZones a string that contains information about the zones.
     */
    public void setZones(String myZones) {
        this.zones = new ArrayList<Zone>();
        for (String zone : myZones.split(" ")) {
            zones.add(new Zone(zone));
        }
    }

    /**
     * Checks if all Zones are off.
     *
     * @return true/false
     */
    public boolean allOff() {
        for (Zone zone : zones) {
            if (zone.getStatus()) return false;
        }
        return true;
    }

    /**
     * Returns the status of first light level
     */
    public boolean getFirstStatus() {

            return zones.get(0).getStatus();

    }

    /**
     * Returns the status of last light level
     */
    public boolean getLastStatus() {

        return zones.get(zones.size()-1).getStatus();

    }

    /**
     * Switches off the last Light-Level.
     */
    public void switchLastOff() {

        zones.get(zones.size()-1).setOff();

    }

    /**
     * Switches off the first Light-Level.
     */
    public void switchOffFirst() {

        zones.get(0).setOff();

    }

    /**
     * Switches on the first Light-Level.
     */
    public void switchOnFirst() {

        zones.get(0).setOn();

    }

    /**
     * Switches on the last Light-Level.
     */
    public void switchOnLast() {

        zones.get(1).setOn();

    }

    /**
     * Switches off all Light-Levels.
     */
    public void switchOffAll() {
        for (Zone zone : zones) {
            zone.setOff();
        }
    }

    /**
     * Switches on all Light-Levels.
     */
    public void switchOnAll() {
        for (Zone zone : zones) {
            zone.setOn();
        }
    }



    /**
     * Inner class that represents light zones.
     */
    private class Zone {
        private boolean status;
        private String name;

        private Zone(String name) {
            this.name = name;
            this.status = false;
        }

        public String getName() {
            return name;
        }

        public boolean getStatus() {
            return status;
        }

        public void change() {
            status = !status;
            controlLight(status, getName());
        }

        public synchronized void setOn() {
            controlLight(true, getName());
            status = true;
        }

        public synchronized void setOff() {
            controlLight(false, getName());
            status = false;
        }

        /**
         * Switches a light zone.
         *
         * @param value target value {on , off}
         * @param zone  target zone {1,2,3, ...}
         */
        private synchronized void controlLight(final boolean value, final String zone) {

            LOGGER.info("Controlling zone " + zone + " " + value);

            final String foiActuator = GetJson.getInstance().callGetJsonWebService(ACTUATOR_URL, "nodeId").split("0x")[1];

            //ALSO checks for a bypass
            final boolean bypass = Boolean.parseBoolean(ProfileManager.getInstance().getElement("bypass"));
            if (!bypass) {
                LOGGER.info("Calling UberdustClient");
                UberdustClient.getInstance().sendCoapPost(foiActuator, "lz" + zone, value ? "1" : "0"); //foiActuator
            } else {
                LOGGER.info("Bypass is Enabled");
            }
        }
    }
}
