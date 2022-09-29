/**
 * 
 * Copyright (C) 2021 Olaf Rosendahl
 * 
 */

const express = require('express')
const cookieParser = require('cookie-parser');
const app = express()
const port = 3000

app.use(cookieParser('my secret here'))

app.get('/mobil/tallspill.jsp', (req, res) => {
	const FERDIG = "Beklager ingen flere sjanser, du må starte på nytt (registrer kortnummer og navn)";
  const navn = req.query.navn;
  if (navn != null) {
    const kortnummer = req.query.kortnummer;
		if (kortnummer == null){
			res.send("Feil, kortnummer er ikke oppgitt!");
			return;
		}
		res.cookie("navn",navn);
		res.cookie("kortnummer", kortnummer);
		res.cookie("teller", 0);
		res.cookie("riktigTall", Math.floor(Math.random() * 10) + 1);
		res.send("Oppgi et tall mellom 1 og 10!");
		return;
  }
  const riktigTallString = req.cookies.riktigTall;
  if (riktigTallString == null){ 
    res.send("Du har glemt å støtte cookies, eller du har ikke oppgit parameterene navn og kortnummer i første forespørsel!!!");
    return;
  }
  
  const riktigTall = Number(riktigTallString);
  
  const ganger = Number(req.cookies.teller);
  let teller = ganger;
  if (ganger == null){
    res.send("Feil, du må registrer navn og kortnummer før du kan tippe (via start nytt spill)");
    return;
  }
	if (ganger > 2) {
		res.send(FERDIG);
	}else{
		const tall = req.query.tall;
		console.log("tall " + tall);
		const verdi = Number(tall);
		teller++;
		res.cookie("teller", teller);
		let str = "";
		if (verdi == riktigTall){
			const n = req.cookies.navn;
			const k = req.cookies.kortnummer;
			str = n +", du har vunnet 100 kr som kommer inn på ditt kort " + k;
		}else if (verdi < riktigTall){
			str = "Tallet " + tall + " er for lite! ";
			if (teller == 3) str += FERDIG;
		}else{
			str = "Tallet " + tall + " er for stort! ";
			if (teller == 3) str += FERDIG;
		}
    console.log(`--${str}--`)
		res.send(str);
	}
})

app.listen(port, () => {
  console.log(`Tallspill app is listening at http://localhost:${port}`)
})
