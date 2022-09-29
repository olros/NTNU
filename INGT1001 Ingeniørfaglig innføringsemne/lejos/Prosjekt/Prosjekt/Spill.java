import lejos.hardware.motor.*;
import lejos.hardware.lcd.*;
import lejos.hardware.port.Port;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.hardware.Keys;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;
class Spill{
	public static Brick brick = BrickFinder.getDefault();
	public static EV3 ev3 = (EV3) BrickFinder.getLocal();
	public static Keys keys = ev3.getKeys();
	public static Metoder metoder = new Metoder();

	public static void main(String []args) throws Exception {
		poker();


	}

	public static void poker() throws Exception {
		boolean fortsett = true;
		int teller = 0;
		while (fortsett){
			if(teller == 0){
				for (int i = 0; i < 4; i++) {
					metoder.delUtLukket();
					metoder.roter(160);
				}
				metoder.roter(70);
				metoder.delUtLukket();
				for (int i = 0; i < 3; i++) {
					metoder.delUtOpen();

				}
				teller++;
			}
			else{
				if(metoder.isPressed()){
					metoder.delUtLukket();
					metoder.delUtOpen();
				}
			}
			if(keys.getButtons()==keys.ID_ESCAPE){
				fortsett = false;
			}
		}
	}

	public static void delUtKort() throws Exception {
		for (int i = 0; i < 10; i++) {
			metoder.delUtLukket();
			Thread.sleep(1000);
		}
	}
}