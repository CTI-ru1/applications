package eu.uberdust.tasks;

import eu.uberdust.communication.RestClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 9/12/12
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualNodeDisconnector extends TimerTask {

    Map<String, Integer> virtuals = new HashMap<String, Integer>();

    @Override
    public void run() {


        String result = RestClient.getInstance().callRestfulWebService("http://uberdust.cti.gr/rest/testbed/1/capability/workstation/tabdelimited");
        String[] parts = result.split("\t");
//        System.out.println(parts.length);
        for (int i = 0; i < parts.length; i += 4) {

//            System.out.println(parts.length);
            String node = parts[i];
            String entityName = "urn:wisebed:ctitestbed:virtual:workstation:" + parts[i + 3];
            System.out.println(node + "---" + entityName);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            }

            try {
                addNode(entityName);
                String result1 = RestClient.getInstance().callRestfulWebService("http://uberdust.cti.gr/rest/testbed/1/link/" + entityName + "/" + node + "/capability/virtual/insert/timestamp/0/reading/1/");
                virtuals.put(entityName, 1);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }
    }

    private void addNode(String entityName) throws IOException {
        if (!virtuals.containsKey(entityName)) {
            System.out.println("ADDING " + entityName);
            String result1 = RestClient.getInstance().callPut("http://uberdust.cti.gr/rest/testbed/1/node/" + entityName + "/");
        }
    }


}

