package Main;

import Models.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class TollPlazaClient {
	private TollPlaza tollPlaza;
	private static final LocalDate date = LocalDate.of(2020,5,28);
	private static final LocalTime rushhourTime = LocalTime.of(7,50,0);
	private static final LocalTime nonRushhourTime = LocalTime.of(12,30,0);

	public static void main(String[] args) {
		TollPlazaClient t = new TollPlazaClient();
		t.run();
	}

	private void run() {
		// Oppretter bomstasjon
		this.tollPlaza = new TollPlaza(1, "Vikebukt bomstasjon");

		// Oppretter biler
		Vehicle dieselCar = VehicleFactory.getInstance().newDieselCar("PR12345", 1600);
		Vehicle petrolCar = VehicleFactory.getInstance().newPetrolCar("AB98765", 2200);
		Vehicle electricalCar = VehicleFactory.getInstance().newElectricalCar("TR45678", 2000);
		Vehicle truck = VehicleFactory.getInstance().newTruck("KL23455", 7400);
		Vehicle motorcycle = VehicleFactory.getInstance().newMotorcycle("DT12121", 250);

		// Legger bilene til i registeret
		VehicleRegister.getInstance().registerNewVehicle(dieselCar);
		VehicleRegister.getInstance().registerNewVehicle(petrolCar);
		VehicleRegister.getInstance().registerNewVehicle(electricalCar);
		VehicleRegister.getInstance().registerNewVehicle(truck);
		VehicleRegister.getInstance().registerNewVehicle(motorcycle);

		// Legger til bompasseringer
		this.tollPlaza.registerTollPassage(dieselCar.getLicensePlateNumber(), date, rushhourTime);
		this.tollPlaza.registerTollPassage(petrolCar.getLicensePlateNumber(), date, nonRushhourTime);
		this.tollPlaza.registerTollPassage(electricalCar.getLicensePlateNumber(), date, rushhourTime);
		this.tollPlaza.registerTollPassage(truck.getLicensePlateNumber(), date, nonRushhourTime);
		this.tollPlaza.registerTollPassage(motorcycle.getLicensePlateNumber(), date, rushhourTime);

		// Get created toll plaza's toll passages and print them
		TollPassageRegister
				.getInstance()
				.getPassagesByTollCollectionPointId(this.tollPlaza.getId())
				.forEach(System.out::println);
	}
}
