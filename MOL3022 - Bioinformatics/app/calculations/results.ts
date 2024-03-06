import type { MatrixDetails } from '~/types';

// Retrived from Chapter 4
// https://www.bioconductor.org/packages/devel/bioc/vignettes/universalmotif/inst/doc/IntroductionToSequenceMotifs.pdf
const frequencyToWeight = (freq: number, total: number): number => {
  const bg = 0.25;
  const p = (freq + 1 * 0.25) / (total + 1);
  return Math.log2(p / bg);
};

const sumArrays = (x: number[], y: number[]) => x.map((val, i) => val + y[i]);

/**
 * @param matrix A matrix from Jaspar
 * @param bg A background distribution for the bases
 * @returns A new Position Weight Matrix for the transcription factor
 */
const getPwmOfMatrix = (matrix: MatrixDetails): Record<string, number[]> => {
  const pfm = matrix.pfm;
  const arr = [pfm.A, pfm.G, pfm.C, pfm.T];

  const total = [...arr].reduce((prev, cur) => sumArrays(prev, cur))[0];

  const newRow: number[][] = [];
  arr.forEach((row, i) => {
    newRow.push([]);
    row.forEach((elem) => {
      const newElem = frequencyToWeight(elem, total);
      newRow[i].push(newElem);
    });
  });
  const newPfm: Record<string, number[]> = {};
  Object.keys(pfm).forEach((key, i) => {
    newPfm[key] = newRow[i];
  });

  return newPfm;
};

/**
 * @param pwm A a position weight matrix for a given motif
 * @param sequence A DNA sequence
 * @returns A probability of the motif at all given positions in the sequence
 */
const getSequenceProbabilityFromPwm = (pwm: Record<string, number[]>, sequence: Array<keyof MatrixDetails['pfm']>): number[] => {
  const length_of_motif = pwm.A.length;
  const prob: Array<number> = Array(sequence.length - length_of_motif + 1).fill(0);

  for (let index = 0; index < sequence.length - length_of_motif + 1; index++) {
    let seq_score = 0;
    for (let j = index; j < index + length_of_motif; j++) {
      seq_score += pwm[sequence[j]][j - index];
    }
    prob[index] = seq_score;
  }

  return prob;
};

/**
 * @param matrix A binding matrix
 * @param sequences A list of sequences to search
 * @param bg A background distribution for the bases
 * @returns Scores for the binding motif, in each sequence
 */
export const calculateSeq = (matrix: MatrixDetails, sequences: Array<string>): number[][] => {
  const pwm = getPwmOfMatrix(matrix);
  const matrixes: number[][] = sequences.map((sequence) =>
    getSequenceProbabilityFromPwm(pwm, sequence.trim().toUpperCase() as unknown as Array<keyof MatrixDetails['pfm']>),
  );
  return matrixes;
};
