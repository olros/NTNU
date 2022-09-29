import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ZooClient {
	public static void main(String[] args) throws ZooException {

		Zoo zoo = new Zoo("Kristiansand Dyrepark");

		Collection<Animal> animals = new ArrayList<>();

		animals.add(new Crocodile("Crocodylus niloticus", 1001));
		animals.add(new Crocodile("Crocodylus niloticus", 1002));
		animals.add(new Crocodile("Crocodylus porosus", 1101));
		animals.add(new Crocodile("Crocodylus porosus", 1102));
		animals.add(new Pelican("Brown Pelican  ", 4001));
		animals.add(new Pelican("Dalmatian Pelican  ", 4101));

		animals.add(new Whale("Blue whale", 2001));
		animals.add(new Whale("Blue whale", 2002));
		animals.add(new Whale("Minke whale", 2101));
		animals.add(new Whale("Minke whale", 2102));
		animals.add(new Bat("Acerodon ", 3001));
		animals.add(new Bat("Cistugo  ", 3002));
		zoo.setAnimals(animals);

		Collection<Animal> flyableAnimals = zoo.getAnimals().stream().filter(a -> a instanceof Flyable).collect(Collectors.toCollection(ArrayList::new));
		Collection<Animal> jumpableAnimals = zoo.getAnimals().stream().filter(a -> (a instanceof Jumpable && a instanceof Mammal)).collect(Collectors.toCollection(ArrayList::new));

		flyableAnimals.stream().forEach(p -> {System.out.println(((Flyable)p).fly());});
		jumpableAnimals.stream().forEach(p -> {System.out.println(((Jumpable)p).jump());});

		List<Object> walker = zoo.getAnimals().stream().filter(p -> p instanceof Walkable).collect(Collectors.toList());
		walker.stream().forEach(p -> {
				try {
					((Flyable) p).fly();
				} catch (ClassCastException e) {
					// Custom ZooException
					try {
						throw new ZooException("This animal aren't capable of this");
					} catch (ZooException ex) {
						ex.printStackTrace();
					}

					// Stop the program
					// System.exit(0);
				}
	        }
		);
	}
}