package eu.uberdust.sparql.endpoint.servlet;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: amaxilatis
 * Date: 9/23/12
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddRuleFormServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AddRuleServlet.class);


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader bin = new BufferedReader(
                new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("add.html")));
        StringBuilder body = new StringBuilder();
        String c = bin.readLine();
        while (c != null) {
            body.append(c);
            c = bin.readLine();
        }
        resp.getOutputStream().write(body.toString().getBytes());
    }

}