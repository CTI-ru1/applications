package eu.uberdust.foiapp.test;

import eu.uberdust.application.foi.manager.PresenseManager;
import eu.uberdust.communication.protobuf.Message;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * Unit test for simple App.
 */
public class PresenseManagerTest
        extends TestCase {
    private static final Logger LOGGER = Logger.getLogger(PresenseManagerTest.class);

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PresenseManagerTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(PresenseManagerTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        try {
            PresenseManager.getInstance();
            //Stays Empty at the beggining
            for (int i = 0; i < 4; i++) {
                assertEquals(PresenseManager.EMPTY, PresenseManager.getInstance().getPrevState());
            }
            //Becomes a NEW_ENTRY for 2*PIR_DELAY
            for (int i = 0; i < 4; i++) {
                PresenseManager.getInstance().addReading(creatOccupiedReading());
                assertEquals(PresenseManager.NEW_ENTRY, PresenseManager.getInstance().getPrevState());
                Thread.sleep(500);
            }
            //Becomes and stays OCCUPIED while having events
            for (int i = 0; i < 4; i++) {
                PresenseManager.getInstance().addReading(creatOccupiedReading());
                assertEquals(PresenseManager.OCCUPIED, PresenseManager.getInstance().getPrevState());
                Thread.sleep(500);
            }
            //Stays OCCUPIED for PIR_DELAY {500+500}
            assertEquals(PresenseManager.OCCUPIED, PresenseManager.getInstance().getPrevState());
            Thread.sleep(500);

            //Stays LEFT for another PIR_DELAY {500+500}
            assertEquals(PresenseManager.LEFT, PresenseManager.getInstance().getPrevState());
            Thread.sleep(500);
            assertEquals(PresenseManager.LEFT, PresenseManager.getInstance().getPrevState());
            Thread.sleep(500);

            //Stays EMPTY forvever
            for (int i = 0; i < 4; i++) {
                assertEquals(PresenseManager.EMPTY, PresenseManager.getInstance().getPrevState());
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private Message.NodeReadings.Reading creatEmptyReading() {
        return Message.NodeReadings.Reading.newBuilder().setCapability("urn:wisebed:node:capability:pir").setDoubleReading(0).setNode("urn:wisebed:ctitestbed:1").setTimestamp(System.currentTimeMillis()).build();
    }

    private Message.NodeReadings.Reading creatOccupiedReading() {
        return Message.NodeReadings.Reading.newBuilder().setCapability("urn:wisebed:node:capability:pir").setDoubleReading(1).setNode("urn:wisebed:ctitestbed:1").setTimestamp(System.currentTimeMillis()).build();
    }
}

