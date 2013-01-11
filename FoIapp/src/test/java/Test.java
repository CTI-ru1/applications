import eu.uberdust.util.PropertyReader;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: dimitris
 * Date: 10/31/12
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    public static void main(final String[] args) {

        PropertyReader.getInstance().setFile("config.properties.template");

        //get the property value and print it out
        System.out.println(PropertyReader.getInstance().getProperties().getProperty("FOI"));
        System.out.println(PropertyReader.getInstance().getProperties().getProperty("ZONES"));

    }

}