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
public class ListRulesServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ListRulesServlet.class);


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseURL = req.getRequestURL().substring(0, req.getRequestURL().lastIndexOf("/"));

        StringBuilder body = new StringBuilder();
        body.append("<head>");
        body.append("<title>Rule List</title>");
        body.append("</head>");
        body.append("<body>");
        body.append("<table>");
        body.append("<tr><th></th><th>Rule</th><th>Interval</th><th>LastRun</th></tr>");
        for (Rule rule : RuleManager.getInstance().list().values()) {
            body.append("<tr>" +
                    "<td><a href='" + baseURL + "/rule/" + rule.getId() + "/'>" + rule.getId() + "</a>" +
                    "</td><td width=200px>" + rule.getSparql() + "</td>" +
                    "<td>" + rule.getInterval() + "</td>" +
                    "<td>" + new Date(rule.getLastRun()) + "</td></tr>");
        }
        body.append("</table>");

        resp.getOutputStream().write(body.toString().getBytes());
    }

}