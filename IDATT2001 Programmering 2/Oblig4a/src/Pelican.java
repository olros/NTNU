public class Pelican extends Oviparous implements Walkable, Flyable {
	public Pelican(String name, int code) {
		super(name, code);
	}

	@Override
	public boolean walk() {
		return true;
	}

	@Override
	public boolean fly() {
		return true;
	}
}
