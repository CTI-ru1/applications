package eu.uberdust.sparql.endpoint;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/10/11
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public final class RuleManager {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RuleManager.class);

    HashMap<String, Rule> ruleList;

    /**
     * static instance(ourInstance) initialized as null.
     */
    private static RuleManager ourInstance = null;

    /**
     * RestClient is loaded on the first execution of RestClient.getInstance()
     * or the first access to RestClient.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static RuleManager getInstance() {
        synchronized (RuleManager.class) {
            if (ourInstance == null) {
                ourInstance = new RuleManager();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private RuleManager() {
        ruleList = new HashMap<String, Rule>();
        parseFromFile();

        Timer executeTimer = new Timer();
        executeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LOGGER.debug("running checks...");

                for (String key : ruleList.keySet()) {
                    try {
                        ruleList.get(key).execute();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (DocumentException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (SAXException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                try {
                    saveRules();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (TransformerException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }, 2000, 3000);


    }

    private void parseFromFile() {

        File dir = new File("rules");
        File[] files = null;
        if (dir.isDirectory()) {
            files = dir.listFiles();
        } else {
            return;
        }

        try {

            for (File file : files) {
                if (!(file.getName().endsWith(".xml") && file.getName().startsWith("rule-"))) continue;
                LOGGER.debug(file.getName());

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();

//            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                NodeList nList = doc.getElementsByTagName("rule");
                LOGGER.debug("-----------------------");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;

                        Rule newRule = new Rule();
                        newRule.setId(getTagValue("id", eElement));
                        newRule.setSparql(getTagValue("sparql", eElement));
                        newRule.setInterval(Integer.parseInt(getTagValue("interval", eElement)));
                        newRule.setLastRun(Long.parseLong(getTagValue("lastrun", eElement)));
                        newRule.setResult(getTagValue("result", eElement));
                        newRule.setResponse(getTagValue("response", eElement));
                        newRule.setOnTrue(getTagValue("onTrue", eElement));
                        newRule.setOnFalse(getTagValue("onFalse", eElement));
                        LOGGER.debug(newRule);
                        ruleList.put(newRule.getId(), newRule);
                    }
                }
            }


            saveRules();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
    }

    public Map<String, Rule> list() {
        return ruleList;
    }

    private void saveRules() throws ParserConfigurationException, TransformerException, IOException {
        LOGGER.info("Saving Rules...");
        for (Rule ruleObj : ruleList.values()) {
            LOGGER.debug("Saving Rule " + ruleObj.getId());

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("rules");
            doc.appendChild(rootElement);


            // rule elements
            Element rule = doc.createElement("rule");
            rootElement.appendChild(rule);

            // set attribute to rule element
//            Attr attr = doc.createAttribute("id");
//            attr.setValue(String.valueOf(ruleObj.getId()));
//            rule.setAttributeNode(attr);
            Element sparql = doc.createElement("sparql");
            rule.appendChild(sparql);
            sparql.setTextContent(ruleObj.getSparql());
            Element id = doc.createElement("id");
            rule.appendChild(id);
            id.setTextContent(String.valueOf(ruleObj.getId()));
            Element interval = doc.createElement("interval");
            rule.appendChild(interval);
            interval.setTextContent(String.valueOf(ruleObj.getInterval()));
            Element lastrun = doc.createElement("lastrun");
            rule.appendChild(lastrun);
            lastrun.setTextContent(String.valueOf(ruleObj.getLastRun()));
            Element response = doc.createElement("response");
            rule.appendChild(response);
            response.setTextContent(String.valueOf(ruleObj.getResponse()));
            Element result = doc.createElement("result");
            rule.appendChild(result);
            result.setTextContent(String.valueOf(ruleObj.getResult()));
            Element onTrue = doc.createElement("onTrue");
            rule.appendChild(onTrue);
            onTrue.setTextContent(String.valueOf(ruleObj.getOnTrue()));
            Element onFalse = doc.createElement("onFalse");
            rule.appendChild(onFalse);
            onFalse.setTextContent(String.valueOf(ruleObj.getOnFalse()));
//            System.out.println(ruleObj);


            DOMSource domSource = new DOMSource(doc);
            final String filename = "rules/rule-" + ruleObj.getId() + ".xml";
            LOGGER.debug("Saving to File " + filename);
            FileWriter writer = new FileWriter(filename);
            StreamResult streamResult = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, streamResult);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            writer.flush();
//        LOGGER.info(writer.toString());

        }
    }

    public Rule getRule(String ruleID) {
        for (Rule rule : ruleList.values()) {
            LOGGER.debug(rule.getId() + "==" + ruleID);
            if (rule.getId().equals(ruleID))
                return rule;
        }
        return null;
    }

    public void add(Rule newRule) {

        ruleList.put(newRule.getId(), newRule);
        try {
            saveRules();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void delete(String ruleID) {
        for (Rule rule : ruleList.values()) {
            if (rule.getId().equals(ruleID)) {
                ruleList.remove(rule.getId());
                try {
                    saveRules();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (TransformerException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return;
            }
        }
    }

    public static void main(String[] args) {

        RuleManager.getInstance().parseFromFile();
    }
}
