package eu.uberdust;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ProfileManager class.
 * Continously Reads the current profile settings from the UberdustProfiles Server.
 * Provides easy functionality for reading the current profile settings.
 */
public class ProfileManager extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(ProfileManager.class);


    private static ProfileManager ourInstance = new ProfileManager();
    /**
     * Name of the foi.
     */
    private String identifier;
    /**
     * Profile App url.
     */
    private String address;
    /**
     * Update Interval.
     */
    private long updateInterval;
    /**
     * Profile Received.
     */
    private JSONObject profile;
    /**
     * Timer used to update the profile.
     */
    private Timer updateTimer;


    public static ProfileManager getInstance() {
        return ourInstance;
    }

    private ProfileManager() {
        identifier = null;
        address = "http://uberdust.cti.gr:3000" + "/api/v1/foi?identifier=";
        updateInterval = 60000;
        profile = null;
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(this, updateInterval, updateInterval);
    }


    /**
     * Change the interval between updates of the profile.
     *
     * @param updateInterval interval in milliseconds
     */
    public void setUpdateInterval(final long updateInterval) {
        this.updateInterval = updateInterval;
    }


    /**
     * Change the identifier of the FOI.
     *
     * @param identifier the name of the FOI.
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
        address = "http://uberdust.cti.gr:3000/api/v1/foi?identifier=" + identifier;
        update();
    }

    /**
     * Change the Profile Server Address.
     *
     * @param address the profile address.
     */
    public void setAddress(final String address) {
        this.address = "";
        if (!address.startsWith("http://")) {
            this.address = "http://";
        }
        this.address += address;
        this.address += "/api/v1/foi?identifier=";
    }

    /**
     * Updates the Profile.
     * Can be used to triger a manual update.
     */
    public void update() {
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

            profile = (JSONObject) new JSONArray(inputLine.toString()).get(0);
            // return inputLine.toString();
        } catch (final Exception e) {
            LOGGER.error(e, e);
        }
    }

    /**
     * Search the profile for a specific property.
     *
     * @param key the property of the profile.
     * @return the value of the profile as a String.
     */
    public String getElement(String key) {
        if (profile != null) {
            try {
                return profile.getString(key);
            } catch (JSONException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void run() {
        if (identifier != null) {
            update();
        }
    }

    /**
     * testing main.
     *
     * @param args unused
     */
    private static void main(String[] args) {
        ProfileManager.getInstance().setIdentifier("0.I.11");
        ProfileManager.getInstance().update();
        LOGGER.info(ProfileManager.getInstance().getElement("bypass"));
    }
}
