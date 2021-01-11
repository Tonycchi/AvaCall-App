package com.example.rcvc;

import java.security.InvalidParameterException;

public class MotorInstruction {

    /*
    *  0 1 2 3 array indices
    *  A B C D motor ports
    *  */
    public byte[] powers;

    public MotorInstruction(byte[] powers) throws InvalidParameterException {
        /* TODO check that input matches accessible power range for ev3 */
        if (powers.length > 4) throw new InvalidParameterException();
        this.powers = powers;
    }

}
