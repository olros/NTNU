import java.util.Collection;

import lejos.hardware.RemoteBTDevice;
import lejos.remote.ev3.RemoteEV3;


public class RemoteBluetooth {

	public static void main(String[] args) throws Exception {
		RemoteEV3 ev3 = new RemoteEV3("192.168.0.9");
		
		System.out.println("Bluetooth address: " + ev3.getBluetooth().getBluetoothAddress());
		
		Collection<RemoteBTDevice> devices = ev3.getBluetooth().search();
		
		System.out.println("Bluetooth scan ...");
		for(RemoteBTDevice d: devices) {
			System.out.println(d.getName() + "\t" + d.getAddress());
		}

	}

}