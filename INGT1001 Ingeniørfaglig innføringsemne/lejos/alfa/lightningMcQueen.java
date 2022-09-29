import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;
//import lejos.hardware.sensor.EV3TouchSensor;
//import lejos.hardware.sensor.EV3ColorSensor;
//import lejos.hardware.sensor.EV3UltrasonicSensor;
//import lejos.hardware.sensor.EV3GyroSensor;
//import lejos.hardware.sensor.NXTTouchSensor;
//import lejos.hardware.sensor.NXTLightSensor;
//import lejos.hardware.sensor.NXTColorSensor;
//import lejos.hardware.sensor.NXTSoundSensor;
//import lejos.hardware.sensor.NXTUltrasonicSensor;

class lightningMcQueen {
	public static void main(String[] arg) throws Exception {
		try {

			Brick brick = BrickFinder.getDefault();

			Port sensFarge1 = brick.getPort("S1"); // Fargesensor nr1
			Port sensFarge2 = brick.getPort("S2"); // Fargesensor nr2
			Port sensPress = brick.getPort("S4"); // Trykksensor

			EV3 ev3 = (EV3) BrickFinder.getLocal();
			TextLCD lcd = ev3.getTextLCD();
			Keys keys = ev3.getKeys();

			// Fargesensor nr1
			EV3ColorSensor fargesensor1 = new EV3ColorSensor(sensFarge1);
			SampleProvider fargeLeser1 = fargesensor1.getMode("RGB");
			float[] fargeSample1 = new float[fargeLeser1.sampleSize()];

			// Fargesensor nr2
			EV3ColorSensor fargesensor2 = new EV3ColorSensor(sensFarge2);
			SampleProvider fargeLeser2 = fargesensor2.getMode("RGB");
			float[] fargeSample2 = new float[fargeLeser2.sampleSize()];

			// Trykksensor
			/*SampleProvider trykkSensor = new EV3TouchSensor(sensPress);
			float[] trykkSample = new float[trykkSensor.sampleSize()];*/

			// Start ved trykk på en knapp
			System.out.println("Trykk for å begynne");
			keys.waitForAnyPress();

			double svart = 0.1;
			int fart = 500;
			int svingFart = 500;
			int litenSvingFart = 50;

			// 500, 500, 50 funker

			// Start begge motorene
			Motor.A.setSpeed(fart);
			Motor.C.setSpeed(fart);
			Motor.A.forward();
			Motor.C.forward();

			boolean lokke = true;

			while(lokke){
				fargeLeser1.fetchSample(fargeSample1, 0);
				fargeLeser2.fetchSample(fargeSample2, 0);
				//System.out.println(fargeSample1[0] + " og " + fargeSample2[0]);

				// Gå rett fremover om ingen måler svart
				if(fargeSample1[0] > svart && fargeSample2[0] > svart){
					System.out.println("Rett frem");
					Motor.A.setSpeed(fart);
					Motor.C.setSpeed(fart);
					Motor.A.forward();
					Motor.C.forward();
					Thread.sleep(10);
				} else {
					System.out.println("else");
					if (fargeSample1[0] < svart){
						// Sving til høyre frem til ingen måler svart
						System.out.println("Color 1");
						Motor.A.setSpeed(litenSvingFart);
						Motor.C.setSpeed(svingFart);
						Motor.A.backward();
						Motor.C.forward();
						Thread.sleep(20);
					} else if (fargeSample2[0] < svart){
						// Sving til venstre frem til ingen måler svart
						System.out.println("Color 1");
						Motor.A.setSpeed(svingFart);
						Motor.C.setSpeed(litenSvingFart);
						Motor.A.forward();
						Motor.C.backward();
						Thread.sleep(20);
					} else {
						System.out.println("Rett frem");
						Motor.A.setSpeed(fart);
						Motor.C.setSpeed(fart);
						Motor.A.forward();
						Motor.C.forward();
						Thread.sleep(10);
					}
					if(keys.getButtons()==keys.ID_ENTER){
						lokke = false;
					}
				}

				// Sjekk om knappen er trykket på
				/*if (trykkSample != null && trykkSample.length > 0){
					trykkSensor.fetchSample(trykkSample, 0);
					if (trykkSample[0] > 0){
						System.out.println("Avslutter");
						lokke = false;
					}
				}*/
				//Thread.sleep(10);
			}

		} catch(Exception e){
			System.out.println("Feil: " + e);
			e.printStackTrace();
		}
	}
}