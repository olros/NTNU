# Øving 6 - Fra tekstlig beskrivelse til SQL

## Oppgave
**Vikartjeneste**
Vikartjenesten har et arkiv over aktuelle vikarkandidater som er villig til å arbeide.

- Om kandidater lagrer vi fornavn, etternavn, telefon og epost-adresse.
- Om bedriftene som etterspør vikarer lagrer vi organisasjonsnr (entydig), bedriftens navn, telefon og epost-adresse.
- Hver kandidat kan ha mange forskjellige kvalifikasjoner.
- Hver gang en bedrift etterspør en vikar, må Vikartjenesten registrere dette med et oppdragsnummer, bedriftsnavn, hvilken kvalifikasjon som er nødvendige for den eventuelle vikaren, startdato for vikariatet og foreløpig sluttdato.
- Hvert oppdrag krever eksakt én spesifikk kvalifikasjon (som kan være "ingen").
- Når en kandidat har en kvalifikasjon som stemmer overens med den kvalifikasjonen et bestemt oppdrag krever, og byrået tilsetter kandidaten i den aktuelle jobben, så skal dette registreres i databasen. Etter at oppdraget er utført, skal virkelig start- og sluttdato samt antall timer arbeidet registreres. Disse opplysningene skal framkomme i kandidatens jobbhistorikk.
- Kandidaten skal etter endt oppdrag få en sluttattest. Denne attesten er lik for alle med unntak av data om det spesifikke oppdraget.

___

### Oppgave a)
_Lag en datamodell (ER-modell der dere bruker UML-notasjon)._

