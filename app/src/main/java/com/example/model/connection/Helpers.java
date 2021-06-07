package com.example.model.connection;

public class Helpers {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes, int length){
        char[] hexArray = new char[length * 3];
        for (int j = 0; j < length; j++) {
            int v = bytes[j] & 0xFF;
            hexArray[j * 3] = HEX_ARRAY[v >>> 4];
            hexArray[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
            if(j%2==0)
                hexArray[j * 3 + 2] = ':';
            else
                hexArray[j * 3 + 2] = '|';
        }

        return "0x|"+new String(hexArray);
    }

}
