import java.util.*;

public class Oppgaveoversikt {

  private ArrayList<Student> studenter;
  private int antStud = 0;

  public Oppgaveoversikt() {
    studenter = new ArrayList<Student>();
  }

  public int getAntallStudenter() {
    return studenter.size();
  }
  public int getAntallOppgStud(Student student) {
    int antall = student.getAntOppg();
    return antall;
  }
  public void okAntallOppgStud(Student student, int okning) {
    student.okAntOppg(okning);
  }
  public void nyStudent(String navn, int antOppg) {
    boolean navnEksisterer = false;
    for (int i = 0; i < getAntallStudenter(); i++) {
      if (studenter.get(i).getNavn().equals(navn)) {
        navnEksisterer = true;
      }
    }
    if (!navnEksisterer) {
      Student nyStudent = new Student(navn, antOppg);
      studenter.add(nyStudent);
    } else {
      System.out.println("Dette navnet er allerede i bruk!");
    }
  }
  public String toString() {
    String tekst = "";
    for (int i = 0; i < getAntallStudenter(); i++) {
      tekst = tekst + studenter.get(i).toString() + " | ";
    }
    return tekst;
  }
  public int studentId(String navn) {
    int index = -1;
    for (int i = 0; i < getAntallStudenter(); i++) {
      if (studenter.get(i).getNavn().equals(navn)) {
        index = i;
        break;
      }
    }
    return index;
  }

  public static void main(String[] args) {
    Oppgaveoversikt o = new Oppgaveoversikt();
    while (true) {
      o.visMeny();
    }
  }

  public void visMeny() {
    Scanner in = new Scanner(System.in);
    System.out.println("");
    System.out.println("");
    System.out.println("Tast 1 for å vise alle studenter");
    System.out.println("Tast 2 for å finne antall oppgaver en bestemt student har gjort");
    System.out.println("Tast 3 for å finne antall studenter som er registrert");
    System.out.println("Tast 4 for å registrere en ny student");
    System.out.println("Tast 5 for å øke antall oppgaver gjort av en bestemt student");

    int menyInput = in.nextInt();
    if (menyInput == 1) {
      skrivUtAlle();
    } else if (menyInput == 2) {
      in.nextLine();
      System.out.println(toString());
      System.out.print("Hvilken student? "); String navn = in.nextLine();
      int id = studentId(navn);
      try {
        Student student = studenter.get(id);
        System.out.print(student.getNavn() + " har gjort " + student.getAntOppg() + " oppgaver");
      } catch(IndexOutOfBoundsException e) {
        System.out.print("Denne studenten eksisterer ikke, tast 1 i menyen for å se alle studentene");
      }
    } else if (menyInput == 3) {
      System.out.print(getAntallStudenter() + " studenter er registrert");
    } else if (menyInput == 4) {
      in.nextLine();
      System.out.print("Skriv inn navn på studenten: "); String navn = in.nextLine();
      System.out.print("Skriv inn antall oppgaver studenten har gjort: "); int antOppg = in.nextInt();
      nyStudent(navn, antOppg);
    } else if (menyInput == 5) {
      in.nextLine();
      System.out.println(toString());
      System.out.print("Hvilken student? "); String navn = in.nextLine();
      int id = studentId(navn);
      try {
        System.out.print("Hvor mange flere oppgaver er gjort? "); int okning = in.nextInt();
        Student student = studenter.get(id);
        student.okAntOppg(okning);
        System.out.print(student.getNavn() + " har nå gjort " + student.getAntOppg() + " oppgaver");
      } catch(IndexOutOfBoundsException e) {
        System.out.print("Denne studenten eksisterer ikke, tast 1 i menyen for å se alle studentene");
      }
    } else {
      System.out.println("Du må taste inn et tall fra 1-3");
    }
  }

  public void skrivUtAlle() {
    System.out.println("");
    if (getAntallStudenter() < 1) {
      System.out.println("Ingen studenter er registrert");
    } else {
      System.out.println("Studenter:");
      for (int i = 0; i < getAntallStudenter(); i++) {
        Student student = studenter.get(i);
        System.out.println("Navn: " + student.getNavn() + ", oppgaver: " + student.getAntOppg());
      }
    }
  }
}
