package eu.uberdust.application.foi.util;

import eu.uberdust.MainApp;
import eu.uberdust.application.foi.controller.FoiController;
import eu.uberdust.communication.protobuf.Message;
import eu.uberdust.communication.websocket.readings.WSReadingsClient;
import org.apache.log4j.Logger;

import java.util.Observable;
import java.util.Observer;

/**
 * Last Readings Observer.
 */
public class ReadingsObserver implements Observer {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FoiController.class);

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     *            method.
     */
    @Override
    public void update(final Observable o, final Object arg) {

        if (!(o instanceof WSReadingsClient)) {
            return;
        }

        if (!(arg instanceof Message.NodeReadings)) {
            return;
        }

        final Message.NodeReadings readings = (Message.NodeReadings) arg;

        for (final Message.NodeReadings.Reading reading : readings.getReadingList()) {

//            if (MainApp.CAPABILITY_SCREENLOCK.equals(reading.getCapability())) {
//                LOGGER.info("New Reading for Screen Lock Capability");
//
//                final String node = "" + reading.getNode().split(":")[2];
//                final boolean isScreenLocked = ((reading.getDoubleReading() == 1) || (reading.getDoubleReading() == 3));
//                LOGGER.info(new StringBuilder().append("isScreenLocked: ")
//                        .append(reading.getDoubleReading()).append(" -- ")
//                        .append(isScreenLocked).append(" -- ").append(node).toString());
//
//                LOGGER.info("Calling setScreenLocked");
//                FoiController.getInstance().setScreenLocked(isScreenLocked);
//
//            } else if (MainApp.CAPABILITY_LIGHT.equals(reading.getCapability())) {
//                LOGGER.info("New Reading for Light Capability");
//
//                final Double value = reading.getDoubleReading();
//                LOGGER.info("Lum: " + value);
//
//                //if(MainApp.FOI.split(":")[0].equals("workstation")){
//                FoiController.getInstance().setLastLumReading(value);   //}
//
//            } else if (MainApp.CAPABILITY_PIR.equals(reading.getCapability())) {
//
//                final Double value = reading.getDoubleReading();
//
////                if (value == 1) {
//                    LOGGER.info("New Reading for Pir Capability");
//                    LOGGER.info("Pir value: " + value + "  Luminosity: " + FoiController.getInstance().getLastLumReading());
//
//                    final long timeStmp = reading.getTimestamp();
//                    FoiController.getInstance().setLastPirReading(timeStmp,value);  //timeStmp
//
////                }
//
//            }
        }
    }
}
