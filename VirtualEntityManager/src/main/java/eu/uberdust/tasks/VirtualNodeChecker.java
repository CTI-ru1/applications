package eu.uberdust.tasks;

import eu.uberdust.communication.RestClient;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 9/12/12
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualNodeChecker extends TimerTask {

    Map<String, List<String>> virtuals = new HashMap<String, List<String>>();
    Map<String, Integer> associations = new HashMap<String, Integer>();

    @Override
    public void run() {


        String nodes = RestClient.getInstance().callRestfulWebService("http://uberdust.cti.gr/rest/testbed/1/node/raw");
        String[] lines = nodes.split("urn:");
        System.out.println(lines.length);
        for (String line : lines) {
            if (line.contains(":virtual:")) {
                virtuals.put("urn:" + line, new ArrayList<String>());
            }
        }
        poppulateNodes();


        displayEntities(virtuals);


        associate("workstation");
        associate("room");


//        if (true) return;


        String response = RestClient.getInstance().callRestfulWebService("http://uberdust.cti.gr/rest/testbed/1/capability/virtual/tabdelimited");
        String[] responseLines = response.split("\\[");
        System.out.println(responseLines.length);
        for (String line : responseLines) {
            if (line.equals("")) continue;

            String key = line.split("\t")[0];
            Boolean connected = line.split("\t")[2].equals("1.0");
            if (!associations.containsKey(key) && connected) {
                if (key.contains("workstation")) {
                    String entityName = key.split(",")[0];
                    String node = key.split(",")[1].replaceAll("\\]", "");
                    String url = "http://uberdust.cti.gr/rest/testbed/1/link/" + entityName + "/" + node + "/capability/virtual/insert/timestamp/0/reading/0/";
                    System.out.println(url);

                    String result1 = RestClient.getInstance().callRestfulWebService(url);

                    System.out.println("SHOULD REMOVE " + key);
                }
            }

        }
    }

    private void associate(String property) {
        String result = RestClient.getInstance().callRestfulWebService("http://uberdust.cti.gr/rest/testbed/1/capability/" + property + "/tabdelimited");
        String[] parts = result.split("\t");
//        System.out.println(parts.length);
        for (int i = 0; i < parts.length; i += 4) {

//            System.out.println(parts.length);
            String node = parts[i];
            String entityName = "urn:wisebed:ctitestbed:virtual:" + property + ":" + parts[i + 3];

            associations.put(entityName + "," + node + "]", 1);

            if (isAssociated(entityName, node)) {
                System.out.println(entityName + "---" + node + " Already Associated!");
                continue;
            }
            System.out.println(entityName + "---" + node + " need to be Associated!");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            }

            try {
                addNode(entityName);
                System.out.println("calling");
                URL a = new URL("http://uberdust.cti.gr/rest/testbed/1/link/" + entityName + "/" + node + "/capability/virtual/insert/timestamp/0/reading/1/");
                URLConnection con = a.openConnection();
                (a.openStream()).close();


//                String result1 = RestClient.getInstance().callRestfulWebService();
                virtuals.put(entityName, new ArrayList<String>());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }


    private boolean isAssociated(String entityName, String node) {
        if (virtuals.containsKey(entityName)) {
            for (String nodename : virtuals.get(entityName)) {
                if (node.equals(nodename)) {
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    private void displayEntities(Map<String, List<String>> virtuals) {
        System.out.println("Existing Virtual Entities:");
        for (String s : virtuals.keySet()) {
            System.out.println(s + " with " + virtuals.get(s).size() + " nodes");
            System.out.println("=========================");
            for (String node : virtuals.get(s)) {
                System.out.println("= " + node);
            }
        }
    }

    private void poppulateNodes() {
        String response = RestClient.getInstance().callRestfulWebService("http://uberdust.cti.gr/rest/testbed/1/capability/virtual/tabdelimited");
        String[] responseLines = response.split("\\[");
        System.out.println(responseLines.length);
        for (String line : responseLines) {
            if (line.equals("")) continue;

            String key = line.split("\t")[0];
            Boolean connected = line.split("\t")[2].equals("1.0");
            if (!associations.containsKey(key) && connected) {
                String entityName = key.split(",")[0];
                String node = key.split(",")[1].replaceAll("\\]", "");
                virtuals.get(entityName).add(node);
            }

        }
    }

    private void addNode(String entityName) throws IOException {
        if (!virtuals.containsKey(entityName)) {
            System.out.println("ADDING " + entityName);
            String result1 = RestClient.getInstance().callPut("http://uberdust.cti.gr/rest/testbed/1/node/" + entityName + "/");
        }
    }

    public static void main(String[] ars) {
//        (new VirtualNodeChecker()).run();

        String contents = "http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:room:0.I.1/rdf/rdf+xml/";
        String vitual = "virtual";
        if (contents.contains(vitual)) {
            int start = contents.indexOf(vitual) + vitual.length() + 1;
            int end = contents.indexOf("/", start);
            System.out.println("start:" + start + ",end:" + end);
            final String type = contents.substring(start, end).split(":")[0];
            final String str = contents.substring(start, end).split(":")[1];
            System.out.println(contents.substring(start, end));


            System.out.println(type);
            System.out.println(str);
        }
    }
}

