import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.motor.*;
import lejos.hardware.sensor.*;
import lejos.robotics.RangeFinder;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;

public class Color {
	EV3ColorSensor sensor;
	SampleProvider sp;
	static Color color;
    float [] sample;
    static boolean venstre;
    static double svart;
    static int harRotert;

    public static void main(String[] args) {
		Brick brick = BrickFinder.getDefault();

		// Color sensor
		Port s4 = brick.getPort("S4");
		color = new Color(s4);

		svart = 5;
		System.out.println("Svart: " + svart);


		boolean fortsett = true;
		Motor.A.forward();
		Motor.C.forward();
		venstre = true;

		while(fortsett) {
			if (color.getColor()*100 > svart) {
				Motor.A.stop(true);
				Motor.C.stop(true);
				harRotert = 1;
				roter();
				System.out.println("Annet " + (color.getColor()*100));
			} else {
				System.out.println("Svart " + (color.getColor()*100));
			}
			Delay.msDelay(50);
		}
	}

	public static void roter() {
		System.out.println("Roterer");
		int antallRoteringer = 0;
		int maxRoteringer = 25 * harRotert;
		while (color.getColor()*100 > svart && antallRoteringer < maxRoteringer) {
			System.out.println("While " + color.getColor()*100);
			if (venstre) {
				//Motor.A.rotate(-10, true);
				Motor.C.rotate(10, true);
				antallRoteringer++;
			} else {
				Motor.A.rotate(10, true);
				//Motor.C.rotate(-10, true);
				antallRoteringer++;
			}
		}
		if (antallRoteringer > (maxRoteringer - 1) && color.getColor()*100 > svart) {
			System.out.println("Fant ikke");
			if (venstre) {
				//Motor.A.rotate(10*maxRoteringer, true);
				Motor.C.rotate(-10*maxRoteringer, true);
			} else {
				Motor.A.rotate(-10*maxRoteringer, true);
				//Motor.C.rotate(10*maxRoteringer, true);
			}
			venstre = !venstre;
			if (harRotert < 10) {
				harRotert++;
			}
			roter();
		} else {
			if (venstre) {
				Motor.C.rotate(20, true);
			} else {
				Motor.A.rotate(20, true);
			}
			venstre = !venstre;
			Motor.A.forward();
			Motor.C.forward();
		}
	}

    public Color(Port port)
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