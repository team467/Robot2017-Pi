/**
 *
 */
package org.usfirst.frc.team467.pi.sensors;

import java.io.IOException;
import java.text.DecimalFormat;

import com.pi4j.gpio.extension.ads.ADS1015GpioProvider;
import com.pi4j.gpio.extension.ads.ADS1015Pin;
import com.pi4j.gpio.extension.ads.ADS1x15GpioProvider.ProgrammableGainAmplifierValue;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public enum AnalogPins {

	GEAR_LIFTER(ADS1015Pin.INPUT_A0, "Gear Lifter"),
	PIN_1(ADS1015Pin.INPUT_A1, "Pin 1"),
	PIN_2(ADS1015Pin.INPUT_A2, "Pin 1"),
	PIN_3(ADS1015Pin.INPUT_A3, "Pin 3");

	private GpioController gpio = null;
	private ADS1015GpioProvider gpioProvider = null;
	private GpioPinAnalogInput input;
	private NetworkTable networkTable = null;

	public final String name;

	public final Pin id;

	private AnalogPins(Pin id, String name) {
		this.id = id;
		this.name= name;

		NetworkTable.setClientMode();
		NetworkTable.setIPAddress(RioConnection.NETWORK_TABLE_SERVER_NAME_OR_IP);
		networkTable = NetworkTable.getTable(RioConnection.TABLE_NAME);

		try {
			gpio = GpioFactory.getInstance();

			// create custom ADS1015 GPIO provider
			// Default I2C address is 0x48;
			gpioProvider = new ADS1015GpioProvider(I2CBus.BUS_1, ADS1015GpioProvider.ADS1015_ADDRESS_0x48);

			// provision gpio analog input pins from ADS1015
			input = gpio.provisionAnalogInputPin(gpioProvider, id, name);

			gpioProvider.setProgrammableGainAmplifier(ProgrammableGainAmplifierValue.PGA_6_144V, ADS1015Pin.ALL);

			// Define a threshold value for each pin for analog value change
			// events to be raised.
			// It is important to set this threshold high enough so that you
			// don't overwhelm your program with change events for insignificant
			// changes
			gpioProvider.setEventThreshold(0, ADS1015Pin.ALL);

			// Define the monitoring thread refresh interval (in milliseconds).
			// This governs the rate at which the monitoring thread will read
			// input values from the ADC chip
			// (a value less than 50 ms is not permitted)
			gpioProvider.setMonitorInterval(50);

		} catch (UnsupportedBusNumberException | IOException e) {
			e.printStackTrace();
		}
	}

	public void addListener(GpioPinListenerAnalog listener) {
		input.addListener(listener);
	}

	public void addNetworkTableListener() {
        GpioPinListenerAnalog listener = new GpioPinListenerAnalog() {
            @Override
            public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
                // Send to network table
                networkTable.putNumber("Gear Lifter Position", (event.getValue()-1600));
            }
        };
        addListener(listener);
	}

	public void addConsolePrintTableListener() {
        GpioPinListenerAnalog listener = new GpioPinListenerAnalog() {
            @Override
            public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
       		 // number formatters
        		final DecimalFormat df = new DecimalFormat("#.##");
        		final DecimalFormat pdf = new DecimalFormat("###.#");


        		// RAW value
                double value = event.getValue();

                // percentage
                double percent = ((value * 100) / ADS1015GpioProvider.ADS1015_RANGE_MAX_VALUE);

                // approximate voltage ( *scaled based on PGA setting )
                double voltage = gpioProvider.getProgrammableGainAmplifier(event.getPin()).getVoltage()
                        * (percent / 100);

                // display output
                System.out.println(" (" + event.getPin().getName() + ") : VOLTS=" + df.format(voltage)
                        + "  | PERCENT=" + pdf.format(percent) + "% | RAW=" + value + "       ");

            }
        };
        addListener(listener);
	}

	public void shutdown() {
		gpio.shutdown();
	}



}
