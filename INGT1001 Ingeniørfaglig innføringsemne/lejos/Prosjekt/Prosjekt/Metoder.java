import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;

class Metoder {
	static float[] trykkSample;
	static SampleProvider trykkSensor;
	static float[] gyroSample;
	static SampleProvider gyroSensor;
	static int offset = 0;

	public Metoder() {
		try {
			Brick brick = BrickFinder.getDefault();

			Port sensKnapp = brick.getPort("S1");
			Port sensGyro = brick.getPort("S2");
			trykkSensor = new EV3TouchSensor(sensKnapp);
			trykkSample = new float[trykkSensor.sampleSize()];
			gyroSensor = new EV3GyroSensor(sensGyro).getAngleMode();
			gyroSample = new float[gyroSensor.sampleSize()];
		} catch(Exception e){
			System.out.println("Feil: " + e);
			e.printStackTrace();
		}
	}

	public static void main(String[] arg) throws Exception {
		try {
			Brick brick = BrickFinder.getDefault();

			Port sensKnapp = brick.getPort("S1");
			Port sensGyro = brick.getPort("S2");
			trykkSensor = new EV3TouchSensor(sensKnapp);
			trykkSample = new float[trykkSensor.sampleSize()];
			gyroSensor = new EV3GyroSensor(sensGyro).getAngleMode();
			gyroSample = new float[gyroSensor.sampleSize()];


			EV3 ev3 = (EV3) BrickFinder.getLocal();
			TextLCD lcd = ev3.getTextLCD();
			Keys keys = ev3.getKeys();

			boolean lokke = true;

			while(lokke){
				//System.out.println(getAngle());

				if(keys.getButtons()==keys.ID_ESCAPE){
					lokke = false;
				} else if(keys.getButtons()==keys.ID_LEFT){
					System.out.println("Lukket kort");
					delUtLukket();
				} else if(keys.getButtons()==keys.ID_RIGHT){
					System.out.println("Åpent kort");
					delUtOpen();
				}
				else if(keys.getButtons()==keys.ID_ENTER){
					roter(80);
				}
			}

		} catch(Exception e){
			System.out.println("Feil: " + e);
			e.printStackTrace();
		}
	}

	public static void delUtOpen() throws Exception {
		/*Motor.A.setSpeed(900);
		Motor.A.forward();
		Thread.sleep(7);*/
		/*Motor.D.forward();
		Thread.sleep(180);
		Motor.D.stop();*/
		Motor.A.setSpeed(175);
		Motor.A.forward();
		Thread.sleep(200);
		Motor.A.backward();
		Thread.sleep(200);
		Motor.A.stop();
		Thread.sleep(100);
		Motor.D.setSpeed(300);//150
		Motor.D.forward();
		Thread.sleep(718);//1436
		Motor.D.stop();
	}

	public static void delUtLukket() throws Exception {
		Motor.A.setSpeed(300);
		Motor.A.forward();
		Thread.sleep(120);
		Motor.A.backward();
		Thread.sleep(140);
		Motor.A.stop();
	}

	public static void roter(int grader) throws Exception {
		while((getAngle() * -1) < grader) {
			System.out.println(getAngle());
			Motor.C.backward();
			Thread.sleep(10);
		}
		Motor.C.stop();
		reset();
	}

	public static int getAngle() throws Exception {
		gyroSensor.fetchSample(gyroSample, 0);
		return (int) gyroSample[0] - offset;
	}
	public static void reset() throws Exception {
		gyroSensor.fetchSample(gyroSample, 0);
		offset = (int) gyroSample[0];
	}

	public static boolean isPressed() throws Exception {
		trykkSensor.fetchSample(trykkSample, 0);
		if (trykkSample[0] > 0){
			return true;
		} else {
			return false;
		}
	}
}