![image](https://user-images.githubusercontent.com/31009729/96595920-733ea800-12ec-11eb-9eab-de53688e8c50.png)

___

### Oppgave b)
_Oversett til relasjonsmodellen. Strek under primærnøkler og marker fremmednøkler medstjerne._


**kandidater**(<u>kandidat_id</u>, fornavn, etternavn, telefon, epost)
**kvalifikasjoner**(<u>kvalifikasjon_id</u>, navn)
**kandidat_kvalifikasjoner**(<u>kandidat_id*, kvalifikasjon_id*</u>)
**bedrifter**(<u>org_nr</u>, navn, telefon, epost)
**oppdrag**(<u>oppdrag_nr</u>, bedrift*, _kvalifikasjon*_, startdato, sluttdato, _kandidat*_)
**sluttatester**(<u>kandidat*, oppdrag*</u>)

_Er det rimelig at noen av fremmednøklene kan være NULL? Hva betyr det i tilfelle?_
Ja. I oppdrag for eksempel, må kandidat kunne være `null` frem til en kandidat har blitt valgt for oppdraget. I slik som kandidat_kvalifikasjoner og sluttattester er det derimot ikke naturlig.

___

### Oppgave c)
_Opprett databasetabellene med primær- og fremmednøkler i MySQL, dvs. lag CREATE TABLE og evt. ALTER TABLE-setninger. Bruk datatypen DATE for dato._

```sql
CREATE TABLE kandidater (
    kandidat_id int NOT NULL AUTO_INCREMENT,
    fornavn varchar(50) NOT NULL,
    etternavn varchar(50) NOT NULL,
    telefon varchar(12) NOT NULL,
    epost varchar(50) NOT NULL,
    PRIMARY KEY (kandidat_id)
);

CREATE TABLE kvalifikasjoner (
    kvalifikasjon_id int NOT NULL AUTO_INCREMENT,
    navn varchar(255) NOT NULL,
    PRIMARY KEY (kvalifikasjon_id)
);

CREATE TABLE kandidat_kvalifikasjoner (
    kvalifikasjon_id int NOT NULL,
    kandidat_id int NOT NULL,
    PRIMARY KEY (kvalifikasjon_id, kandidat_id)
    FOREIGN KEY (kvalifikasjon_id) REFERENCES kvalifikasjoner(kvalifikasjon_id),
    FOREIGN KEY (kandidat_id) REFERENCES kandidater(kandidat_id)
);

CREATE TABLE bedrifter (
    org_nr int NOT NULL,
    navn varchar(255) NOT NULL,
    telefon varchar(12) NOT NULL,
    epost varchar(50) NOT NULL,
    PRIMARY KEY (org_nr)
);

CREATE TABLE oppdrag (
    oppdrag_nr int NOT NULL AUTO_INCREMENT,
    bedrift int NOT NULL,
    kvalifikasjon int,
    startdato DATE NOT NULL,
    sluttdato DATE NOT NULL,
    kandidat int,
    PRIMARY KEY (oppdrag_nr),
    FOREIGN KEY (bedrift) REFERENCES bedrifter(org_nr),
    FOREIGN KEY (kvalifikasjon) REFERENCES kvalifikasjoner(kvalifikasjon_id),
    FOREIGN KEY (kandidat) REFERENCES kandidater(kandidat_id)
);

CREATE TABLE sluttatester (
    kandidat int NOT NULL,
    oppdrag int NOT NULL,
    PRIMARY KEY (kandidat, oppdrag),
    FOREIGN KEY (kandidat) REFERENCES kandidater(kandidat_id),
    FOREIGN KEY (oppdrag) REFERENCES oppdrag(oppdrag_nr)
);
```

_Fyller databasen:_
```sql
INSERT INTO kandidater VALUES (null, "Arne", "Arnesen", "99887766", "arne@arnesen.no");
INSERT INTO kandidater VALUES (null, "Egil", "Egilsen", "99887766", "arne@arnesen.no");
INSERT INTO kandidater VALUES (null, "Tom", "Tomsen", "99887766", "arne@arnesen.no");
INSERT INTO kandidater VALUES (null, "Trond", "Trondsen", "99887766", "arne@arnesen.no");

INSERT INTO kvalifikasjoner VALUES (null, "Kul");
INSERT INTO kvalifikasjoner VALUES (null, "Smart");
INSERT INTO kvalifikasjoner VALUES (null, "Ego");

INSERT INTO kandidat_kvalifikasjoner VALUES (1, 1);
INSERT INTO kandidat_kvalifikasjoner VALUES (1, 2);
INSERT INTO kandidat_kvalifikasjoner VALUES (2, 3);

INSERT INTO bedrifter VALUES (313, "Andeby auto", "98798765", "andeby@auto.com");
INSERT INTO bedrifter VALUES (987, "Cola", "98798765", "cola@gmail.com");
INSERT INTO bedrifter VALUES (123, "SAS", "98798765", "sas@mail.com");

INSERT INTO oppdrag VALUES (null, 313, 1, "2020-11-15", "2020-11-25", 2);
INSERT INTO oppdrag VALUES (null, 313, 2, "2020-12-15", "2020-12-25", 2);
INSERT INTO oppdrag VALUES (null, 123, 3, "2020-11-21", "2020-11-25", null);
```

___

### Oppgave d)
_Sett opp SELECT-setninger som gjør følgende:_

1. Lag en liste over alle bedriftene. Navn, telefon og epost til bedriften skal skrives ut.
    ```sql
    SELECT navn, telefon, epost FROM bedrifter;
    ```

2. Lag en liste over alle oppdragene. Om hvert oppdrag skal du skrive ut oppdragets nummer samt navn og telefonnummer til bedriften som tilbyr oppdraget.
    ```sql
    SELECT oppdrag.oppdrag_nr, bedrifter.navn, bedrifter.telefon
      FROM oppdrag
      JOIN bedrifter ON oppdrag.bedrift = bedrifter.org_nr;
    ```

3. Lag en liste over kandidater og kvalifikasjoner. Kandidatnavn og kvalifikasjonsbeskrivelse skal med i utskriften i tillegg til løpenumrene som identifiserer kandidat og kvalifikasjon.
    ```sql
    SELECT kandidater.kandidat_id, kandidater.fornavn, kandidater.etternavn, kvalifikasjoner.kvalifikasjon_id, kvalifikasjoner.navn as "Kvalifikasjon"
      FROM kandidat_kvalifikasjoner
      JOIN kandidater ON kandidat_kvalifikasjoner.kandidat_id = kandidater.kandidat_id
      JOIN kvalifikasjoner ON kvalifikasjoner.kvalifikasjon_id = kandidat_kvalifikasjoner.kvalifikasjon_id;
    ```

4. Som oppgave 3), men få med de kandidatene som ikke er registrert med kvalifikasjoner.
    ```sql
    SELECT kandidater.kandidat_id, kandidater.fornavn, kandidater.etternavn, kvalifikasjoner.kvalifikasjon_id, kvalifikasjoner.navn as "Kvalifikasjon"
      FROM kandidater
      LEFT JOIN kandidat_kvalifikasjoner ON kandidat_kvalifikasjoner.kandidat_id = kandidater.kandidat_id
      LEFT JOIN kvalifikasjoner ON kvalifikasjoner.kvalifikasjon_id = kandidat_kvalifikasjoner.kvalifikasjon_id;
    ```

5. Skriv ut jobbhistorikken til en bestemt vikar, gitt kandidatnr. Vikarnavn, sluttdato, oppdragsnr og bedriftsnavn skal med.
    ```sql
    SELECT kandidater.fornavn, kandidater.etternavn, oppdrag.sluttdato, oppdrag.oppdrag_nr, bedrifter.navn
      FROM oppdrag JOIN kandidater ON oppdrag.kandidat = kandidater.kandidat_id AND oppdrag.kandidat = 2
      JOIN bedrifter ON oppdrag.bedrift = bedrifter.org_nr;
    ```
