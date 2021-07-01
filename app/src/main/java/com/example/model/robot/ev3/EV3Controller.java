package com.example.model.robot.ev3;

import android.util.Log;

import com.example.model.connection.ConnectionService;
import com.example.model.robot.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EV3Controller implements Controller {

    private final String TAG = "EV3Controller";

    public String s;

    public ConnectionService service;
    private ArrayList<EV3ControlElement> controlElements;
    //private List<Integer> controlCounts;
    private HashMap<String, Integer> controlCounts;

    public EV3Controller(String specs, ConnectionService service) {
        this.service = service;

        this.s = specs;

        this.controlCounts = new HashMap<>();

        Log.d(TAG, specs);
        createElements(specs);
    }

    @Override
    public void sendInput(int... input) {
        service.write(createCommand(input));
    }

    @Override
    public Map<String, Integer> getControlCounts() {
        return controlCounts;
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
        // put into map with key = $element$, value = $attributes$
        HashMap<String, String> elements = new HashMap<>();
        ArrayList<String> keys = new ArrayList<>();
        for (String t : tmp) {
            String[] a = t.split(":");
            elements.put(a[0], a[1]);
            keys.add(a[0]);
        }
        // now translate each $attributes$ into corresponding Objects:
        for (String k : keys) {
            String[] attrs = elements.get(k).split(";");
            int maxPower = Integer.parseInt(attrs[0]);
            int[] ports;
            switch (k) {
                case "joystick":
                    // $maxPower$;$right$,$left$
                    String[] portsString = attrs[1].split(",");
                    ports = new int[2];
                    ports[0] = Integer.parseInt(portsString[0]);
                    ports[1] = Integer.parseInt(portsString[1]);
                    controlElements.add(new EV3ControlElement.Joystick(ports, maxPower));
                    incrCounts("joystick");
                    break;
                case "slider":
                    // $maxPower$;$port$
                    ports = new int[1];
                    ports[0] = Integer.parseInt(attrs[1]);
                    controlElements.add(new EV3ControlElement.Slider(ports, maxPower));
                    incrCounts("slider");
                    break;
                case "button":
                    // $maxPower$;$port$;$duration$
                    ports = new int[1];
                    ports[0] = Integer.parseInt(attrs[1]);
                    controlElements.add(new EV3ControlElement.Button(ports, maxPower));
                    incrCounts("button");
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

        int id = input[0];              // get id of input
        EV3ControlElement e = controlElements.get(id);  // for this
        byte[] output = e.getMotorPower(Arrays.copyOfRange(input, 1, input.length));         // and compute output power

        int length = 10 + output.length * 5;            // e.g. joystick may return two values
        int lastCommand = 8 + output.length * 5;
        byte[] directCommand = new byte[length];        // this will be the command

        directCommand[0] = (byte) (length - 2);         // pre defined parts of direct command
        directCommand[2] = 0x2a;
        directCommand[4] = (byte) 0x80;

        int i = 7;                                      // position of first output power command
        byte[] t;
        byte portSum = 0;
        for (int k = 0; k < output.length; k++) {
            portSum += e.port[k];
            t = commandPart(e.port[k], output[k]);      // get output power command & copy 2 direct command
            System.arraycopy(t, 0, directCommand, i, t.length);
            i += t.length;
        }

        directCommand[lastCommand] = (byte) 0xa6;                // end of direct command
        directCommand[lastCommand + 1] = portSum;

        /*
        byte length = 20;
        byte[] directCommand = new byte[length];

        directCommand[0] = (byte) (length - 2);
        directCommand[2] = 0x2a;
        directCommand[4] = (byte) 0x80;
        directCommand[7] = (byte) 0xa4;
        directCommand[10] = (byte) 0x81;
        directCommand[12] = (byte) 0xa4;
        directCommand[15] = (byte) 0x81;
        directCommand[17] = (byte) 0xa6;
        directCommand[19] = (byte) 0x0f; // TODO

        directCommand[9] = PORT_RIGHT;    // PORT right motor
        directCommand[11] = rightPower;  // POWER right motor

        directCommand[14] = PORT_LEFT;   // PORT left motor
        directCommand[16] = leftPower;   // POWER left motor
         */
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

    private void incrCounts(String name) {
        if (controlCounts.containsKey(name)) {
            controlCounts.put(name, controlCounts.get(name) + 1);
        } else {
            controlCounts.put(name, 1);
        }
    }
}
