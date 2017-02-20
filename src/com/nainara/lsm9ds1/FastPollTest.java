/**
 *
 */
package com.nainara.lsm9ds1;

import java.util.concurrent.TimeUnit;

import org.usfirst.frc.team467.pi.sensors.RioConnection;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public class FastPollTest {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress(RioConnection.NETWORK_TABLE_SERVER_NAME_OR_IP);
		NetworkTable table = NetworkTable.getTable("imu");

		Driver driver = new Driver();
		driver.setDatarate(Driver.DataRate.FREQ_476_HZ);
		driver.setAccelerometerScale(Driver.AccelerometerScale.SCALE_PLUS_MINUS_2G);
	    driver.setUseFifoBuffer(false);
	    driver.initialize();

	    long start, duration = 0, iterations = 2000;
	    try{
	        System.out.print("Waking " + iterations + " times at 5 millisecond intervals");
	        for(int interval=0; interval < iterations; interval++){
	            start = System.nanoTime();
	            driver.pollIMU();
	            driver.pollMagnetometer();
				table.putNumber("X-Axis Accelleration", driver.getAccX());
				table.putNumber("Y-Axis Accelleration", driver.getAccY());
				table.putNumber("Z-Axis Accelleration", driver.getAccZ());
				table.putNumber("Pitch X", driver.getGyrPitchX());
				table.putNumber("Roll Y", driver.getGyrRollY());
				table.putNumber("Yaw Z", driver.getGyrYawZ());
				table.putNumber("Magnetometer X", driver.getMagX());
				table.putNumber("Magnetometer Y", driver.getMagY());
				table.putNumber("Magnetometer Z", driver.getMagZ());

	            // Do something with the data here
	            duration += System.nanoTime() - start;
//	            System.out.print(driver.toString());
	            Thread.sleep(5);
	        }
	        System.out.println("Finished polling. Mean poll time: " + TimeUnit.MILLISECONDS.convert((duration / iterations), TimeUnit.NANOSECONDS) +" ms");
	    }catch(Exception e){
	        e.printStackTrace();
	    }	}

}
