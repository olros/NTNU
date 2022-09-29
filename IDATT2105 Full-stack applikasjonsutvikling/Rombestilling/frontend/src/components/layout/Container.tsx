import { forwardRef, Ref } from 'react';
import classnames from 'classnames';

// Material UI Components
import { Container as MuiContainer, makeStyles, ContainerProps } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
  container: {
    paddingTop: theme.spacing(2),
    paddingBottom: theme.spacing(2),
    [theme.breakpoints.down('md')]: {
      paddingRight: theme.spacing(2),
      paddingLeft: theme.spacing(2),
    },
  },
}));

const Container = forwardRef(function Container({ className, children, maxWidth = 'lg', ...props }: ContainerProps, ref: Ref<HTMLDivElement>) {
  const classes = useStyles();
  return (
    <MuiContainer className={classnames(classes.container, className)} maxWidth={maxWidth} ref={ref} {...props}>
      {children}
    </MuiContainer>
  );
});

export default Container;
