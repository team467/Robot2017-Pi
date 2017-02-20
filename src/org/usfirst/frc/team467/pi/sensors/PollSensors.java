/**
 *
 */
package org.usfirst.frc.team467.pi.sensors;

import java.io.IOException;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 *
 */
public class PollSensors extends Thread {
	public static void main(String args[]) throws InterruptedException, UnsupportedBusNumberException, IOException {
		System.out.println("Starting sensor polling");

		AnalogPins gpio = AnalogPins.GEAR_LIFTER;
//		gpio.addConsolePrintTableListener();
		gpio.addNetworkTableListener();

		(new Thread(new PiGyro())).start();

		// keep program running for 10 minutes
//		Thread.sleep(60000000);

		while(true) {
			Thread.sleep(120000);
		}


//		System.out.println("Exiting sensor polling");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}
