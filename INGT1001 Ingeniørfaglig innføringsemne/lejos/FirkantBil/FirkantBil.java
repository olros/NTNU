import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.robotics.SampleProvider;

public class FirkantBil {
	public static void main (String[] args)  throws Exception {
		/*Motor.A.setSpeed(450);	// Sett hastighet (toppfart = 900)
		Motor.C.setSpeed(450);	// Kjør framover
		// Kjør framover
    	LCD.clear();
    	System.out.println("Fram 1000");
    	Motor.A.forward();
    	Motor.C.forward();
    	Thread.sleep(1000);
    	Motor.A.stop(); // Stopp motor A
    	Motor.C.stop(); // Stopp motor C
		// Sving
		LCD.clear();
		System.out.println("Snu 180 grader");
		Motor.A.rotate(540);  // Roter 180 grader med motor A
		//Motor.C.rotate(-180);
		Motor.C.stop();
		while (Motor.A.isMoving()) Thread.yield();  // Vent til rotasjon er ferdig
		// Kjør framover
    	LCD.clear();
    	System.out.println("Fram 1000");
    	Motor.A.forward();
    	Motor.C.forward();
    	Thread.sleep(1000);
    	Motor.A.stop();
    	Motor.C.stop();
		// Sving
		LCD.clear();
		System.out.println("Snu 180 grader");
		Motor.A.rotate(180);  // roter 180 gr med motor A
		Motor.C.stop();
		while (Motor.A.isMoving()) Thread.yield();  // Vent til rotasjon er ferdig
		// Kjør framover
    	LCD.clear();
    	System.out.println("Fram 1000");
    	Motor.A.forward();
    	Motor.C.forward();
    	Thread.sleep(1000);
    	Motor.A.stop();
    	Motor.C.stop();
		// Sving
		LCD.clear();
		System.out.println("Snu 180 grader");
		Motor.A.rotate(180);  // Roter 180 grader med motor A
		Motor.C.stop();
		while (Motor.A.isMoving()) Thread.yield();  // Vent til rotasjon er ferdig
		// Kjør framover
    	LCD.clear();
    	System.out.println("Fram 1000");
    	Motor.A.forward();
    	Motor.C.forward();
    	Thread.sleep(1000);
    	Motor.A.stop();
    	Motor.C.stop();*/

    	/*Motor.A.rotate(460);
    	Thread.sleep(1000);

    	Motor.A.rotate(460);
    	Thread.sleep(1000);

    	Motor.A.rotate(460);
    	Thread.sleep(1000);

    	Motor.A.rotate(460);
    	Thread.sleep(1000);*/

    	/*Motor.A.rotate(230, true);
    	Motor.C.rotate(-230, true);
		Thread.sleep(1000);

		Motor.A.rotate(230, true);
    	Motor.C.rotate(-230, true);
		Thread.sleep(1000);

		Motor.A.rotate(230, true);
    	Motor.C.rotate(-230, true);
		Thread.sleep(1000);

		Motor.A.rotate(230, true);
    	Motor.C.rotate(-230, true);
    	Thread.sleep(1000);*/

    	/*for (int i = 0; i < 4; i++) {
			Motor.A.forward();
    		Motor.C.forward();
    		Thread.sleep(1000);
    		Motor.A.stop();
    		Motor.C.stop();
			Motor.A.rotate(230, true);
			Motor.C.rotate(-230, true);

		}*/
		for (int i = 0; i < 4; i++) {
					Motor.A.forward();
		    		Motor.C.forward();
		    		Thread.sleep(1000);

		    		Motor.A.stop(true);
		    		Motor.C.stop(true);
					Motor.A.rotate(460);
		}
	}
}