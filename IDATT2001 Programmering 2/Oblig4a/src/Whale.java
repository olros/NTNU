public class Whale extends Mammal implements Jumpable, Swimmable {
	public Whale(String name, int code) {
		super(name, code);
	}

	@Override
	public boolean jump() {
		return true;
	}

	@Override
	public boolean swim() {
		return true;
	}
}
