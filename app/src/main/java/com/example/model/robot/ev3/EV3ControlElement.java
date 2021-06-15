package com.example.model.robot.ev3;

import com.example.model.robot.ControllerInput;

abstract class EV3ControlElement {

    final int maxPower;
    final int port;

    protected EV3ControlElement(int port, int maxPower) {
        this.maxPower = maxPower;
        this.port = port;
    }

    /**
     * @param input controlling input
     * @return power for use in ev3 direct command
     */
    protected abstract byte getMotorPower(ControllerInput input);

    protected static class JoystickRight extends EV3ControlElement {

        JoystickRight(int port, int maxPower) {
            super(port, maxPower);
        }

        @Override
        protected byte getMotorPower(ControllerInput input) {
            float right = 0.0f;

            if (input.angle >= 0 && input.angle < 90) { //0°-89°
                right = -100 + input.angle * 20 / 9.0f; //-100 to 100

            } else if (input.angle >= 90 && input.angle < 180) { //90°-179°
                right = 100; //100 to 100

            } else if (input.angle >= 180 && input.angle < 270) { //180°-269°
                right = 100 - (input.angle - 180) * 20 / 9.0f; //50 to -100

            } else if (input.angle >= 270 && input.angle <= 360) {//270°-359°
                right = -100; //-100 to -100
            }

            right = right * input.strength / 10000;

            return (byte) (right * maxPower);
        }
    }

    protected static class JoystickLeft extends EV3ControlElement {

        JoystickLeft(int port, int maxPower) {
            super(port, maxPower);
        }

        @Override
        protected byte getMotorPower(ControllerInput input) {
            float left = 0.0f;

            if (input.angle >= 0 && input.angle < 90) { //0°-89°
                left = 100; //100 to 100

            } else if (input.angle >= 90 && input.angle < 180) { //90°-179°
                left = 100 - (input.angle - 90) * 20 / 9.0f; //100 to -100

            } else if (input.angle >= 180 && input.angle < 270) { //180°-269°
                left = -100; //-100 to -100

            } else if (input.angle >= 270 && input.angle <= 360) {//270°-359°
                left = -100 + (input.angle - 270) * 20 / 9.0f; //-100 to 100
            }

            left = left * input.strength / 10000;

            return (byte) (left * maxPower);
        }
    }

    protected static class Slider extends EV3ControlElement {

        Slider(int port, int maxPower) {
            super(port, maxPower);
        }

        @Override
        protected byte getMotorPower(ControllerInput input) {
            return (byte) 0; //TODO implement
        }
    }

    protected static class Button extends EV3ControlElement {

        Button(int port, int maxPower) {
            super(port, maxPower);
        }

        @Override
        protected byte getMotorPower(ControllerInput input) {
            return (byte) 0; //TODO implement
        }
    }
}
