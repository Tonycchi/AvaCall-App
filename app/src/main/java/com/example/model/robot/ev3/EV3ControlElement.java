package com.example.model.robot.ev3;

import android.util.Log;

abstract class EV3ControlElement {

    final int maxPower;
    public final int[] port;

    protected EV3ControlElement(int[] ports, int maxPower) {
        this.maxPower = maxPower;
        this.port = ports;
    }

    /**
     *
     * @param value an int
     * @return int as byte array
     */
    static byte[] toByteArray(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value};
    }

    /**
     *
     * @param value param value
     * @return parameter for direct command as byte array
     */
    static byte[] LCX(int value) {
        byte[] r;
        byte[] t = toByteArray(value);
        if (-32767 <= value && value <= 32767) {
            r = new byte[]{
                    (byte) 0x82, t[2], t[3]
            };
        } else {
            r = new byte[]{
                    (byte) 0x83, t[0], t[1], t[2], t[3]
            };
        }
        return r;
    }

    /**
     * @param input controlling input
     * @return power for use in ev3 direct command
     */
    protected abstract byte[] getMotorPower(int... input);

    /**
     *
     * @param input controlling input
     * @return part of direct command
     */
    protected abstract byte[] getCommand(int... input);

    /**
     *
     * @param x value in [0,1], percentage of maxPower
     * @return scaled power
     */
    final byte scalePower(float x) {
        return (byte) (x * maxPower);
    }

    protected static class Joystick extends EV3ControlElement {

        /**
         *
         * @param ports 2 ports
         * @param maxPower
         */
        Joystick(int[] ports, int maxPower) {
            super(ports, maxPower);
        }

        @Override
        protected byte[] getMotorPower(int... input) {
            Log.d("Joystick", String.valueOf(input));
            float right = 0.0f;
            float left = 0.0f;

            int angle = input[0], strength = input[1];

            if (angle >= 0 &&
                    angle < 90) { //0°-89°
                right = -100 + angle * 20 / 9.0f; //-100 to 100
                left = 100; //100 to 100

            } else if (angle >= 90 &&
                    angle < 180) { //90°-179°
                right = 100; //100 to 100
                left = 100 - (angle - 90) * 20 / 9.0f; //100 to -100

            } else if (angle >= 180 &&
                    angle < 270) { //180°-269°
                right = 100 - (angle - 180) * 20 / 9.0f; //50 to -100
                left = -100; //-100 to -100

            } else if (angle >= 270 &&
                    angle <= 360) {//270°-359°
                right = -100; //-100 to -100
                left = -100 + (angle - 270) * 20 / 9.0f; //-100 to 100
            }

            byte[] r = new byte[2];

            r[0] = scalePower(right * strength / 10000);
            r[1] = scalePower(left * strength / 10000);

            return r;
        }

        @Override
        protected byte[] getCommand(int... input) {
            // A4|00|0p|81:po
            //  0  1  2  3  4
            // 0 opcode for output power
            // 1 filler
            // 2 p = port
            // 3 predefined prefix
            // 4 po = power

            byte[] power = getMotorPower(input);

            byte[] r = new byte[10];
            r[0] = (byte) 0xA4;
            r[1] = (byte) 0x00;
            r[2] = (byte) port[0];
            r[3] = (byte) 0x81;
            r[4] = power[0];
            r[5] = (byte) 0xA4;
            r[6] = (byte) 0x00;
            r[7] = (byte) port[1];
            r[8] = (byte) 0x81;
            r[9] = power[1];
            return r;
        }
    }

    protected static class Slider extends EV3ControlElement {

        /**
         *
         * @param ports 1 port
         * @param maxPower
         */
        Slider(int[] ports, int maxPower) {
            super(ports, maxPower);
        }

        @Override
        protected byte[] getMotorPower(int... input) {
            float tmp = (input[0] - 50) / 50.0f;
            return new byte[]{
                    scalePower(tmp)
            };
        }

        @Override
        protected byte[] getCommand(int... input) {
            Log.d("slider", input[0] + "");
            byte[] power = getMotorPower(input);

            byte[] r = new byte[5];
            r[0] = (byte) 0xA4;
            r[1] = (byte) 0x00;
            r[2] = (byte) port[0];
            r[3] = (byte) 0x81;
            r[4] = power[0];

            return r;
        }
    }

    protected static class Button extends EV3ControlElement {

        private long pressedT = -1;
        private int duration;
        private final byte[] t;

        /**
         *
         * @param ports 1 port
         * @param maxPower
         * @param duration in ms
         */
        Button(int[] ports, int maxPower, int duration) {
            super(ports, maxPower);
            this.duration = duration;
            t = LCX(duration - 100);
        }

        @Override
        protected byte[] getMotorPower(int... input) {
            return new byte[]{0};
        }

        @Override
        protected byte[] getCommand(int... input) {
            long current = System.currentTimeMillis();
            if (input[0] == 1 && (current - pressedT >= duration)) {
                pressedT = current;

                byte[] r = new byte[9 + t.length];

                r[0] = (byte) 0xAD;
                r[1] = (byte) 0x00;
                r[2] = (byte) port[0];
                r[3] = (byte) 0x81;
                r[4] = (byte) maxPower;
                r[5] = (byte) 0x81;
                r[6] = 100;
                System.arraycopy(t, 0, r, 7 , t.length);
                r[7 + t.length] = 0;
                r[8 + t.length] = 1;

                return r;
            }
            return new byte[]{
                    0x01 // nop
            };
        }
    }
}
