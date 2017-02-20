/**
 *
 */
package org.usfirst.frc.team467.pi.sensors;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public class RioConnection {
//	public static final String NETWORK_TABLE_SERVER_IP = "10.0.1.28";
	public static final String NETWORK_TABLE_SERVER_NAME_OR_IP = "roborio-467-frc.local";
	public static final String TABLE_NAME = "Sensors on Pi";

	private static NetworkTable table = null;

	public static NetworkTable getInstance() {
		if (table == null) {
			NetworkTable.setClientMode();
			NetworkTable.setIPAddress(NETWORK_TABLE_SERVER_NAME_OR_IP);
			table = NetworkTable.getTable(TABLE_NAME);
		}
		return table;
	}
}
