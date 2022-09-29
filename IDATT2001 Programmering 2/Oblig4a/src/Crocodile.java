public class Crocodile extends Oviparous implements Walkable, Swimmable {
	public Crocodile(String name, int code) {
		super(name, code);
	}

	@Override
	public boolean walk() {
		return true;
	}

	@Override
	public boolean swim() {
		return true;
	}
}
