import java.util.ArrayList;
import java.util.Collection;

public class Zoo {
	private final String name;
	private Collection<Animal> animals = new ArrayList<Animal>();

	public Zoo(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Collection<Animal> getAnimals() {
		return animals;
	}

	public void setAnimals(Collection<Animal> animals) {
		this.animals.addAll(animals);
	}
}