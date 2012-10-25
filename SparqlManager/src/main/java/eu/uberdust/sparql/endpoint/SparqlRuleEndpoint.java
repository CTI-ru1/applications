package eu.uberdust.sparql.endpoint;


import eu.uberdust.communication.UberdustClient;
import eu.uberdust.sparql.endpoint.servlet.AddRuleFormServlet;
import eu.uberdust.sparql.endpoint.servlet.AddRuleServlet;
import eu.uberdust.sparql.endpoint.servlet.ListRulesServlet;
import eu.uberdust.sparql.endpoint.servlet.ViewRuleServlet;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Main App.
 */
public class SparqlRuleEndpoint {

    /**
     * Main Function.
     *
     * @param args String Arguments
     */
    public static void main(String[] args) throws Exception {

        PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.properties"));

        UberdustClient.getInstance().setUberdustURL("http://uberdust.cti.gr");

        RuleManager.getInstance();

        Server server = new Server(9000);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);


        // list all rules
        context.addServlet(new ServletHolder(new ListRulesServlet()), "/rules");

        //add new rule
        context.addServlet(new ServletHolder(new AddRuleServlet()), "/rule/add");
        //add new rule
        context.addServlet(new ServletHolder(new ViewRuleServlet()), "/rule/*");

        //add new rule
        context.addServlet(new ServletHolder(new AddRuleFormServlet()), "/add.html");

        server.start();

    }
}
