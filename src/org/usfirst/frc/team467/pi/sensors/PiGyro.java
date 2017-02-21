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
//	private Driver driver;
	LSM9DS1_IMU imu;

	/**
	 *
	 */
	public PiGyro() {
		table = RioConnection.getInstance();
//		driver = new Driver();
//		driver.setDatarate(Driver.DataRate.FREQ_476_HZ);
//		driver.setAccelerometerScale(Driver.AccelerometerScale.SCALE_PLUS_MINUS_2G);
//	    driver.setUseFifoBuffer(false);
//	    driver.initialize();
	    imu = new LSM9DS1_IMU();
	}

	public void poll() {
//        driver.pollIMU();
//        driver.pollMagnetometer();
//		table.putNumber("X-Axis Accelleration", driver.getAccX());
//		table.putNumber("Y-Axis Accelleration", driver.getAccY());
//		table.putNumber("Z-Axis Accelleration", driver.getAccZ());
//		table.putNumber("X-Axis Angle", driver.getGyrPitchX());
//		table.putNumber("Y-Axis Angle", driver.getGyrRollY());
//		table.putNumber("Z-Axis Angle", driver.getGyrYawZ());
//		table.putNumber("X-Axis Magnetometer", driver.getMagX());
//		table.putNumber("Y-Axis Magnetometer", driver.getMagY());
//		table.putNumber("Z-Axis Magnetometer", driver.getMagZ());
		table.putNumber("X-Axis Accelleration", imu.getAccelX());
		table.putNumber("Y-Axis Accelleration", imu.getAccelY());
		table.putNumber("Z-Axis Accelleration", imu.getAccelZ());
		table.putNumber("X-Axis Angle", imu.getAngleX());
		table.putNumber("Y-Axis Angle", imu.getAngleY());
		table.putNumber("Z-Axis Angle", imu.getAngleY());
		table.putNumber("X-Axis Magnetometer", imu.getMagX());
		table.putNumber("Y-Axis Magnetometer", imu.getMagY());
		table.putNumber("Z-Axis Magnetometer", imu.getMagZ());
		table.putNumber("Temperature", imu.getTemperature());
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
