import eu.uberdust.communication.UberdustClient;
import org.apache.log4j.Logger;

import java.io.IOException;

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

        try {
            UberdustClient.getInstance().sendCoapPost("2df", "lz1", "1");
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
 }

}