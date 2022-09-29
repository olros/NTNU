import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.motor.*;
import lejos.hardware.sensor.*;
import lejos.robotics.RangeFinder;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;

public class Color_v3 {
	EV3ColorSensor sensor;
	SampleProvider sp;
	static Color_v3 color1, color4;
    float [] sample;
    static double svart;
    static boolean sistSvart1;

    public static void main(String[] args) {
		Brick brick = BrickFinder.getDefault();

		// Color sensor
		Port s1 = brick.getPort("S1");
		Port s4 = brick.getPort("S4");
		color1 = new Color_v3(s1);
		color4 = new Color_v3(s4);

		svart = 4;
		System.out.println("Svart: " + svart);


		boolean fortsett = true;
		Motor.A.forward();
		Motor.C.forward();

		while(fortsett) {
			System.out.println("1: " + color1.getColor()*100 + " 4: " + color4.getColor()*100);
			if (color1.getColor()*100 < svart && color4.getColor() < svart) {
				// Ikke gj�r noe
			} else {
				if (color1.getColor()*100 < svart) {
					sistSvart1 = true;
				}
				if (color4.getColor()*100 < svart) {
					sistSvart1 = false;
				}
			}
			if (color1.getColor()*100 > svart && color4.getColor()*100 > svart) {
				Motor.A.stop(true);
				Motor.C.stop(true);
				roter(sistSvart1);
			}

			Delay.msDelay(50);
		}
	}

	public static void roter(boolean left) {
		while (color1.getColor()*100 > svart && color4.getColor()*100 > svart) {
			if (left) {
				Motor.C.rotate(10, true);
			} else {
				Motor.A.rotate(10, true);
			}
		}
		Motor.A.forward();
		Motor.C.forward();
	}

    public Color_v3(Port port)
	{
		sensor = new EV3ColorSensor(port);
		sp = sensor.getMode("RGB");
	    sample = new float[sp.sampleSize()];
	}

	public float getColor()
	{
       		sp.fetchSample(sample, 0);

       		return sample[0];
	}

	public float[] getColors()
	{
       		sp.fetchSample(sample, 0);

       		return sample;
	}

}