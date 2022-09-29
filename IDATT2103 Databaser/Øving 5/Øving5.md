# Øving 5 - Transaksjoner

---

## a)

### Teori

#### Hvilke typer låser har databasesystemene?

**Delt lås**: Brukes kun ved lesing av data og godtar at flere transaksjoner leser den samme dataene samtidig. En delt lås kan settes selv om andre transaksjoner allerede har satt delt lås på samme data.

**Eksklusiv lås**: Kan kun settes av én transaksjon om gangen. Andre transaksjoner har ikke tilgang til dataen før låsen opphaves. Hindrer at dataen oppdateres mens andre oppdaterer eller leser den samme dataen.

#### Hva er grunnen til at at man gjerne ønsker lavere isolasjonsnivå enn SERIALIZABLE?

Fordi _SERIALIZABLE_ låser så mye av databasen at det begrenser hastighet hvor et antall transaksjoner som kan gjennomføres.

#### Hva skjer om to pågående transaksjoner med isolasjonsnivå serializable prøver `select sum(saldo) from konto?`

Den ene vil måtte vente til den andre er ferdig.

#### Hva er to-fase-låsing?

Det er når en transaksjon deles opp i to faser. I den første fasen settes de nødvendige låsene. I den andre fasen oppheves låsene.

#### Hvilke typer samtidighetsproblemer (de har egne navn) kan man få ved ulike isolasjonsnivåer?

- _Tapt oppdatering_: Når den ene oppdateringen skrives over av en annen. Skjer når to transaksjoner leser verdien de skal endre samtidig og oppdaterer basert på den.
- _Ikke-overgitte data_: Når en transaksjon får se foreløpige resultater fra en annen transaksjon som ikke har utført _COMMIT_. Hvis da de foreløpige resultatene annuleres, så vil sluttresultaten bli feil.
- _Inkonsistente innhentinger_: Hvis en lesing fra databasen gir feil data. Skjer hvis man leser flere rader samtidig som en annen transaksjon oppdaterer dem. Da får man verken resultatet fra før eller etter den andre transaksjonen.

#### Hva er optimistisk låsing/utførelse? Hva kan grunnen til å bruke dette være?

Det er når transaksjoner kjører uten å låse dataen. Før de _COMMIT_'er sjekker de om dataen den har lest har blitt endret av andre transaksjoner i mellomtiden. Dette øker muligheten for samtidighet og kan fungere godt hvis det ikke er så ofte data endres.

#### Hvorfor kan det være dumt med lange transaksjoner (som tar lang tid)? Vil det være lurt å ha en transaksjon hvor det kreves input fra bruker?

Dess lengre tid en transaksjon brukes, dess større sjanse er det for at data som brukes endres samtidig eller at det samler seg opp en kø med andre transaksjoner som venter på å kunne låse den samme dataen.

### Oppgaver

#### Oppgave 1
![image](https://user-images.githubusercontent.com/31009729/96298233-6a02c200-0ff2-11eb-8e31-cce4f2076c93.png)

- **Hva skjer og hvorfor?**
  - _Klient 1_ får tilgang til å kjøre select-spørringen og, men ikke oppdateringen før _Klient 2_ har commit'et siden _serializable_ låser skriving. _Klient 1_ får lese siden den har isolasjonsnivå **READ UNCOMMITTED**.
- **Hva hadde skjedd om Klient 2 hadde brukt et annet isolasjonsnivå?**
  - Da ville _Klient 1_ kunne oppdatert før _Klient 2_ commit'er

#### Oppgave 2

##### a)
![image](https://user-images.githubusercontent.com/31009729/96298821-6459ac00-0ff3-11eb-8551-7fb4e85005cd.png)

- **Hva blir resultatet? Hvorfor må det være slik?**
  - Saldo blir lik 2 på begge kontoene fordi klientene kjører samtidig og _Klient 2_ committ'er sist. Dermed blir _Klient 2_'s oppdateringer gjeldene.

##### b)
![image](https://user-images.githubusercontent.com/31009729/96299204-f6fa4b00-0ff3-11eb-8802-9cabd9ad413a.png)

- **Hva blir resultatet? Forklar forskjellen fra oppgave a). Vil det ha noe å si om man endrer isolasjonsnivå på klientene?**
  - Det blir en deadlock der begge klientene venter på den andre for å få tilgang til en rad.
  - Nei

#### Oppgave 3

![image](https://user-images.githubusercontent.com/31009729/96299915-0a59e600-0ff5-11eb-942a-3284bb5a7032.png)

- **Hva skjer?**
  - På første SELECT returneres én sum, mens på de 2 neste returneres den samme summen pluss 10 siden _Klient 1_ bruker **READ UNCOMMITTED**. 
- **Hva vil skje om Klient 1 bruker read committed, repeatable read eller serializable?**
  - **READ COMMITTED**: Øker med 10 etter at _Klient 2_ committ'er
  - **REPEATABLE READ**: Øker aldri
  - **SERIALIZABLE**: Øker aldri

#### Oppgave 4

Lag en kjøring med to klienter som tester phantom reads. Her kan det være lurt å tenke igjennom
isolasjonsnivå. Om resultatet ikke er som forventet så kan det være lurt å sjekke dokumentasjonen

**Transaksjon 1**:
```sql
SELECT * FROM konto WHERE saldo BETWEEN 10 AND 40;
```

**Transaksjon 2**:
```sql
INSERT INTO konto(kontonr, saldo) VALUES ( 7, 27 );
COMMIT;
```

**Transaksjon 1**:
```sql
SELECT * FROM konto WHERE saldo BETWEEN 10 AND 40;
COMMIT;
```

---

## b)

```java
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Oppgave5b {

	private static Map<String, String> getProperties() {
		Map<String, String> result = new HashMap<>();
		try (InputStream input = new FileInputStream("config.properties")) {
			Properties prop = new Properties();
			prop.load(input);
			result.put("username", prop.getProperty("username"));
			result.put("password", prop.getProperty("password"));
			result.put("database_url", prop.getProperty("database_url"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static Connection connect() throws SQLException {
		Map<String, String> properties = getProperties();
		String user = properties.get("username");
		String pass = properties.get("password");
		String url = properties.get("database_url");
		return DriverManager.getConnection(url, user, pass);
	}

	public static void getInfoByIsbn(String isbn) {
		try {
			Connection connection = connect();
			PreparedStatement stmt;
			stmt = connection.prepareStatement("select forfatter, tittel, count(*) as antall " +
					"from boktittel join eksemplar on boktittel.isbn = eksemplar.isbn and eksemplar.isbn = ?");
			stmt.setString(1, isbn);
			printInfo(stmt.executeQuery());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void printInfo(ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			System.out.println("-----------------------\n" +
					"Forfatter: " + resultSet.getString("forfatter") + "\n" +
					"Tittel: " + resultSet.getString("tittel") + "\n" +
					"Antall: " + resultSet.getInt("antall"));
		}
	}

	public static int updateInfoByIsbn(String isbn, String rentedBy, int copyNr) {
		try {
			Connection connection = connect();
			PreparedStatement statement;
			statement = connection.prepareStatement("update eksemplar set laant_av = ? " +
					"where isbn = ? and eks_nr = ? and laant_av is null;");
			statement.setString(1, rentedBy);
			statement.setString(2, isbn);
			statement.setInt(3, copyNr);
			return statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static void main(String[] args) {
		getInfoByIsbn("0-07-241163-5");
		System.out.println("Rows affected: " + updateInfoByIsbn("0-07-241163-5", "Per Olsen", 1));
	}
}
```
