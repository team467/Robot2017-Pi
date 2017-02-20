/**
 *
 */
package org.usfirst.frc.team467.pi.sensors;

import com.nainara.lsm9ds1.Driver;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public class PiGyro implements Runnable {

	private NetworkTable table;
	private Driver driver;

	/**
	 *
	 */
	public PiGyro() {
		table = RioConnection.getInstance();
		driver = new Driver();
		driver.setDatarate(Driver.DataRate.FREQ_476_HZ);
		driver.setAccelerometerScale(Driver.AccelerometerScale.SCALE_PLUS_MINUS_2G);
	    driver.setUseFifoBuffer(false);
	    driver.initialize();
	}

	public void poll() {
        driver.pollIMU();
        driver.pollMagnetometer();
		table.putNumber("X-Axis Accelleration", driver.getAccX());
		table.putNumber("Y-Axis Accelleration", driver.getAccY());
		table.putNumber("Z-Axis Accelleration", driver.getAccZ());
		table.putNumber("X-Axis Angle", driver.getGyrPitchX());
		table.putNumber("Y-Axis Angle", driver.getGyrRollY());
		table.putNumber("Z-Axis Angle", driver.getGyrYawZ());
		table.putNumber("X-Axis Magnetometer", driver.getMagX());
		table.putNumber("Y-Axis Magnetometer", driver.getMagY());
		table.putNumber("Z-Axis Magnetometer", driver.getMagZ());
	}

	public void checkReset() {
	}

	@Override
	public void run() {
		while (true) {
			try {
				poll();
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
