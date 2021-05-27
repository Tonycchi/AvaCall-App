package com.example.model.connection;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.data.ConnectedDevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by User on 12/21/2016.
 */

@SuppressLint("LogNotTimber")
public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";

    private static final String APP_NAME = "AvaCall";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter BLUETOOTH_ADAPTER;
    //0 is not tested, 1 is connected, 2 is could not connect, 3 is connection lost
    private MutableLiveData<Integer> connectionStatus;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothDevice bluetoothDevice;

    public BluetoothConnectionService() {
        BLUETOOTH_ADAPTER = BluetoothAdapter.getDefaultAdapter();
        connectionStatus = new MutableLiveData<Integer>();
    }

    public MutableLiveData<Integer> getConnectionStatus() {
        return connectionStatus;
    }

    public void connectingCanceled() {
        if (connectThread != null)
            connectThread.cancel();
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    /**
     * AcceptThread starts and sits waiting for a connection.
     * Then ConnectThread starts and attempts to make a connection with the other devices AcceptThread.
     **/

    public void startClient(BluetoothDevice device, ParcelUuid[] deviceUUIDs) {
        start();
        Log.d(TAG, "startClient: Started.");
        connectionStatus.setValue(0);
        connectThread = new ConnectThread(device, deviceUUIDs);
        connectThread.start();
    }

    /**
     * Starts the bluetooth connection with the given socket
     *
     * @param bluetoothSocket the socket to connect with
     */
    private void connected(BluetoothSocket bluetoothSocket) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();
    }

    /**
     * Cancels the existing connection
     */
    public void cancel() {
        Log.d(TAG, "cancel: Connection cancelled.");
        connectedThread.cancel();
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Synchronize a copy of the ConnectedThread
        Log.d(TAG, "write: Write Called.");
        //perform the write
        connectedThread.write(out);
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {

        // The local server socket
        private final BluetoothServerSocket SERVER_SOCKET;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = BLUETOOTH_ADAPTER.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);

                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            SERVER_SOCKET = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread Running.");

            BluetoothSocket bluetoothSocket = null;

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start.....");

                bluetoothSocket = SERVER_SOCKET.accept();

                Log.d(TAG, "run: RFCOM server socket accepted connection.");

            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            if (bluetoothSocket != null) {
                connected(bluetoothSocket);
            }

            Log.d(TAG, "END AcceptThread ");
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket bluetoothSocket;
        private ParcelUuid[] deviceUUIDs;

        public ConnectThread(BluetoothDevice device, ParcelUuid[] deviceUUIDs) {
            Log.d(TAG, "ConnectThread: started.");
            bluetoothDevice = device;
            this.deviceUUIDs = deviceUUIDs;
        }

        public void run() {
            Log.d(TAG, "number of UUIDs: " + deviceUUIDs.length);

            boolean isConnected = false;

            for (ParcelUuid mDeviceUUID : deviceUUIDs) {
                try {
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(mDeviceUUID.getUuid());
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                BLUETOOTH_ADAPTER.cancelDiscovery();

                try {
                    bluetoothSocket.connect();
                    isConnected = true;
                    break;
                } catch (IOException e) {
                    // Close the socket
                    try {
                        bluetoothSocket.close();
                        Log.d(TAG, "run: Closed Socket.");
                    } catch (IOException e1) {
                        Log.e(TAG, "ConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                    }
                    Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + mDeviceUUID.getUuid());
                }
            }
            connected(bluetoothSocket);
        }

        /**
         * Cancels the connection to the socket
         */
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of bluetoothSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }

    /**
     * Finally the ConnectedThread which is responsible for maintaining the BTConnection, Sending the data, and
     * receiving incoming data through input/output streams respectively.
     **/
    private class ConnectedThread extends Thread {
        private final BluetoothSocket BLUETOOTH_SOCKET;
        private final InputStream INPUT_STREAM;
        private final OutputStream OUTPUT_STREAM;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");

            BLUETOOTH_SOCKET = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = BLUETOOTH_SOCKET.getInputStream();
                tmpOut = BLUETOOTH_SOCKET.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            INPUT_STREAM = tmpIn;
            OUTPUT_STREAM = tmpOut;
        }

        public void run() {
            Log.d(TAG, "connectedThread running");

            //TODO: only do this if its an ev3!
            connectionStatus.postValue(1);
            writeToDatabase();

            byte[] buffer = new byte[1024];  // buffer store for the stream

            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = INPUT_STREAM.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    Intent incomingMessageIntent = new Intent("incomingMessage");
                    incomingMessageIntent.putExtra("theMessage", incomingMessage);

                } catch (IOException e) {
                    // could not connect, so connection status gets set to 2
                    if (connectionStatus.getValue() == 0) {
                        connectionStatus.postValue(2);
                    }
                    // connection got lost, so status gets set to 3
                    if (connectionStatus.getValue() == 1) {
                        connectionStatus.postValue(3);
                    }
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                    break;
                }
            }
        }

        private void writeToDatabase(){
            String address = bluetoothDevice.getAddress();
            long currentTime = System.currentTimeMillis();
            ConnectedDevice connectedDevice = new ConnectedDevice(address, currentTime);
            Log.d(TAG, "writeToDatabase: Address:"+address+" currentTime:"+currentTime);
        }

        /**
         * this method gets called from main activity to send data to the remote device
         * this method also gets called once at the start to make sure the connection was successful
         *
         * @param bytes the bytes to be send
         */
        public void write(byte[] bytes) {
            //String text = new String(bytes, Charset.defaultCharset());
            //Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                OUTPUT_STREAM.write(bytes);
            } catch (IOException e) {
                // could not connect, so connection status gets set to 2
                if (connectionStatus.getValue() == 0) {
                    connectionStatus.postValue(2);
                }
                // connection got lost, so status gets set to 3
                if (connectionStatus.getValue() == 1) {
                    connectionStatus.postValue(3);
                }
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
            }
            // if connection status is still 0 at this point,
            // the connection was successful and it gets set to 1
            if (connectionStatus.getValue() == 0) {
                connectionStatus.postValue(1);
            }
        }

        /**
         * This method gets called from main activity to shutdown the connection
         */
        public void cancel() {
            try {
                BLUETOOTH_SOCKET.close();
            } catch (IOException ignored) {
            }
        }
    }
}