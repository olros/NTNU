import { Box, Button, Card, styled, Typography } from '@mui/joy';
import type { ActionArgs } from '@remix-run/node';
import { redirect } from '@remix-run/node';
import { Link, useActionData, useNavigate } from '@remix-run/react';
import { calculateSeq } from '~/calculations/results';
import ky from 'ky';
import { Scatter } from 'react-chartjs-2';
import invariant from 'tiny-invariant';
import { useTimeout } from 'usehooks-ts';

import type { MatrixDetails } from '~/types';

export { ErrorBoundary } from '~/components/ErrorBoundary';

const Grid = styled('div')(({ theme }) => ({
  display: 'grid',
  gridAutoFlow: 'row',
  padding: theme.spacing(2),
  gap: theme.spacing(2),
}));

const VALID_DNA_CHARS = new RegExp(/^[ACGT,]*$/);

export const action = async ({ request, params }: ActionArgs) => {
  invariant(params.matrixId, 'Missing params.matrixId');
  const matrix = await ky.get(`https://jaspar.genereg.net/api/v1/matrix/${params.matrixId}/?format=json`).json<MatrixDetails>();
  const formData = await request.formData();
  const sequence = formData.get('sequence') as string | null;

  if (!sequence || !VALID_DNA_CHARS.test(sequence.replace(/[^0-9a-zA-Z,]/g, '').toUpperCase())) {
    throw redirect(`/sequence/${params.matrixId}`);
  }

  const sequences = sequence.replace(/[^0-9a-zA-Z,]/g, '').split(',');

  const seq = calculateSeq(matrix, sequences);

  return { seq, sequences };
};

export default function Results() {
  const actionData = useActionData<typeof action>();
  const navigate = useNavigate();

  useTimeout(() => {
    if (!actionData) {
      navigate('/');
    }
  }, 3000);

  if (!actionData) {
    return null;
  }

  const { seq, sequences } = actionData;

  return (
    <Grid>
      <Card orientation='horizontal' sx={{ justifyContent: 'space-between' }} variant='outlined'>
        <Typography level='h1' textAlign='center'>
          Results
        </Typography>
        <Button component={Link} to='/' variant='outlined'>
          Try again
        </Button>
      </Card>
      {seq.map((sequence, i) => (
        <Box key={i} sx={{ overflow: 'auto', display: 'grid', maxHeight: '75vh', width: '100%', gap: 1, gridTemplateColumns: '1fr 2fr' }}>
          <Card sx={{ overflow: 'auto' }} variant='outlined'>
            <Typography gutterBottom level='h2'>
              DNA-sequence
            </Typography>
            <Card component={Typography} key={i} level='body2' sx={{ overflow: 'auto', fontFamily: 'monospace', wordBreak: 'break-all' }} variant='outlined'>
              {sequences[i]}
            </Card>
          </Card>
          <Card sx={{ overflow: 'auto' }} variant='outlined'>
            <Typography level='h2'>Likely transcription factor binding sites</Typography>
            <Box sx={{ height: 400 }}>
              <Typography gutterBottom level='body1'>{`The probability is above 0 at the positions: ${
                sequence
                  .map((val, i) => ({ val, i }))
                  .filter((d) => d.val > 0)
                  .map((d) => d.i)
                  .join(', ') || 'none'
              }`}</Typography>
              <Scatter
                data={{
                  datasets: [
                    {
                      backgroundColor: sequence.map((val) => (val > 0 ? 'lightgreen' : 'rgba(31,188,209,0.5)')),
                      data: sequence.map((y, x) => ({ x, y })),
                    },
                  ],
                }}
                height={100}
                key={i}
                options={{
                  pan: {
                    enabled: true,
                    mode: 'xy',
                  },
                  zoom: {
                    enabled: true,
                    mode: 'x',
                  },
                  legend: {
                    display: false,
                  },
                  maintainAspectRatio: false,
                  scales: {
                    xAxes: [
                      {
                        scaleLabel: {
                          display: true,
                          labelString: 'Position',
                        },
                      },
                    ],
                    yAxes: [
                      {
                        ticks: {
                          suggestedMin: 5,
                          suggestedMax: 5,
                        },
                        scaleLabel: {
                          display: true,
                          labelString: 'Score',
                        },
                      },
                    ],
                  },
                }}
                width={100}
              />
            </Box>
          </Card>
        </Box>
      ))}
    </Grid>
  );
}
