package com.example.rcvc;

import android.util.Log;

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