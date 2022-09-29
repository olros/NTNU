import { ReactNode } from 'react';
import classnames from 'classnames';
import { Paper as MaterialPaper, makeStyles } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
  main: {
    overflow: 'hidden',
    ...theme.palette.transparent,
  },
  blurred: {
    ...theme.palette.blurred,
  },
  padding: {
    padding: theme.spacing(3),
  },
  border: {
    border: `${theme.palette.borderWidth} solid ${theme.palette.divider}`,
  },
}));

export type PaperProps = {
  children: ReactNode;
  border?: boolean;
  blurred?: boolean;
  noPadding?: boolean;
  className?: string;
};

const Paper = ({ border, noPadding, blurred = false, children, className }: PaperProps) => {
  const classes = useStyles();
  return (
    <MaterialPaper
      className={classnames(classes.main, !noPadding && classes.padding, border && classes.border, blurred && classes.blurred, className)}
      elevation={0}>
      {children}
    </MaterialPaper>
  );
};

export default Paper;
