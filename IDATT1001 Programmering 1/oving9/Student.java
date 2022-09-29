import java.util.*;

public class Student {

  private final String navn;
  private int antOppg;

  public Student(String navn, int antOppg) {
    this.navn = navn;
    this.antOppg = antOppg;
  }

  public String getNavn() {
  	return this.navn;
  }
  public int getAntOppg() {
  	return this.antOppg;
  }
  public void okAntOppg(int okning) {
    this.antOppg = this.antOppg + okning;
  }
  public String toString() {
    return this.navn + ", " + Double.toString(this.antOppg);
  }

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    System.out.print("Skriv inn navn på studenten: "); String navn = in.nextLine();
    System.out.print("Skriv inn antall oppgaver studenten har gjort: "); int antOppg = in.nextInt();
    Student nyStudent = new Student(navn, antOppg);
    System.out.println("");
    System.out.println("Data registrert på student:");
    System.out.println("Navn: " + nyStudent.getNavn());
    System.out.println("Antall oppgaver gjort: " + nyStudent.getAntOppg());
    System.out.println("Legger til 4: ");
    nyStudent.okAntOppg(4);
    System.out.println("Antall oppgaver gjort: " + nyStudent.getAntOppg());
    System.out.println(nyStudent.toString());
  }
}
