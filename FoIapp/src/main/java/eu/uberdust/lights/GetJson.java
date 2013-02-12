package eu.uberdust.lights;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by IntelliJ IDEA.
 * User: dimitris
 * Date: 11/5/12
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetJson {

    private static final Logger LOGGER = Logger.getLogger(GetJson.class);


    /**
     * static instance(ourInstance) initialized as null.
     */
    private static GetJson ourInstance = null;

    /**
     * RestClient is loaded on the first execution of RestClient.getInstance()
     * or the first access to RestClient.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static GetJson getInstance() {
        synchronized (GetJson.class) {
            if (ourInstance == null) {
                ourInstance = new GetJson();
            }
        }
        return ourInstance;
    }


    private GetJson() {

    }

    public String callGetJsonWebService(final String address, final String pref) {
        LOGGER.info(address + " PREF " + pref);
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

            LOGGER.info(inputLine.toString());
            String the_json = inputLine.toString();

            String val = null;
            if (the_json.startsWith("[")) {
                JSONArray json = new JSONArray(the_json);

                String parse1 = json.getString(0);

                JSONObject json2 = new JSONObject(parse1);

                val = json2.getString(pref);
            } else if (the_json.startsWith("{")) {

                JSONObject json2 = new JSONObject(the_json);

                val = json2.getString(pref);
            }

            //LOGGER.info(val);

            return val;

            // return inputLine.toString();
        } catch (final Exception e) {
            LOGGER.error(e);
            if (e.getMessage().contains("406")) {
                return "0\t0";
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                // LOGGER.error(e1);
            }
            callGetJsonWebService(address, pref);
        }
        return "0\t0";
    }

}

