package com.example.rcvc;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.function.Function;

@SuppressLint("LogNotTimber")
public class DirectCommander {

    private static final String TAG = "DirectCommander";

    //start and end part of direct commands used to control EV3
    private static final String startDirCom = "0D002A00800000A4000";
    //private final String middleDirCom = "000";
    private static final String endDirCom = "A6000";

    //ports that are used to control the ev3 coded in hex
    //private final String port_AD = "9";
    private static final String port_A = "1";
    private static final String port_D = "8";

    //definitions will probably be able to be set in settings
    //should probably make library of ev3 command parts
    private static final byte PORT_RIGHT = 0x01;
    private static final byte PORT_LEFT = 0x08;

    //start + port + power +  end + port

    private static int maxPower = 50;

    public static void setMaxPow(int maxPow) {
        int pow = maxPow;
        if (maxPow > 100) {
            pow = 100;
        }
        if (maxPow < -100) {
            pow = -100;
        }
        maxPower = pow;
    }

    private static String convToHexString(float val) {
        int strength = (int) (val * maxPower);
        Log.d(TAG, "intStrength: " + strength);
        String output = Integer.toHexString(strength);
        Log.d(TAG, "hexString: " + output);
        if (output.length() == 1) {
            output = "0" + output;
        }
        if (output.length() > 2) {
            output = output.substring(output.length() - 2);
        }
        Log.d(TAG, "hexString: " + output);
        output = "81" + output;
        return output;
    }

    public static void send(float right, float left, BluetoothConnectionService b) {
        //TODO
        /* converts motor powers to direct commands */
        /* connection.write(direct commands) */
        /* maybe static maybe as object */
        /* robot controller computes powers instead, similar class for joystick */

        /*
        String powR = convToHexString(right);
        String powL = convToHexString(left);
        Log.d(TAG, "right pow: " + powR);
        Log.d(TAG, "left pow: " + powL);
        String outputStringR = startDirCom + port_A + powR + endDirCom + port_A;
        String outputStringL = startDirCom + port_D + powL + endDirCom + port_D;
        Log.d(TAG, "right com: " + outputStringR);
        Log.d(TAG, "left com: " + outputStringL);
        byte[] outputR = hexStringToByteArray(outputStringR);
        byte[] outputL = hexStringToByteArray(outputStringL);
        b.write(outputR);
        b.write(outputL);
         */

        byte[] command = createCommand(calcPower(right), calcPower(left));
        b.write(command);
    }

    /**
     * creates a direct command for movement
     *
     * @param right,left motor speeds
     * @return direct command as byte array
     */
    public static byte[] createCommand(byte right, byte left) {
        //0x|14:00|2A:00|80|00:00|A4|00|01|81:RP|A4|00|08|81:LP|A6|00|09
        //   0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19
        // 00: length 20
        // 11 & 16: right and left motor speeds

        byte LENGTH = 20;
        byte[] y = new byte[LENGTH];
        for (int i = 0; i < LENGTH; i++) {
            y[i] = 0;
        }

        y[0] = LENGTH;
        y[2] = 0x2a;
        y[4] = (byte) 0x80;
        y[7] = (byte) 0xa4;
        y[10] = (byte) 0x81;
        y[12] = (byte) 0xa4;
        y[15] = (byte) 0x81;
        y[17] = (byte) 0xa6;
        y[19] = PORT_RIGHT + PORT_LEFT;

        y[9] = PORT_RIGHT;    // PORT right motor
        y[11] = right;  // POWER right motor

        y[14] = PORT_LEFT;   // PORT left motor
        y[16] = left;   // POWER left motor

        return y;
    }

    public static byte calcPower(float x) {
        return (byte) (x * maxPower);
    }

    /**
     * converts a string to a byte array
     *
     * @param s the input string
     * @return the byte array
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}