package eu.uberdust.applications.intelen;

import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.network.NetworkManager;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: amaxilatis
 * Date: 10/7/13
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(final String[] args) throws IOException {

        String cmd = "python getPython.py";
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = br.readLine();


        try {
//            FileReader fr = new FileReader("/home/amaxilatis/intelen.day");
//            BufferedReader br = new BufferedReader(fr);
//            String line = br.readLine();
//            StringBuilder sb;
//            do {
//                sb = new StringBuilder(line);
//                line = br.readLine();
//            } while (line != null);
            List<IntelenSensor> sensors = IntelenSensor.parseSensors(s);
            for (IntelenSensor sensor : sensors) {
                System.out.println(sensor);
            }

            NetworkManager.getInstance().start("uberdust.cti.gr:80/", 8);

            Message.NodeReadings.Builder readingsBuilder = Message.NodeReadings.newBuilder();
            long now = System.currentTimeMillis();
            for (IntelenSensor sensor : sensors) {
                readingsBuilder.addReading(Message.NodeReadings.Reading.newBuilder().setNode("urn:gen6:" + sensor.getMac()).setCapability("urn:node:capability:kwh").setDoubleReading(sensor.getKwh()).setTimestamp(now).build());
            }
            NetworkManager.getInstance().sendNodeReading(readingsBuilder.build());

            Thread.sleep(60000);
            System.exit(1);

        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
