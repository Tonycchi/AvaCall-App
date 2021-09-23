package com.example.model.robot.ev3;

import android.util.Log;

import com.example.data.RobotModel;
import com.example.model.robot.Controller;
import com.example.model.robotConnection.ConnectionService;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An implementation of {@code Controller} for a LEGO MINDSTORMS EV3.
 */
public class EV3Controller implements Controller {

    private final String TAG = "EV3Controller";

    private final RobotModel model;
    private final int[] ids = new int[4];
    public ConnectionService service;
    private ArrayList<EV3ControlElement> controlElements;
    private String controlElementString = "";
    private int lastUsedId;
    private boolean inputWebClient;

    public EV3Controller(RobotModel model, ConnectionService service) {
        this.service = service;

        this.model = model;
        this.inputWebClient = false;

        if (model != null)
            createElements(model.specs);
    }

    public ArrayList<EV3ControlElement> getControlElements() {
        return controlElements;
    }

    public int getLastUsedId() {
        return lastUsedId;
    }

    public void setLastUsedId(int id) {
        lastUsedId = id;
    }

    @Override
    public void sendInput(int... input) {
        Log.d(TAG, Arrays.toString(input));
        // command to move the mindstorm
        byte[] inputCommand = createCommand(input);
        // command to get current motor strength
        byte[] outputCommand = createStallCommand(input);
        service.write(inputCommand);
        try {
            Log.d(TAG, "before sleep");
            Thread.sleep(50);
            Log.d(TAG, "after sleep");
            service.write(outputCommand);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getOutput() {
        service.write(createOutputCommand());
    }

    public String getControlElementString() {
        return controlElementString;
    }

    public RobotModel getCurrentModel() {
        return model;
    }

    public boolean getInputFromWebClient() {
        return inputWebClient;
    }

    public void setInputFromWebClient(boolean input) {
        inputWebClient = input;
    }

    /**
     * creates EV3ControlElement objects according to specifications
     *
     * @param specs string specifying control element to port mapping
     */
    private void createElements(String specs) {
        /*
        we get:
        -$controlElement$|$controlElement$|...
         */
        controlElements = new ArrayList<>();

        // split into $controlElement$ = $element$:$attributes$
        String[] tmp = specs.split("\\|");
        // put into list with [0] = $element$, [1] = $attributes$
        ArrayList<String[]> list = new ArrayList<>();
        for (String t : tmp) {
            list.add(t.split(":"));
        }
        // now translate each $attributes$ into corresponding Objects:
        for (String[] k : list) {
            String[] attrs = k[1].split(";");
            int maxPower = Integer.parseInt(attrs[0]);
            int[] ports;
            switch (k[0]) {
                case "joystick":
                    // $maxPower$;$right$,$left$
                    String[] portsString = attrs[1].split(",");
                    ports = new int[2];
                    ports[0] = Integer.parseInt(portsString[0]);
                    ports[1] = Integer.parseInt(portsString[1]);
                    controlElements.add(new EV3ControlElement.Joystick(ports, maxPower));
                    addToString("joystick");
                    break;
                case "slider":
                    // $maxPower$;$port$
                    ports = new int[1];
                    ports[0] = Integer.parseInt(attrs[1]);
                    controlElements.add(new EV3ControlElement.Slider(ports, maxPower));
                    addToString("slider");
                    break;
                case "button":
                    // $maxPower$;$port$;$duration$
                    ports = new int[1];
                    ports[0] = Integer.parseInt(attrs[1]);
                    int dur = Integer.parseInt(attrs[2]);
                    controlElements.add(new EV3ControlElement.Button(ports, maxPower, dur));
                    addToString("button");
                    break;
                default:
            }
        }

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < controlElements.size(); i++) {
            EV3ControlElement e = controlElements.get(i);
            s.append(i).append(": ");
            s.append(e.getClass().getName()).append(" ports: ");
            s.append(Arrays.toString(e.port)).append("\n");
        }
        Log.d(TAG, s.toString());

    }

    /**
     * @param input [id, value, ...]
     * @return direct command for EV3
     */
    private byte[] createCommand(int... input) {
        //0x|14:00|2A:00|80|00:00|A4|00|0p|81:po|...|A6|00|0P
        //   0  1  2  3  4  5  6  7  8  9  10 11
        // 0 length of command minus 2
        // 2-6 predefined
        // 7-11 command for one motor
        // last 3 bytes: A6 opcode for start output
        //               00 filler
        //               0P = sum of used ports

        int id = input[0];  // get id of input
        EV3ControlElement e = controlElements.get(id);  // for this
        Log.d("TAG", e.getClass().toString());
        byte[] output = e.getCommand(Arrays.copyOfRange(input, 1, input.length));

        int length = 10 + output.length;            // e.g. joystick may return two values
        int lastCommand = 7 + output.length;
        byte[] directCommand = new byte[length];        // this will be the command

        directCommand[0] = (byte) (length - 2);         // pre defined parts of direct command

        if (e.port.length == 1) {
            // directly write the port into the message counter
            directCommand[2] = Byte.parseByte(Integer.toHexString(e.port[0]), 16);
        } else {
            Log.d(TAG, "Port 1: " + e.port[0] + " Port 2: " + e.port[1]);
            // combine the ports into a single byte and write it into the message counter
            directCommand[2] = Byte.parseByte(Integer.toHexString((e.port[0] << 4) + e.port[1]), 16);
        }

        directCommand[3] = (byte) id;              //message counter is used as info which control element is writing

        directCommand[4] = (byte) 0x80;
        directCommand[5] = (byte) 0x04;

        int commandPos = 7;                                      // position of first output power command
        byte portSum = 0;
        System.arraycopy(output, 0, directCommand, commandPos, output.length);
        for (int p : e.port)
            portSum += p;

        directCommand[lastCommand] = (byte) 0xa6;                // end of direct command
        directCommand[lastCommand + 2] = portSum;
        return directCommand;
    }

    /**
     * creates direct command requesting motor powers
     * @return direct command
     */
    private byte[] createOutputCommand() {
        // 0x|25:00|01:23|00|04:00|99:1C|00|pp:08|02:01|gm1...
        //    0  1  2  3  4  5  6  7  8  9  10 11 12 13 14
        // 0 length of command minus 2
        // 2-3 ids of the control elements for port AB:CD
        // 5 global memory size 4 for port A:B:C:D
        // 7-14 command for output of one motor (see commandPart)
        for (int i = 0; i < controlElements.size(); i++) {
            switch (controlElements.get(i).port[0]) {
                case 1:
                    ids[0] = i;
                    break;
                case 2:
                    ids[1] = i;
                    break;
                case 4:
                    ids[2] = i;
                    break;
                case 8:
                    ids[3] = i;
                    break;
                default:
            }
            if ((controlElements.get(i).port.length) > 1) {
                switch (controlElements.get(i).port[1]) {
                    case 1:
                        ids[0] = i;
                        break;
                    case 2:
                        ids[1] = i;
                        break;
                    case 4:
                        ids[2] = i;
                        break;
                    case 8:
                        ids[3] = i;
                        break;
                    default:
                }
            }
        }

        byte[] directCommand = new byte[39];
        byte[] tmp;
        directCommand[0] = (byte) 0x25;
        directCommand[2] = Byte.parseByte(Integer.toHexString((ids[0] << 4) + ids[1]), 16);
        directCommand[3] = Byte.parseByte(Integer.toHexString((ids[2] << 4) + ids[3]), 16);
        directCommand[5] = (byte) 0x04;

        tmp = commandPart((byte) 0x10, (byte) 0x60);
        System.arraycopy(tmp, 0, directCommand, 7, 8);
        tmp = commandPart((byte) 0x11, (byte) 0x61);
        System.arraycopy(tmp, 0, directCommand, 15, 8);
        tmp = commandPart((byte) 0x12, (byte) 0x62);
        System.arraycopy(tmp, 0, directCommand, 23, 8);
        tmp = commandPart((byte) 0x13, (byte) 0x63);
        System.arraycopy(tmp, 0, directCommand, 31, 8);
        return directCommand;
    }

    /**
     * @param input ports
     * @return command
     */
    private byte[] createStallCommand(int... input) {
        byte[] tmp;

        int[] inp = Arrays.copyOfRange(input, 1, input.length);

        EV3ControlElement controlElement = controlElements.get(input[0]);
        byte[] directCommand = controlElement.port.length > 1 ? new byte[23] : new byte[15];
        directCommand[0] = controlElement.port.length > 1 ? (byte) 0x15 : (byte) 0x0D;
        byte[] motorPower = controlElement.getMotorPower(inp);
        directCommand[2] = controlElement.getMotorPower(inp)[0];
        directCommand[3] = motorPower.length > 1 ? motorPower[1] : (byte) 0x00;
        directCommand[5] = (byte) 0x02;

        int port1 = controlElement.port[0];
        tmp = commandPart(intToBytePort(port1), (byte) 0x60);
        System.arraycopy(tmp, 0, directCommand, 7, 8);
        if (controlElement.port.length > 1) {
            int port2 = controlElement.port[1];
            tmp = commandPart(intToBytePort(port2), (byte) 0x61);
            System.arraycopy(tmp, 0, directCommand, 15, 8);
        }

        return directCommand;
    }

    /**
     * @param port    ev3 motor port
     * @param counter offset of global memory
     * @return part of direct command for given port
     */
    private byte[] commandPart(byte port, byte counter) {
        byte[] r = new byte[8];
        r[0] = (byte) 0x99;
        r[1] = (byte) 0x1C;
        r[3] = port;
        r[4] = (byte) 0x08;
        r[5] = (byte) 0x02;
        r[6] = (byte) 0x01;
        r[7] = counter;
        return r;
    }

    /**
     * @param element string to be added to controlElementString
     */
    private void addToString(String element) {
        if (!controlElementString.equals(""))
            controlElementString += "|";
        controlElementString += element;
    }

    /**
     *
     * @param port ev3 port number
     * @return corresponding direct command port variable
     */
    private byte intToBytePort(int port) {
        switch (port) {
            case 1:
                return 0x10;
            case 2:
                return 0x11;
            case 4:
                return 0x12;
            case 8:
                return 0x13;
            default:
                return 0x00;
        }
    }
}
