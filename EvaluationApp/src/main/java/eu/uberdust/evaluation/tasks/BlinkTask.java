package eu.uberdust.evaluation.tasks;

import eu.uberdust.evaluation.RestClient;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 11/14/11
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlinkTask extends TimerTask {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(BlinkTask.class);

    public static final long DELAY = 10000;

    private final Timer timer;

    public BlinkTask(final Timer thatTimer) {
        super();
        this.timer = thatTimer;
    }

    @Override
    public final void run() {
        LOGGER.info("BLINKING");
        RestClient.getInstance().callRestfulWebService(RestClient.EVALUATION_URL);
        timer.schedule(new BlinkTask(timer), DELAY);
    }
}
