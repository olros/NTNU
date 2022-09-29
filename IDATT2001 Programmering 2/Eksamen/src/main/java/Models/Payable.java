package Models;

public interface Payable {
	String getLicensePlateNumber();
	double getCost(boolean withinRushHours);
}
