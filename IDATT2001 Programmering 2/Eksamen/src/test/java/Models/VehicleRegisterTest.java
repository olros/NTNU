package Models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VehicleRegisterTest {
	private Vehicle dieselVehicle;
	private static final String licenseNumber = "PR13676";

	@Test
	void registerNewVehicle_success() {
		dieselVehicle = VehicleFactory.getInstance().newDieselCar(licenseNumber, 2100);
		boolean registration = VehicleRegister.getInstance().registerNewVehicle(dieselVehicle);
		assertTrue(registration);
	}

	@Test
	void registerNewVehicle_fail() {
		assertThrows(IllegalArgumentException.class, () -> {
			VehicleRegister.getInstance().registerNewVehicle(null);
		});
	}

	@Test
	void searchBylicensePlate_success() {
		Vehicle vehicle = VehicleRegister.getInstance().searchBylicensePlate(licenseNumber);
		assertEquals(vehicle.getLicensePlateNumber(), licenseNumber);
	}

	@Test
	void searchBylicensePlate_fail() {
		Vehicle vehicle = VehicleRegister.getInstance().searchBylicensePlate("TR12345");
		assertEquals(vehicle, null);
	}

	@Test
	void searchByWeightMoreThen() {
		ArrayList<Vehicle> vehicles = VehicleRegister.getInstance().searchByWeightMoreThen(2500);
		assertEquals(vehicles, new ArrayList<>());
	}
}