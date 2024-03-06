import { Card, FormControl, FormLabel, Input, Stack, styled, Typography } from '@mui/joy';
import { json } from '@remix-run/node';
import { useLoaderData } from '@remix-run/react';
import { MatrixSelector } from '~/components/MatrixSelector';
import ky from 'ky';
import { useState } from 'react';

import type { Matrix, PaginationResponse } from '~/types';

export { ErrorBoundary } from '~/components/ErrorBoundary';

const Grid = styled('div')(({ theme }) => ({
  display: 'grid',
  gridTemplateColumns: '1fr 2fr',
  gridTemplateRows: 'auto 1fr',
  padding: theme.spacing(2),
  height: '100vh',
  gap: theme.spacing(2),
}));

const getMatricesAtPage = (page: number) =>
  ky
    .get(`https://jaspar.genereg.net/api/v1/matrix?format=json&page_size=1000&page=${page}`)
    .json<PaginationResponse<Matrix>>()
    .then((d) => d.results);

export const loader = async () => {
  try {
    const matrices = await getMatricesAtPage(1);
    // const matrices = await Promise.all([getMatricesAtPage(1), getMatricesAtPage(2), getMatricesAtPage(3)]).then(([matrice1, matrice2, matrice3]) => [
    //   ...matrice1,
    //   ...matrice2,
    //   ...matrice3,
    // ]);
    return json({ matrices });
  } catch (e) {
    console.error(e);
    return json({ matrices: [] });
  }
};

export default function Index() {
  const { matrices } = useLoaderData<typeof loader>();
  const [search, setSearch] = useState('');

  return (
    <Grid>
      <Card sx={{ gridColumn: 'span 2' }} variant='outlined'>
        <Typography level='h1' textAlign='center'>
          Mapping known transcription factor binding sites
        </Typography>
      </Card>
      <Card variant='outlined'>
        <Typography gutterBottom level='h2'>
          About
        </Typography>
        <Typography>
          This is a tool that can use data on a transcription factors from Jaspar http://jaspar.genereg.net/, scan a DNA sequence, and identify the most likely
          transcription factor binding sites in the DNA sequence. The results are drawn both as a graph showing the score at each position along the sequence,
          and with specific indexes of the DNA sequence showing the most likely binding sites.
        </Typography>
      </Card>
      <Card sx={{ overflow: 'hidden' }} variant='outlined'>
        <Typography gutterBottom level='h2'>
          Matrices
        </Typography>
        <FormControl>
          <FormLabel>Search</FormLabel>
          <Input onChange={(e) => setSearch(e.target.value)} placeholder='Search for matrix' value={search} />
        </FormControl>
        {matrices !== undefined ? (
          <Stack gap={1} sx={{ mt: 1, overflowY: 'auto', width: '100%', height: '100%' }}>
            {matrices
              .filter(
                (matrix) =>
                  !search.trim() ||
                  matrix.name.toUpperCase().includes(search.trim().toUpperCase()) ||
                  matrix.matrix_id.toUpperCase().includes(search.trim().toUpperCase()),
              )
              .map((matrix) => (
                <MatrixSelector key={matrix.matrix_id} matrix={matrix} />
              ))}
          </Stack>
        ) : (
          <p>Loading matrices...</p>
        )}
      </Card>
    </Grid>
  );
}
