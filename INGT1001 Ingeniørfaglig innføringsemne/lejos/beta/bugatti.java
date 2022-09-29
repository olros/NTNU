import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.hardware.sensor.NXTLightSensor;
import lejos.hardware.sensor.NXTColorSensor;
import lejos.hardware.sensor.NXTSoundSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;

class bugatti {
	public static void main(String[] arg) throws Exception {
		try{

			Brick brick = BrickFinder.getDefault();

			Port sensFarge1 = brick.getPort("S1");
			Port sensFarge2 = brick.getPort("S2");
			Port sensPress = brick.getPort("S4"); //trykksensor

			EV3 ev3 = (EV3) BrickFinder.getLocal();
			TextLCD lcd = ev3.getTextLCD();
			Keys keys = ev3.getKeys();

			lcd.drawString("Begynn Race", 0, 1);
			keys.waitForAnyPress();

			EV3ColorSensor fargesensor1 = new EV3ColorSensor(sensFarge1);
			SampleProvider fargeLeser1 = fargesensor1.getMode("RGB");
			float[] fargeSample1 = new float[fargeLeser1.sampleSize()];
			EV3ColorSensor fargesensor2 = new EV3ColorSensor(sensFarge2);
			SampleProvider fargeLeser2 = fargesensor2.getMode("RGB");
			float[] fargeSample2 = new float[fargeLeser2.sampleSize()];

			SampleProvider trykkSensor = new EV3TouchSensor(sensPress);
			float[] trykkSample = new float[trykkSensor.sampleSize()];

			Motor.A.setSpeed(700);
			Motor.C.setSpeed(700);

			boolean lokke = true;
			int i = 0;

			int roteringsFart = 150;

			while(lokke){
				fargeLeser1.fetchSample(fargeSample1, 0);
				fargeLeser2.fetchSample(fargeSample2, 0);
				System.out.println(fargeSample1[0] + " og " + fargeSample2[0]);

				if(fargeSample1[0] < 0.035 || fargeSample2[0] < 0.035){
					Motor.A.forward();
					Motor.C.forward();
				}else if(fargeSample1[0] > 0.07 && fargeSample2[0] > 0.07){
					if (i == 15){
						Motor.A.setSpeed(roteringsFart/4);
						Motor.C.setSpeed(roteringsFart/2);
						Motor.A.forward();
						Motor.C.backward();

						Thread.sleep(20);
					}else{


						Motor.A.setSpeed(roteringsFart);
						Motor.C.setSpeed(roteringsFart/2);



						Motor.A.backward();
						Motor.C.forward();
						Thread.sleep(20);
						i++;
					}
				}else{

					Thread.sleep(10);
					Motor.A.setSpeed(700);
					Motor.C.setSpeed(700);
					Motor.A.forward();
					Motor.C.forward();
					i = 0;
				}

			}


        if (trykkSample != null && trykkSample.length > 0){
          trykkSensor.fetchSample(trykkSample, 0);
      	 	if (trykkSample[0] > 0){
      			System.out.println("Avslutter");
      		 	lokke = false;
      		}
        }else System.out.println("Sample er null eller 0");

		} catch(Exception e){
			System.out.println("Feil: " + e);
			e.printStackTrace();
		}
	}
}