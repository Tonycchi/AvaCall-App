package com.example.model.connection;

public class EV3BluetoothHandshake implements ByteArrayHandshake {
    @Override
    public byte[][] getSyn() {
        byte[][] directCommand = new byte[1][8];

        directCommand[0][0] = (byte) (6);              //length
        directCommand[0][1] = 0x00;                    //length
        directCommand[0][2] = 0x00;                    //first message
        directCommand[0][3] = 0x00;                    //first message
        directCommand[0][4] = 0x00;                    // Direct command, reply required
        directCommand[0][5] = 0x00;                    //global variables
        directCommand[0][6] = 0x00;                    //global and local variables
        directCommand[0][7] = 0x01;                    //opcode

        return directCommand;
    }

    @Override
    public boolean isAckCorrect(byte[] ack) {
        return ack[2]==0x00 && ack[4]==0x02;
    }
}
