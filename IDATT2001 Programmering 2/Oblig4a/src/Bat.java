public class Bat extends Mammal implements Flyable {
	public Bat(String name, int code) {
		super(name, code);
	}

	@Override
	public boolean fly() {
		return true;
	}
}
