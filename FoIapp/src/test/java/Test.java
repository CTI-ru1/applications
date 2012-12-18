import eu.uberdust.MainApp;
import eu.uberdust.communication.UberdustClient;
import eu.uberdust.lights.GetJson;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: dimitris
 * Date: 10/31/12
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    private static final Logger LOGGER = Logger.getLogger(Test.class);

    public static void main(final String[] args) {


        Properties prop = new Properties();

        try {
            //load a properties file
            prop.load(new FileInputStream("config.properties"));

            //get the property value and print it out
            System.out.println(prop.getProperty("FOI"));
            System.out.println(prop.getProperty("ZONES"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //try {
            //set the properties value
         //   prop.setProperty("FOI", "workstation:0.I.2-2");
         //   prop.setProperty("ZONES", "1");

            //save properties to project root folder
         //   prop.store(new FileOutputStream("config.properties"), null);

        //} catch (IOException ex) {
        //    ex.printStackTrace();
        //}

       // System.out.println(GetJson.getInstance().callGetJsonWebService("http://150.140.16.31/api/v1/foi?identifier=workstation:0.I.2-2","mode"));  //350
       //UberdustClient.getInstance().sendCoapPost("2df", "lz1", "1");

 }


}