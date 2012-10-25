package eu.uberdust.sparql.endpoint;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import eu.uberdust.communication.RestClient;
import eu.uberdust.communication.UberdustClient;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: amaxilatis
 * Date: 9/24/12
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class Rule {

    private static final Logger LOGGER = Logger.getLogger(Rule.class);

    private String id;
    private String sparql;
    private int interval;
    private long lastRun;
    private String response;
    private String result;
    private String onTrue;

    public String getOnTrue() {
        return onTrue;
    }

    public String getOnFalse() {
        return onFalse;
    }

    private String onFalse;

    public long getLastRun() {
        return lastRun;
    }

    public void setLastRun(long lastRun) {
        this.lastRun = lastRun;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSparql() {
        return sparql;
    }

    public void setSparql(String sparql) {
        this.sparql = sparql;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void execute() throws IOException, DocumentException, SAXException {
        if (this.getLastRun() + this.getInterval() * 1000 < System.currentTimeMillis()) {
            LOGGER.info("Running rule " + id);
            String response1 = UberdustClient.getInstance().makeSparqlQuery(getSparql());
            boolean f1 = hasResults(response1);
            setResponse(response1);
            setResult(String.valueOf(f1));
            setLastRun(System.currentTimeMillis());

            LOGGER.info(f1);
            LOGGER.info(onTrue != null);
            LOGGER.info(onTrue.equals(""));
            LOGGER.info(((f1) && (onTrue != null) && (!onTrue.equals(""))));
            LOGGER.info(onFalse != null);
            LOGGER.info(onFalse.equals(""));
            LOGGER.info(((!f1) && (onFalse != null) && (!onFalse.equals(""))));
            if ((f1) && (onTrue != null) && (!onTrue.equals(""))) {
                LOGGER.debug("calling onTrue");
                RestClient.getInstance().callRestfulWebService(onTrue);
            }
            if ((!f1) && (onFalse != null) && (!onFalse.equals(""))) {
                LOGGER.debug("calling onFalse");
                RestClient.getInstance().callRestfulWebService(onFalse);
            }

        }

    }


    public static boolean hasResults(String response) throws DocumentException, IOException, SAXException {
//        System.out.println(URLDecoder.decode(response));
        DOMParser parser = new DOMParser();

        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(response));
        parser.parse(is);
        org.w3c.dom.Document document = parser.getDocument();
        NodeList nodes = document.getElementsByTagName("result");
        if (nodes.getLength() == 0) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                '}';
    }

    public void setOnTrue(String onTrue) {
        this.onTrue = onTrue;
    }

    public void setOnFalse(String onFalse) {
        this.onFalse = onFalse;
    }
}
