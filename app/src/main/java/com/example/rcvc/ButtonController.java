package com.example.rcvc;

public class ButtonController {
    //TODO use DirectCommander

    public static final int STOP = 0;
    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;
    public static final int TURN_RIGHT = 3;
    public static final int TURN_LEFT = 4;

    //start and end part of direct commands used to control EV3
    private final String startDirCom = "0D002A00800000A4000";
    private final String endDirCom = "A6000";

    //power that is used to control the ev3 coded in hex
    private final String plus_50 = "8132";
    private final String minus_50 = "81CE";

    //ports that are used to control the ev3 coded in hex
    private final String port_AD = "9";
    private final String port_A = "1";
    private final String port_D = "8";

    //complete direct commands used to control the ev3 consisting of :
    //start + port + power + end + port
    private final String directCommandForward = startDirCom + port_AD + plus_50 + endDirCom + port_AD;
    private final String directCommandBackward = startDirCom + port_AD + minus_50 + endDirCom + port_AD;
    private final String directCommandRightPortB = startDirCom + port_A + minus_50 + endDirCom + port_A;
    private final String directCommandRightPortC = startDirCom + port_D + plus_50 + endDirCom + port_D;
    private final String directCommandLeftPortB = startDirCom + port_A + plus_50 + endDirCom + port_A;
    private final String directCommandLeftPortC = startDirCom + port_D + minus_50 + endDirCom + port_D;
    private final String directCommandStop = "09002A00000000A3000F00";

    private final BluetoothConnectionService b;

    public ButtonController(BluetoothConnectionService b) {
        this.b = b;
    }

//    public void sendCommands(int command) {
//        switch (command) {
//            case STOP:
//                b.write(hexStringToByteArray(directCommandStop));
//                break;
//            case FORWARD:
//                b.write(hexStringToByteArray(directCommandForward));
//                break;
//            case BACKWARD:
//                b.write(hexStringToByteArray(directCommandBackward));
//                break;
//            case TURN_RIGHT:
//                b.write(hexStringToByteArray(directCommandRightPortB));
//                b.write(hexStringToByteArray(directCommandRightPortC));
//                break;
//            case TURN_LEFT:
//                b.write(hexStringToByteArray(directCommandLeftPortB));
//                b.write(hexStringToByteArray(directCommandLeftPortC));
//                break;
//            default:
//        }
//    }

    public void sendCommands(int command) {
        switch (command) {
            case STOP:
                DirectCommander.send(0.0f, 0.0f, b);
                break;
            case FORWARD:
                DirectCommander.send(1.0f, 1.0f, b);
                break;
            case BACKWARD:
                DirectCommander.send(-1.0f, -1.0f, b);
                break;
            case TURN_RIGHT:
                DirectCommander.send(-1.0f, 1.0f, b);
                break;
            case TURN_LEFT:
                DirectCommander.send(1.0f, -1.0f, b);
                break;
            default:
        }
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