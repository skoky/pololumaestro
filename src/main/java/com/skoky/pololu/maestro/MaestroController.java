package com.skoky.pololu.maestro;
import com.skoky.pololu.maestro.port.PololuSerialPort;

import java.io.*;
import java.nio.ByteBuffer;

import static java.lang.Thread.sleep;

/**
 * Main class to call while controlling Pololu Maestro. Works with Pololu Maestro 12, 18 and 24.
 * Its probably not compatible with Maestro 6.
 * Created by skokys@gmail.com 16.5.2014.
 */
public class MaestroController {

    private static final int DEFAULT_SPEED = 40;
    private static final int DEFAULT_ACCELERATION = 20;
    private final boolean verbose;
    private int POS_MID = 1500;
    private int POS_MAX = 2300;
    private int POS_MIN = 600;

    public class Command {
        public static final byte SET_SPEED = (byte) 0x87;
        public static final byte SET_ACCELERATION = (byte) 0x89;
        public static final byte GET_POSITION = (byte) 0x90;
        public static final byte SET_POSITION = (byte) 0x84;
    }

    private final PololuSerialPort port;

    /**
     *  Pololu Maestro controller sets servo positions. The "USB Dual port" must be set in Pololu control center to work
     * @param portS  port name like COM23 or /dev/acm0 on linux. Use maestro's controller port
     * @param verbose log to system out or not
     * @throws Exception
     */
	public MaestroController(String portS, boolean verbose) throws Exception {
		this.verbose=verbose;
        port = new PololuSerialPort(portS);
        if (verbose) System.out.println("Port created:"+port);
		init();
	}

	private void init() {
		for (int i = 0; i < 6; i++) {
			setSpeed(i, DEFAULT_SPEED);
			setAcceleration(i, 20);
		}
        if (verbose) System.out.println("Init done");
	}

	private void setAcceleration(int servo, int acc) {
		try {
			port.out.write(new byte[] { Command.SET_ACCELERATION, (byte) servo, (byte) acc, 0 });
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void setSpeed(int servo, int speed) {
		try {
			port.out.write(new byte[] { Command.SET_SPEED, (byte) servo, (byte) speed, 0 });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
        try {
            port.out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


	public void setPosition(int servo, int pos, int speed, int acc) {
        if (verbose) System.out.println("Setting position "+ servo+"/"+pos+"/"+speed+"/"+acc);
		setSpeed(servo, speed);
		setAcceleration(servo, acc);
		setPositionOnly(servo, pos);
	}

    /**
     * sets servo position from 900-2000 - these are default limits and may be changed in the Pololu Maestro Control center
     * @param servo
     * @param pos
     */
	public void setPosition(int servo, int pos) {
        if (verbose) System.out.println("Setting position "+ servo+"/"+pos);
		setSpeed(servo, DEFAULT_SPEED);
		setAcceleration(servo, DEFAULT_ACCELERATION);
		setPositionOnly(servo, pos);
	}

    private void setPositionOnly(int servo, int pos) {
        if (verbose) logPosition(servo);
        int p = pos * 8;
        if (pos<POS_MIN || pos>POS_MAX)
            throw new IllegalStateException("Wrong position "+servo+"/"+p);

        ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(p);
        byte low = (byte) (bb.array()[3] & 0x7f);
        byte high = (byte) (bb.array()[2] & 0x7f);

		byte[] bbb = new byte[] { Command.SET_POSITION, (byte) servo, low, high };
		try {
			port.out.flush();
            port.out.write(bbb);
            port.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    private void logPosition(int servo) {
        byte[] bbb = new byte[] { Command.GET_POSITION, (byte) servo};
        try {
            port.out.flush();
            port.out.write(bbb);
            port.out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
