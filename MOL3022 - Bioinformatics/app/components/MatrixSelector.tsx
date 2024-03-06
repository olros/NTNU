import { Button, Card, CardContent, Divider, Typography } from '@mui/joy';
import { Link } from '@remix-run/react';

import type { Matrix } from '~/types';

export type MatrixSelectorProps = {
  matrix: Matrix;
};

export const MatrixSelector = ({ matrix }: MatrixSelectorProps) => (
  <Card orientation='horizontal' sx={{ bgcolor: 'background.body', p: 1, pl: 2 }} variant='outlined'>
    <CardContent>
      <Typography fontWeight='md' textColor='success.plainColor'>
        {`Name: ${matrix.name}`}
      </Typography>
      <Typography level='body2'>{`ID: ${matrix.matrix_id}`}</Typography>
    </CardContent>
    <Divider />
    <Button component={Link} sx={{ ml: 1 }} to={`/sequence/${matrix.matrix_id}`} variant='outlined'>
      Use
    </Button>
  </Card>
);
