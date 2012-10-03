package eu.uberdust.sparql.endpoint.servlet;

import eu.uberdust.sparql.endpoint.Rule;
import eu.uberdust.sparql.endpoint.RuleManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: amaxilatis
 * Date: 9/23/12
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewRuleServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ViewRuleServlet.class);


    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        StringBuilder body = new StringBuilder();
        body.append("<body>");
        String ruleID = req.getRequestURL().substring(req.getRequestURL().indexOf("/rule/") + "/rule/".length());
        Rule rule = RuleManager.getInstance().getRule(ruleID);
        if (rule != null) {
            RuleManager.getInstance().delete(ruleID);

        } else {
            body.append("<h2>No Rule with id " + ruleID + "!</h2><br>");
        }


        resp.getOutputStream().write(body.toString().getBytes());
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOGGER.debug("doGet");
        StringBuilder body = new StringBuilder();
        body.append("<body>");
        String ruleID;
        try {
            ruleID = req.getRequestURL().substring(req.getRequestURL().indexOf("/rule/") + "/rule/".length());
            ruleID = ruleID.replaceAll("/", "");
        } catch (NumberFormatException nfe) {
            LOGGER.debug("nfe");
            if (req.getRequestURL().substring(req.getRequestURL().indexOf("/rule/") + "/rule/".length()).endsWith("/result")) {
                String mrule = req.getRequestURL().substring(req.getRequestURL().indexOf("/rule/") + "/rule/".length());
                ruleID = mrule.replaceAll("/result", "");
                body = new StringBuilder();
                body.append(RuleManager.getInstance().getRule(ruleID).getResult());
                resp.getOutputStream().write(body.toString().getBytes());

                return;
            } else {
                return;
            }
        }

        Rule rule = RuleManager.getInstance().getRule(ruleID);
        if (rule != null) {
            body.append("<h2>View Rule " + ruleID + "</h2>");
            body.append("<h4>Sparql: </h4>");
            body.append(rule.getSparql() + "</br>");


            //                rule.execute();
            body.append("<h4>LastRun: ");
            body.append(new Date(rule.getLastRun()) + "</h4>");
            body.append("<h4>Result: ");
            body.append(rule.getResult() + "</h4>");
            if (rule.getResult().equals("true")) {
                body.append("<h4>Response: </h4>");
                body.append(rule.getResponse() + "</br>");
            }
//            body.append("<h4>On True: </h4>");
//            body.append(rule.getOnTrue() + "</br>");
//            body.append("<h4>On False: </h4>");
//            body.append(rule.getOnFalse() + "</br>");


        } else {
            body.append("<h2>No Rule with id " + ruleID + "!</h2><br>");
        }


        resp.getOutputStream().write(body.toString().getBytes());
    }

}