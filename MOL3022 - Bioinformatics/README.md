# MOL3022 Bioinformatics - Project

## Mapping known transcription factor binding sites

This is a tool that can use data on a transcription factors from Jaspar http://jaspar.genereg.net/, scan a DNA sequence, and identify the most likely transcription factor binding sites in the DNA sequence. The results are drawn both as a graph showing the score at each position along the sequence, and with specific indexes of the DNA sequence showing the most likely binding sites.

## Run locally

In addition to running the application locally, the application is also available at https://mol3022.vercel.app/ (may take some time loading due to the amount of matrices to load from JASPAR).

- Ensure Node.js version 16>= is installed
- Install dependencies: `yarn` or `npm i`
- Run application: `yarn dev` or `npm run dev`
- Open http://localhost:3000/ in a browser to use application
