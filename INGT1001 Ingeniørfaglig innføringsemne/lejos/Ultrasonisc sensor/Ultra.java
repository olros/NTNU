import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.motor.*;
import lejos.robotics.RangeFinder;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;

public class Ultra implements RangeFinder {
	EV3UltrasonicSensor sensor;
	SampleProvider		sp;
	static Ultra               ultra;
    float [] 			sample;
    static float[]             trykkSample;
    static SampleProvider      trykksensor;

    public static void main(String[] args) {
		Brick brick = BrickFinder.getDefault();

		// Ultrasonisk sensor
		Port s4 = brick.getPort("S4");
		ultra = new Ultra(s4);

		// trykksensor
		/*Port s2 = brick.getPort("S2");
		trykksensor = new EV3TouchSensor(s2);
		trykkSample = new float[trykksensor.sampleSize()];*/

		boolean fortsett = true;
		int antallHindringer = 0;
		Motor.A.forward();
		Motor.C.forward();

		while(fortsett) {
			System.out.println("Avstand: " + ultra.getRange());
			if (ultra.getRange() < 0.15) {
				Motor.A.stop(true);
				Motor.C.stop(true);
				roter();
				if (antallHindringer > 20) {
					fortsett = false;
				}
				antallHindringer++;
			}
			Delay.msDelay(10);
		}
	}

    public Ultra(Port port)
	{
		sensor = new EV3UltrasonicSensor(port);
		sp = sensor.getDistanceMode();
	    sample = new float[sp.sampleSize()];
	}

	@Override
	public float getRange()
	{
       		sp.fetchSample(sample, 0);

       		return sample[0];
	}

	@Override
	public float[] getRanges()
	{
       		sp.fetchSample(sample, 0);

       		return sample;
	}

	public static boolean isPressed() {
		boolean pressed = false;
		trykksensor.fetchSample(trykkSample, 0);
		if (trykkSample[0] > 0){
			System.out.println("Avslutter");
			pressed = true;
		}
		return pressed;
	}

	public static void roter() {
		boolean storAvstand = false;
		while(!storAvstand) {
			Motor.A.rotate(10, true);
			Motor.C.rotate(-10, true);
			if (ultra.getRange() > 0.15) {
				//Motor.A.rotate(25, true);
				//Motor.C.rotate(-25, true);
				storAvstand = true;
				Motor.A.forward();
				Motor.C.forward();
			}
		}
	}

}