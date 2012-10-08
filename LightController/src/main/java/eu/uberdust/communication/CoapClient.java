package eu.uberdust.communication;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by IntelliJ IDEA.
 * User: akribopo
 * Date: 10/10/11
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public final class CoapClient {

    /**
     * Static Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CoapClient.class);


    /**
     * static instance(ourInstance) initialized as null.
     */
    private static CoapClient ourInstance = null;

    /**
     * RestClient is loaded on the first execution of RestClient.getInstance()
     * or the first access to RestClient.ourInstance, not before.
     *
     * @return ourInstance
     */
    public static CoapClient getInstance() {
        synchronized (CoapClient.class) {
            if (ourInstance == null) {
                ourInstance = new CoapClient();
            }
        }
        return ourInstance;
    }

    /**
     * Private constructor suppresses generation of a (public) default constructor.
     */
    private CoapClient() {
        coapPost("/urn:wisebed:ctitestbed:0x2df/lz0", "1");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        coapPost("/urn:wisebed:ctitestbed:0x2df/lz0", "0");
    }

    /**
     * Call Remote  Rest Interface.
     *
     * @param address the address
     * @return the return String
     */
    public String coapPost(final String address, final String value) {
        Request req = new Request(CodeRegistry.METHOD_POST, false);
        req.setURI(address);
        req.setMID((int) (System.currentTimeMillis() % 256));
        req.setPayload(value);
        byte[] buff = req.toByteArray();
        LOGGER.info(buff.length);
        try {
            DatagramSocket clientSocket = new DatagramSocket();

            clientSocket.send(new DatagramPacket(buff, 0, buff.length, InetAddress.getByName("150.140.5.72"), 5683));

            clientSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return "";
    }

    public static void main(String[] args) {
        CoapClient.getInstance();
    }

}
