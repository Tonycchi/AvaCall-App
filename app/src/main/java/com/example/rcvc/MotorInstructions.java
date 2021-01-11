package com.example.rcvc;

import java.security.InvalidParameterException;

public class MotorInstructions {

    public byte[] powers;

    public MotorInstructions(byte[] powers) throws InvalidParameterException {
        /* TODO check that input matches accessible power range for ev3 */
        if (powers.length > 4) throw new InvalidParameterException();
        this.powers = powers;
    }

}
