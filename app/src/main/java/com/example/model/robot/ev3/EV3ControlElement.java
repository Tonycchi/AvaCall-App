package com.example.model.robot.ev3;

import com.example.model.robot.ControllerInput;

abstract class EV3ControlElement {

    final int MAX_POWER;
    final int port;

    protected EV3ControlElement(int port, int maxPower) {
        this.MAX_POWER = maxPower;
        this.port = port;
    }

    protected abstract byte getMotorPower(ControllerInput in);

    protected static class JoystickRight extends EV3ControlElement {

        JoystickRight(int port, int maxPower) {
            super(port, maxPower);
        }

        @Override
        public byte getMotorPower(ControllerInput in) {
            float right = 0.0f;

            if (in.angle >= 0 && in.angle < 90) { //0°-89°
                right = -100 + in.angle * 20 / 9.0f; //-100 to 100

            } else if (in.angle >= 90 && in.angle < 180) { //90°-179°
                right = 100; //100 to 100

            } else if (in.angle >= 180 && in.angle < 270) { //180°-269°
                right = 100 - (in.angle - 180) * 20 / 9.0f; //50 to -100

            } else if (in.angle >= 270 && in.angle <= 360) {//270°-359°
                right = -100; //-100 to -100
            }

            right = right * in.strength / 10000;

            return (byte) (right * MAX_POWER);
        }
    }

    protected static class JoystickLeft extends EV3ControlElement {

        JoystickLeft(int port, int maxPower) {
            super(port, maxPower);
        }

        @Override
        public byte getMotorPower(ControllerInput in) {
            float left = 0.0f;

            if (in.angle >= 0 && in.angle < 90) { //0°-89°
                left = 100; //100 to 100

            } else if (in.angle >= 90 && in.angle < 180) { //90°-179°
                left = 100 - (in.angle - 90) * 20 / 9.0f; //100 to -100

            } else if (in.angle >= 180 && in.angle < 270) { //180°-269°
                left = -100; //-100 to -100

            } else if (in.angle >= 270 && in.angle <= 360) {//270°-359°
                left = -100 + (in.angle - 270) * 20 / 9.0f; //-100 to 100
            }

            left = left * in.strength / 10000;

            return (byte) (left * MAX_POWER);
        }
    }

    protected static class Slider extends EV3ControlElement {

        Slider(int port, int maxPower) {
            super(port, maxPower);
        }

        @Override
        public byte getMotorPower(ControllerInput in) {
            return (byte) 0; //TODO implement
        }
    }

    protected static class Button extends EV3ControlElement {

        Button(int port, int maxPower) {
            super(port, maxPower);
        }

        @Override
        public byte getMotorPower(ControllerInput in) {
            return (byte) 0; //TODO implement
        }
    }
}
