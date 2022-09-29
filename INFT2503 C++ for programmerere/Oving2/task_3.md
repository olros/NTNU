# Oppgave 3

2 ting kan gå galt:

1. Når man setter verdien til `text` ved input: `cin << text` så sjekker man ikke at input er maks like lang som det `text` har plass til, dermed kan bokstavene etter posisjon 5 gå utover minneområdet som er satt av til `text`
2. Når vi bruker `while` til å gå gjennom `text` for å finne bokstaven "e", så sjekkes det ikke om vi har nådd enden på strengen, dermed kan vi også her gå utover minneområdet og prøve å få tilgang til minne man ikke skal ha tilgang til.