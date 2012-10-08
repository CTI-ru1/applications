package eu.uberdust.sparql.endpoint.servlet;

import eu.uberdust.sparql.endpoint.Rule;
import eu.uberdust.sparql.endpoint.RuleManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: amaxilatis
 * Date: 9/23/12
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddRuleServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AddRuleServlet.class);


    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOGGER.debug("executing a put request");
        StringBuilder body = new StringBuilder();

        body.append("running put");


        // Extract the request content
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        String content = "";

        try {
            InputStream inputStream = req.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[1024];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        content = stringBuilder.toString();
        LOGGER.debug("Length: " + req.getContentLength());
        LOGGER.debug(content);
        try {
            JSONObject json = new JSONObject(content);
            Rule newRule = new Rule();
            newRule.setSparql((String) json.get("sparql"));
            newRule.setId((String) json.get("id"));
            newRule.setInterval(Integer.parseInt(String.valueOf(json.get("interval"))));
            LOGGER.debug(newRule);
            RuleManager.getInstance().add(newRule);
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        resp.getOutputStream().write(body.toString().getBytes());
    }

}