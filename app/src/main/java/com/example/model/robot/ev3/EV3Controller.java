package com.example.model.robot.ev3;

import android.util.Log;

import com.example.data.RobotModel;
import com.example.model.connection.ConnectionService;
import com.example.model.robot.Controller;
import com.example.model.robot.ev3.EV3ControlElement;

import java.util.ArrayList;
import java.util.Arrays;

public class EV3Controller implements Controller {

    private final String TAG = "EV3Controller";
    public ConnectionService service;
    private RobotModel model;
    private ArrayList<EV3ControlElement> controlElements;
    private String controlElementString = "";

    public EV3Controller(RobotModel model, ConnectionService service) {
        this.service = service;

        this.model = model;

        if (model != null)
            createElements(model.specs);
    }

    @Override
    public void sendInput(int... input) {
        Log.d(TAG, Arrays.toString(input));
        service.write(createCommand(input));
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        service.write(createOutputCommand());
    }

    public String getControlElementString() {
        return controlElementString;
    }

    public RobotModel getCurrentModel() {
        return model;
    }

    /**
     * creates EV3ControlElement objects according to specifications
     *
     * @param specs string specifying control element to port mapping
     */
    private void createElements(String specs) {
        /* TODO document outside of code
        we get:
        -$controlElement$|$controlElement$|...
         */
        controlElements = new ArrayList<>();

        // split into $controlElement$ = $element$:$attributes$
        String[] tmp = specs.split("\\|");
        // put into list with [0] = $element$, [1] = $attributes$
        ArrayList<String[]> list = new ArrayList<>();
        for (String t : tmp) {
            String[] a = t.split(":");
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

        String s = "";
        for (int i = 0; i < controlElements.size(); i++) {
            EV3ControlElement e = controlElements.get(i);
            s += i + ": ";
            s += e.getClass().getName() + " ports: ";
            s += Arrays.toString(e.port) + "\n";
        }
        Log.d(TAG, s);

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
        // 7-11 command for one motor (see commandPart)
        // last 3 bytes: A6 opcode for start output
        //               00 filler
        //               0P = sum of used ports

        int id = input[0];  // get id of input
        EV3ControlElement e = controlElements.get(id);  // for this
        //byte[] output = e.getMotorPower(Arrays.copyOfRange(input, 1, input.length));  // and compute output power
        byte[] output = e.getCommand(Arrays.copyOfRange(input, 1, input.length));

        int length = 10 + output.length + 16;            // e.g. joystick may return two values
        int lastCommand = 7 + output.length;
        byte[] directCommand = new byte[length];        // this will be the command

        directCommand[0] = (byte) (length - 2);         // pre defined parts of direct command

        //TODO:
        //directCommand[2] = port;  //message counter is used as info which port is used
        if (e.port.length == 1) {
            // directly write the port into the message counter
            directCommand[2] = Byte.parseByte(Integer.toHexString(e.port[0]), 16);
        }
        else {
            Log.d(TAG, "Port 1: "+e.port[0]+" Port 2: "+e.port[1]);
            // combine the ports into a single byte and write it into the message counter
            directCommand[2] = Byte.parseByte(Integer.toHexString((e.port[0]<<4)+e.port[1]), 16);
        }

        directCommand[3] = (byte) id;              //message counter is used as info which control element is writing

        directCommand[4] = (byte) 0x00;
        directCommand[5] = (byte) 0x04;

        int commandPos = 7;                                      // position of first output power command
        byte[] t;
        byte portSum = 0;
        System.arraycopy(output, 0, directCommand, commandPos, output.length);
        /*
        for (int k = 0; k < output.length; k++) {
            portSum += e.port[k];
            t = commandPart(e.port[k], output[k]);      // get output power command & copy 2 direct command
            System.arraycopy(t, 0, directCommand, i, t.length);
            i += t.length;
        }
         */
        for (int p : e.port)
            portSum += p;

        directCommand[lastCommand] = (byte) 0xa6;                // end of direct command
        directCommand[lastCommand + 2] = portSum;

//        byte[] timer = new byte[7]; // 1 + 3 + 1 | 1 + 1
//        timer[0] = (byte) 0x85; //opcode timer_wait
//        timer[1] = (byte) 0x82; //LC2
//        timer[2] = (byte) 0x64; //LC2 Value
//        timer[4] = (byte) 0x40; //LV(0)
//
//        timer[5] = (byte) 0x86; //opcoode timer_read
//        timer[6] = (byte) 0x40; //LV(0)
//        System.arraycopy(timer, 0, directCommand, lastCommand + 3, 7);

        // directCommand to read output of motors
        byte[] changeMode = new byte[16];
        changeMode[0] = (byte) 0x99;    //opcode
        changeMode[1] = (byte) 0x1C;    //opcode extension
        changeMode[2] = (byte) 0x00;    //layer
        changeMode[3] = (byte) 0x10;    //port
        changeMode[4] = (byte) 0x07;    //type
        changeMode[5] = (byte) 0x02;    //typemode
        changeMode[6] = (byte) 0x01;    //values
        changeMode[7] = (byte) 0x60;    //global memory address

        changeMode[8] = (byte) 0x99;    //opcode
        changeMode[9] = (byte) 0x1C;    //opcode extension
        changeMode[10] = (byte) 0x00;   //layer
        changeMode[11] = (byte) 0x13;   //port
        changeMode[12] = (byte) 0x07;   //type
        changeMode[13] = (byte) 0x02;   //typemode
        changeMode[14] = (byte) 0x01;   //values
        changeMode[15] = (byte) 0x61;   //global memory address
        System.arraycopy(changeMode,0, directCommand, lastCommand + 3, 16);
        return directCommand;
    }

    private byte[] createOutputCommand() {
        //0x|14:00|2A:00|80|00:00|A4|00|0p|81:po|...|A6|00|0P
        //   0  1  2  3  4  5  6  7  8  9  10 11
        // 0 length of command minus 2
        // 2-6 predefined
        // 7-11 command for one motor (see commandPart)
        // last 3 bytes: A6 opcode for start output
        //               00 filler
        //               0P = sum of used ports

        byte[] directCommand = new byte[15];
        directCommand[0] = (byte) 0x0D;
        directCommand[2] = (byte) 0x2A;
        directCommand[5] = (byte) 0x04;
        directCommand[7] = (byte) 0x99;             //opcode
        directCommand[8] = (byte) 0x1C;
        directCommand[9] = (byte) 0x00;
        directCommand[10] = (byte) 0x10;            //port
        directCommand[11] = (byte) 0x08;
        directCommand[12] = (byte) 0x02;           //typemode
        directCommand[13] = (byte) 0x01;
        directCommand[14] = (byte) 0x60;

        return directCommand;
    }



    /**
     * @param port  ev3 motor port
     * @param power evr motor power
     * @return part of direct command for given port
     */
    private byte[] commandPart(int port, int power) {
        // A4|00|0p|81:po
        //  0  1  2  3  4
        // 0 opcode for output power
        // 1 filler
        // 2 p = port
        // 3 predefined prefix
        // 4 po = power

        byte[] r = new byte[5];
        r[0] = (byte) 0xA4;
        r[1] = (byte) 0x00;
        r[2] = (byte) port;
        r[3] = (byte) 0x81;
        r[4] = (byte) power;
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
}
