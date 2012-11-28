package eu.uberdust.virtualnodemanager.tasks;

import eu.uberdust.communication.RestClient;
import eu.uberdust.util.PropertyReader;

import java.io.IOException;
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
    /**
     * Static Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(VirtualNodeChecker.class);

    Map<String, List<String>> virtuals;
    Map<String, Integer> associations;
    private String uberdustURL;

    @Override
    public void run() {
        uberdustURL = (String) PropertyReader.getInstance().getProperties().get("uberdustURL");
        try {
            check();
        } catch (Exception e) {
            LOGGER.error(e, e);
        }
    }

    public void check() throws Exception {
        virtuals = new HashMap<String, List<String>>();
        associations = new HashMap<String, Integer>();

        String nodes = RestClient.getInstance().callRestfulWebService(uberdustURL + "/rest/testbed/1/node/raw");
        String[] lines = nodes.split("urn:");
        LOGGER.info("Nodes Found: " + lines.length);
        for (String line : lines) {
            if (line.contains(":virtual:")) {
                virtuals.put("urn:" + line, new ArrayList<String>());
            }
        }
        poppulateNodes();

        displayEntities(virtuals);


        for (String capability : ((String) PropertyReader.getInstance().getProperties().get("virtual.capabilities")).split(",")) {
            associate(capability);
        }

        String response = RestClient.getInstance().callRestfulWebService(uberdustURL + "/rest/testbed/1/capability/virtual/tabdelimited");
        String[] responseLines = response.split("\\[");
        LOGGER.info("Virtual Relations Found: " + responseLines.length);
        for (String line : responseLines) {
            if (line.equals("")) continue;

            String key = line.split("\t")[0];
            Boolean connected = line.split("\t")[2].equals("1.0");
            if (!associations.containsKey(key) && connected) {
                for (String capability : ((String) PropertyReader.getInstance().getProperties().get("virtual.capabilities")).split(",")) {
                    if (key.contains(capability)) {
                        String entityName = key.split(",")[0];
                        String node = key.split(",")[1].replaceAll("\\]", "");
                        disconnect(entityName, node);
                        LOGGER.error("SHOULD REMOVE " + key);
                    }
                }
            }
        }
    }

    private void associate(String property) {
        String result = RestClient.getInstance().callRestfulWebService(uberdustURL + "/rest/testbed/1/capability/" + property + "/tabdelimited");
        String[] parts = result.split("\t");
        for (int i = 0; i < parts.length; i += 4) {

            String node = parts[i];
            for (String briefEntityName : parts[i + 3].split(",")) {
                String entityName = PropertyReader.getInstance().getTestbedPrefix() + "virtual:" + property + ":" + briefEntityName;

                associations.put(entityName + "," + node + "]", 1);

                if (isAssociated(entityName, node)) {
                    LOGGER.info(entityName + "---" + node + " Already Associated!");
                    continue;
                }
                LOGGER.error(entityName + "---" + node + " need to be Associated!");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

                }

                try {
                    addNode(entityName);
                    connect(entityName, node);

                    virtuals.put(entityName, new ArrayList<String>());
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
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
        LOGGER.info("Existing Virtual Entities:");
        for (String s : virtuals.keySet()) {
            LOGGER.info(s + " with " + virtuals.get(s).size() + " nodes");
            LOGGER.info("=========================");
            for (String node : virtuals.get(s)) {
                LOGGER.info("= " + node);
            }
        }
    }


    private void poppulateNodes() {
        String response = RestClient.getInstance().callRestfulWebService(uberdustURL + "/rest/testbed/1/capability/virtual/tabdelimited");
        String[] responseLines = response.split("\\[");
        LOGGER.info("Virtual Associations Found: " + responseLines.length);
        for (String line : responseLines) {
            if (line.equals("")) continue;
            System.out.println(line);
            String key = line.split("\t")[0];
            Boolean connected = line.split("\t")[2].equals("1.0");
            if (!associations.containsKey(key) && connected) {
                String entityName = key.split(",")[0];
                String node = key.split(",")[1].replaceAll("\\]", "");
                if (!virtuals.containsKey(entityName)) {
                    virtuals.put(entityName, new ArrayList<String>());
                }
                virtuals.get(entityName).add(node);
            }

        }
    }

    private void addNode(String entityName) throws IOException {
        if (!virtuals.containsKey(entityName)) {
            LOGGER.info("ADDING " + entityName);
            String result1 = RestClient.getInstance().callPut(uberdustURL + "/rest/testbed/1/node/" + entityName + "/");
        }
    }

    private void connect(String entityName, String node) {
        String url = uberdustURL + "/rest/testbed/1/link/" + entityName + "/" + node + "/capability/virtual/insert/timestamp/0/reading/1/";
        LOGGER.info("Calling " + url);
        RestClient.getInstance().callRestfulWebService(url);
    }

    private void disconnect(String entityName, String node) {
        String url = uberdustURL + "/rest/testbed/1/link/" + entityName + "/" + node + "/capability/virtual/insert/timestamp/0/reading/0/";
        LOGGER.info("Calling " + url);
        RestClient.getInstance().callRestfulWebService(url);
    }
}

