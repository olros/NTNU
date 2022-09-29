import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.motor.*;
import lejos.hardware.sensor.*;
import lejos.robotics.RangeFinder;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;

public class Color10 {

	EV3ColorSensor sensor;
	SampleProvider sp;

	static Color10 color1, color4;
    float [] sample;
    static double svart;
    static boolean sistSvart1 = true;

    public static void main(String[] args) {
		Brick brick = BrickFinder.getDefault();

		Port s1 = brick.getPort("S1");
		Port s4 = brick.getPort("S4");
		color1 = new Color10(s1);
		color4 = new Color10(s4);

		svart = 4;
		System.out.println("Svart: " + svart);

		boolean fortsett = true;

		Motor.A.forward();
		Motor.C.forward();

		while(fortsett) {

			System.out.println("1: " + color1.getColor()*100 + " 4: " + color4.getColor()*100);

			if (color1.getColor()*100 > svart && color4.getColor()*100 > svart) {
				// Ikke gjør noe
			} else {
				if (color1.getColor()*100 < svart) {
					Motor.A.stop(true);
					Motor.C.stop(true);
					roter(true);
				}
				if (color4.getColor()*100 < svart) {
					Motor.A.stop(true);
					Motor.C.stop(true);
					roter(false);
				}
			}

			Delay.msDelay(10);
		}
	}

	public static void roter(boolean left) {
		/*while (color1.getColor()*100 < svart || color4.getColor()*100 < svart) {
			System.out.println("Roter1: " + color1.getColor()*100 + " Roter4: " + color4.getColor()*100);
			if (left) {
				Motor.C.rotate(10, true);
			} else {
				Motor.A.rotate(10, true);
			}
		}*/
		int rotering = 50;
		if (left) {
			while (color1.getColor()*100 < svart) {
				Motor.A.rotate(-rotering, true);
				Motor.C.rotate(rotering, true);
			}
		} else {
			while (color4.getColor()*100 < svart) {
				Motor.A.rotate(rotering, true);
				Motor.C.rotate(-rotering, true);
			}
		}

		Motor.A.forward();
		Motor.C.forward();
	}

    public Color10(Port port)
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
