# IDATT2021 AlgDat

Øvinger i IDATT2021 (Algoritmer og Datastrukturer) skrevet i hovedsak i Java

## Filer brukt:

- **[Øving 1](http://www.iie.ntnu.no/fag/_alg/rekursjon/rek_opg_bb.pdf)**

- **[Øving 2](https://ait.idi.ntnu.no/fag/_alg/sortering/sortering2020.pdf)**
    
- **[Øving 3](https://ait.idi.ntnu.no/fag/_alg/liste-k%C3%B8-stakk/opg03-2020.pdf)**

- **[Øving 4](https://ait.idi.ntnu.no/fag/_alg/hash/opg-hash.pdf)**:
    - http://www.iie.ntnu.no/fag/_alg/hash/navn
    
- **[Øving 5](https://ait.idi.ntnu.no/fag/_alg/uv-graf/opg-uv-graf-2020.pdf)**:
    - http://www.iie.ntnu.no/fag/_alg/uv-graf/L7g1
    - http://www.iie.ntnu.no/fag/_alg/uv-graf/L7g6
    - http://www.iie.ntnu.no/fag/_alg/uv-graf/L7g2
    - http://www.iie.ntnu.no/fag/_alg/uv-graf/L7g5
    - http://www.iie.ntnu.no/fag/_alg/uv-graf/L7Skandinavia
    - http://www.iie.ntnu.no/fag/_alg/uv-graf/L7Skandinavia-navn

- **[Øving 6](https://ait.idi.ntnu.no/fag/_alg/v-graf/opg-v-graf-2020.pdf)**:
    - http://www.iie.ntnu.no/fag/_alg/v-graf/vg1
    - http://www.iie.ntnu.no/fag/_alg/v-graf/vg5
    - http://www.iie.ntnu.no/fag/_alg/v-graf/vg2
    - http://www.iie.ntnu.no/fag/_alg/v-graf/vg3
    - http://www.iie.ntnu.no/fag/_alg/v-graf/vg4
    - http://www.iie.ntnu.no/fag/_alg/v-graf/vgSkandinavia
    - http://www.iie.ntnu.no/fag/_alg/uv-graf/L7Skandinavia-navn

- **[Øving 7](https://ait.idi.ntnu.no/fag/_alg/kompr/opg7-2020.pdf)**:
    - http://www.iie.ntnu.no/fag/_alg/kompr/opg7-2020.pdf
    - http://www.iie.ntnu.no/fag/_alg/kompr/diverse.pdf
    - http://www.iie.ntnu.no/fag/_alg/kompr/diverse.txt
    - http://www.iie.ntnu.no/fag/_alg/kompr/diverse.lyx
     - http://www.iie.ntnu.no/fag/_alg/kompr/enwik8


- **[Øving 8](https://ait.idi.ntnu.no/fag/_alg/Astjerne/opg8.pdf)**:
    - http://www.iie.ntnu.no/fag/_alg/Astjerne/opg/norden/noder.txt
    - http://www.iie.ntnu.no/fag/_alg/Astjerne/opg/norden/kanter.txt
    - http://www.iie.ntnu.no/fag/_alg/Astjerne/opg/norden/interessepkt.txt

### Kompilere C med WSL
For å kompilere og kjøre C med WSL (Windows Subsystem for Linux), kjøre disse kommandoene i CMD:

```
wsl

// Option 1:
gcc [Filename].c [linking flags] && ./a.out

// Option 2:
gcc -o [OutputFilename] [Filename].c [linking flags]
./[OutputFilename]
```

Du må laste ned `gcc` for at dette skal virke. Sjekk om du har installert `gcc` ved å skrive: `wsl gcc --version`

#### Linking flags eksempler
- `<math.h>`: `-lm`:
