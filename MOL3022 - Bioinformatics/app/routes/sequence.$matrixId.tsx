import { Box, Button, Card, FormControl, FormHelperText, FormLabel, styled, Textarea, Typography } from '@mui/joy';
import Table from '@mui/joy/Table';
import type { LoaderArgs } from '@remix-run/node';
import { defer, redirect } from '@remix-run/node';
import { Form, Link, useLoaderData } from '@remix-run/react';
import ky from 'ky';
import invariant from 'tiny-invariant';

import type { MatrixDetails } from '~/types';

export { ErrorBoundary } from '~/components/ErrorBoundary';

const Grid = styled('div')(({ theme }) => ({
  display: 'grid',
  gridTemplateColumns: '1fr 2fr',
  gridTemplateRows: 'auto 1fr',
  padding: theme.spacing(2),
  height: '100vh',
  gap: theme.spacing(2),
}));

export const loader = async ({ params }: LoaderArgs) => {
  invariant(params.matrixId, 'Missing params.matrixId');
  try {
    const matrix = await ky.get(`https://jaspar.genereg.net/api/v1/matrix/${params.matrixId}/?format=json`).json<MatrixDetails>();
    return defer({ matrix });
  } catch (e) {
    console.error(e);
    throw redirect('/');
  }
};

export default function SequenceConfiguration() {
  const { matrix } = useLoaderData<typeof loader>();
  const tableRows = [
    ['ID', matrix.matrix_id],
    ['Name', matrix.name],
    ['Class', matrix.class.join(', ')],
    ['Species', matrix.species.map((s) => s.name).join(', ')],
    ['Families', matrix.family.join(', ')],
    ['Type', matrix.type],
  ];
  return (
    <Grid>
      <Card sx={{ gridColumn: 'span 2' }} variant='outlined'>
        <Typography level='h1' textAlign='center'>
          Matrix: {matrix.name}
        </Typography>
      </Card>
      <Card variant='outlined'>
        <Typography level='h2'>About</Typography>
        <Table sx={{ mt: 2, '& tbody td:nth-child(1)': { width: '25%', fontWeight: 'bold' } }}>
          <tbody>
            {tableRows.map((row, i) => (
              <tr key={i}>
                <td>{row[0]}</td>
                <td>{row[1]}</td>
              </tr>
            ))}
          </tbody>
        </Table>
        <Box
          alt='Sequence logo'
          component='img'
          src={`https://jaspar.genereg.net/static/logos/all/svg/${matrix.matrix_id}.svg`}
          sx={{ width: '100%', mt: 1 }}
        />
        <Button component={Link} sx={{ mt: 'auto' }} to='/' variant='outlined'>
          Cancel
        </Button>
      </Card>
      <Card sx={{ overflow: 'auto' }} variant='outlined'>
        <Typography level='h2'>Configure</Typography>
        <Form action={`/results/${matrix.matrix_id}`} method='post'>
          <FormControl required sx={{ mb: 2 }}>
            <FormLabel>DNA sequence(s)</FormLabel>
            <Textarea
              defaultValue={
                process.env.NODE_ENV === 'development'
                  ? 'cccaggcaacaatgagggaaacaggacggttataatctacttaaagcagcatcgggccactttcctctgcgagttcgtggacgagggatatagaatttcagtcttgctcctccccgtcactcctggaggacctagtcggcgacatgtcgcgggtccggcaaggctgtagggatgactgtctgatacgtttatgagagggtgaaacactacgccgcggctgataccagtaactgttgcccttcctgtcaatacgtcccgagcttccgtacctattcgccatacaaccttgcgctttttcagccccctcgtagggtgaccaatactccacaagggccttaactcagagagcgcgtaatatagaaactccgtcgactaagtaacccgtatctgttgccacaactcctctggagggttagttcccggaagttagaccttgtaatgaagagcaccctccctctcgtaagcatatttgcgttcgagaatgtaaatcatgcgcatcacctcccgcaggaggggctctttggaattgaacgccttctgggccaggggcggtcaatcttgcgcagtggtacctgtgccaacaacggtacgggcctggatcttgggatctatgtgacgtgggagagttgcatgagaaaacgaggccaacggatgtgccattcggcgcggcctcacggctgtttcacgggtggctcgttcctccgatcttgttcgacctatgtgtgtatctccgttatcacttagtgataggtggttcaatgtcgcttttctttattgtgggatgtaaggggaaacaatgacgcgactacctcgacgatacgctctctgaatgcagccaggctgactttagtttgcgatgacatcctcacataccatcccacatgtgatggccagtctcgaccccccccgtggcaccctctactattcatcacaatccgcgcacagatgacctcgcataaggggctagtcgcttacggcggaggagtggttagtt'
                  : ''
              }
              minRows={10}
              name='sequence'
              placeholder='DNA sequence(s)'
            />
            <FormHelperText>Split sequences by comma if more than one</FormHelperText>
          </FormControl>
          <Button sx={{ width: '100%' }} type='submit'>
            Analyze
          </Button>
        </Form>
      </Card>
    </Grid>
  );
}
