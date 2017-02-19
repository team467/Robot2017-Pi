/**
 *
 */


import java.io.IOException;
import java.text.DecimalFormat;

import com.pi4j.gpio.extension.ads.ADS1015GpioProvider;
import com.pi4j.gpio.extension.ads.ADS1015Pin;
import com.pi4j.gpio.extension.ads.ADS1x15GpioProvider.ProgrammableGainAmplifierValue;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public class ADS1015Gpio {

	private GpioController gpio = null;
	private ADS1015GpioProvider gpioProvider = null;
	private static ADS1015Gpio ads1015 = null;
	private NetworkTable table = null;

	/**
	 *
	 */
	public ADS1015Gpio() {
		try {
			// number formatters
			final DecimalFormat df = new DecimalFormat("#.##");
			final DecimalFormat pdf = new DecimalFormat("###.#");

			gpio = GpioFactory.getInstance();

			// create custom ADS1015 GPIO provider
			// Default I2C address is 0x48;
				gpioProvider = new ADS1015GpioProvider(I2CBus.BUS_1, ADS1015GpioProvider.ADS1015_ADDRESS_0x48);

			// provision gpio analog input pins from ADS1015
			GpioPinAnalogInput myInputs[] = {
					gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A0, "MyAnalogInput-A0"),
					gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A1, "MyAnalogInput-A1"),
					gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A2, "MyAnalogInput-A2"),
					gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A3, "MyAnalogInput-A3"), };

			// gpioProvider.setProgrammableGainAmplifier(ProgrammableGainAmplifierValue.PGA_4_096V,
			// ADS1015Pin.ALL);
			gpioProvider.setProgrammableGainAmplifier(ProgrammableGainAmplifierValue.PGA_6_144V, ADS1015Pin.ALL);

			// Define a threshold value for each pin for analog value change
			// events to be raised.
			// It is important to set this threshold high enough so that you
			// don't overwhelm your program with change events for insignificant
			// changes
			gpioProvider.setEventThreshold(500, ADS1015Pin.ALL);

			// Define the monitoring thread refresh interval (in milliseconds).
			// This governs the rate at which the monitoring thread will read
			// input values from the ADC chip
			// (a value less than 50 ms is not permitted)
			gpioProvider.setMonitorInterval(50);

			// Get the WPILIB network tables
			NetworkTable.setClientMode();
			NetworkTable.setIPAddress("10.0.1.9");
			table = NetworkTable.getTable("gearlifter");

			// create analog pin value change listener
			GpioPinListenerAnalog listener = new GpioPinListenerAnalog() {
				@Override
				public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
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

					// Send to network table
					table.putNumber("Gear Lifter Position", value);
				}
			};

			myInputs[0].addListener(listener);
			myInputs[1].addListener(listener);
			myInputs[2].addListener(listener);
			myInputs[3].addListener(listener);

		} catch (UnsupportedBusNumberException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public void shutdown() {
		gpio.shutdown();
	}

	public static ADS1015Gpio getInstance() {
		if (ads1015 == null) {
			ads1015 = new ADS1015Gpio();
		}
		return ads1015;
	}

	public static void main(String args[]) throws InterruptedException, UnsupportedBusNumberException, IOException {
		System.out.println("<--Pi4J--> ADS1015 GPIO Example ... started.");

		ADS1015Gpio gpio = ADS1015Gpio.getInstance();
		// keep program running for 10 minutes
		Thread.sleep(600000);

		// stop all GPIO activity/threads by shutting down the GPIO
		// controller
		// (this method will forcefully shutdown all GPIO monitoring threads
		// and scheduled tasks)
		gpio.shutdown();

		System.out.println("Exiting ADS1015GpioExample");
	}

}
