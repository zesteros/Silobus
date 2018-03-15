package itl.silobus.comm;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import itl.silobus.R;
import itl.silobus.util.calculator.BusCalculator;

/**
 * Created by Angelo on 10/02/2017.
 */

public class ConnectionThread extends Thread {
    //private BusCalculator mBusCalculator;
    private Context mContext;
    private String mIp;
    private int mPort;
    private Socket mSocket;
    private BufferedReader mInput;
    private SensorDataUpdater mUpdater;
    private boolean keepRunning = true;


    /**
     * @param context the context of the app
     * @param ip      the ip to connect
     * @param port    the port to connect
     */
    public ConnectionThread(Context context, String ip, int port) {
        this.mContext = context;
        this.mIp = ip;
        this.mPort = port;
        mUpdater = new SensorDataUpdater();
        //mBusCalculator = new BusCalculator();
    }

    /**
     * @param ip to connect
     * @param port  to connect
     */
    public ConnectionThread(String ip, int port) {
        this.mIp ="http://"+ip;
        this.mPort = port;
    }


    /**
     * @return if connection was success
     * @throws IOException for internet socket
     */
    public boolean testConnection() throws IOException {
        /*Try the connection*/
        try {
            mSocket = new Socket(mIp, mPort);
           //mSocket.setSoTimeout(3000);
        } catch (ConnectException ce) {
            ce.printStackTrace();
            //if the exception contains some of this errors, send the notification
            if (ce.toString().contains(mContext.getString(
                    R.string.network_unreachable_exception)
            )) {
                notifyConnectionError(ConnectionStatus.INTERNET_DISCONNECTED);
                isKeepRunning(false);
            } else if (ce.toString().contains(mContext.getString(
                    R.string.no_route_to_host_exception)
            )) {
                notifyConnectionError(ConnectionStatus.NO_ROUTE_TO_HOST);
                isKeepRunning(false);
            } else if (ce.toString().contains(mContext.getString(
                    R.string.connection_refused_exception))) {
                notifyConnectionError(ConnectionStatus.CANNOT_CONNECT);
                isKeepRunning(false);
            }
            ce.printStackTrace();
            Log.e("error", ce + "");
            if (mSocket != null) mSocket.close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Method to run while app is open
     */
    @Override
    public void run() {
        try {
            //start the mCounter for obtain avg
            //start infinite loop
            while (isKeepRunning()) {
                if (testConnection()) {
                    try {
                       // Thread.sleep(2000);
                        //get the input from Arduino
                        mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                        /*
                        *   Here we going to debug the message from Arduino
                        */
                        mUpdater.debugMessage(mInput.readLine());
                        //mBusCalculator.determinePassengers();
                        //updateUI();
                        if (mSocket != null) mSocket.close();
                    } catch (Exception e) {
                        //if something went wrong notify to user almost the reason is
                        //the connection was lost
                        mInput.close();
                        mSocket.close();
                        e.printStackTrace();
                        notifyConnectionError(ConnectionStatus.LOST);
                        //break;
                    }
                }
            }
            //} catch (ConnectException ce) {
            //  ce.printStackTrace();
            //notifyConnectionError(ConnectionStatus.IP_PORT_ERROR);
        } catch (Exception e) {
            notifyConnectionError(ConnectionStatus.LOST);
            e.printStackTrace();
        }
    }


    public void updateUI() {

    }

    /**
     * @param status the status to evaluate
     */
    public void notifyConnectionError(final ConnectionStatus status) {
        /*
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mDataActivity.notification.getNotificationManager().cancel(NotificationHelper.RECEIVING_DATA);
                switch (status) {
                    case NO_ROUTE_TO_HOST:
                        mUserInteraction.showSnackbar(R.string.no_route_to_host);
                        break;
                    case LOST:
                        mUserInteraction.showSnackbar(R.string.conection_lost);
                        break;
                    case IP_PORT_ERROR:
                        mUserInteraction.showSnackbar(R.string.ip_port_error);
                        break;
                    case INPUT_ERROR:
                        mUserInteraction.showSnackbar(R.string.input_error);
                        break;
                    case INTERNET_DISCONNECTED:
                        mUserInteraction.showSnackbar(R.string.internet_disconnected);
                        break;
                    case CANNOT_CONNECT:
                        mUserInteraction.showSnackbar(R.string.connection_refused);
                        break;
                }
            }
        });*/
    }

    /**
     * @param keepRunning setter for keep running thread
     */
    public void isKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }


    /**
     * @return if the thread is running
     */
    public boolean isKeepRunning() {
        return keepRunning;
    }
}
