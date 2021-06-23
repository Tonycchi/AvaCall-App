package com.example.model.robot.ev3;

import android.util.Log;

abstract class EV3ControlElement {

    final int maxPower;
    final int[] port;

    protected EV3ControlElement(int[] ports, int maxPower) {
        this.maxPower = maxPower;
        this.port = ports;
    }

    /**
     * @param input controlling input
     * @return power for use in ev3 direct command
     */
    protected abstract byte[] getMotorPower(String input);

    protected static class Joystick extends EV3ControlElement {

        Joystick(int[] ports, int maxPower) {
            super(ports, maxPower);
        }

        @Override
        protected byte[] getMotorPower(String input) {
            Log.d("Joystick", input);
            float right = 0.0f;
            float left = 0.0f;

            String[] t = input.split(";");
            int angle = Integer.parseInt(t[0]), strength = Integer.parseInt(t[1]);

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
    }


    protected static class Slider extends EV3ControlElement {

        Slider(int[] ports, int maxPower) {
            super(ports, maxPower);
        }

        @Override
        protected byte[] getMotorPower(String input) {
            return new byte[]{0}; //TODO implement
        }
    }

    protected static class Button extends EV3ControlElement {

        Button(int[] ports, int maxPower) {
            super(ports, maxPower);
        }

        @Override
        protected byte[] getMotorPower(String input) {
            return new byte[]{0}; //TODO implement
        }
    }

    final byte scalePower(float x) {
        return (byte) (x * maxPower);
    }
}
