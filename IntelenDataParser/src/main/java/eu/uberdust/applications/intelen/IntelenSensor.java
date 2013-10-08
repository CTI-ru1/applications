package eu.uberdust.applications.intelen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amaxilatis
 * Date: 10/7/13
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class IntelenSensor {
    private final String mac;
    private final String name;
    private final Double kwh;

    public IntelenSensor(String mac, String name, Double kwh) {
        this.mac = mac;
        this.name = name;
        this.kwh = kwh;
    }

    public static List<IntelenSensor> parseSensors(String sensorJSON) throws JSONException {
        JSONArray obj = new JSONArray(sensorJSON);
        List<IntelenSensor> sensors = new ArrayList<IntelenSensor>();
        for (int i = 0; i < obj.length(); i++) {
            JSONObject data = obj.getJSONObject(i);
            String mac = data.getString("mac");
            String name = data.getString("name");
            name = name.replaceAll("\\[GEN6\\] ", "");
            Double kwh = data.getDouble("kwh");
            sensors.add(new IntelenSensor(mac, name, kwh));
        }
        return sensors;
    }

    @Override
    public String toString() {
        return "IntelenSensor{" +
                "mac='" + mac + '\'' +
                ", name='" + name + '\'' +
                ", kwh=" + kwh +
                '}';
    }

    public String getMac() {
        return mac;
    }

    public Double getKwh() {
        return kwh;
    }

    public String getName() {
        return name;
    }
}
