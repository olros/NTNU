import { ReactNode } from 'react';

// Material UI Components
import { makeStyles, Button, ButtonProps } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
  button: {
    marginTop: theme.spacing(1),
    padding: theme.spacing(1),
  },
}));

export type PaginationProps = ButtonProps & {
  fullWidth?: boolean;
  children?: ReactNode;
  nextPage: () => void;
  hasNextPage?: boolean | string | number | null;
  isLoading?: boolean;
};

const Pagination = ({ children, fullWidth, isLoading, nextPage, hasNextPage, ...props }: PaginationProps) => {
  const classes = useStyles();
  return (
    <>
      <div>{children}</div>
      {hasNextPage && !isLoading && (
        <Button className={classes.button} fullWidth={fullWidth} onClick={nextPage} variant='outlined' {...props}>
          Vis flere elementer
        </Button>
      )}
    </>
  );
};

export default Pagination;
