import { ReactNode } from 'react';
import MaterialPaper from '@material-ui/core/Paper';
import classnames from 'classnames';
import { makeStyles } from '@material-ui/core/styles';

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
  noBorder: {
    border: 'none',
  },
}));

export type PaperProps = {
  children: ReactNode;
  shadow?: boolean;
  blurred?: boolean;
  noPadding?: boolean;
  className?: string;
};

const Paper = ({ shadow, noPadding, blurred = false, children, className }: PaperProps) => {
  const classes = useStyles();
  return (
    <MaterialPaper
      className={classnames(classes.main, !noPadding && classes.padding, shadow && classes.noBorder, blurred && classes.blurred, className)}
      elevation={shadow ? 2 : 0}>
      {children}
    </MaterialPaper>
  );
};

export default Paper;
