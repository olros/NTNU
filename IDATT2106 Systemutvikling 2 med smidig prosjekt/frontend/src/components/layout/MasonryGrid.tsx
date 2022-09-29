// Material UI Components
import { makeStyles, useTheme } from '@material-ui/core/styles';
import { ReactNode } from 'react';

// Project Components
import Masonry, { MasonryProps } from 'react-masonry-css';

const useStyles = makeStyles((theme) => ({
  myMasonryGrid: {
    display: 'flex',
    marginLeft: theme.spacing(-1),
    width: 'auto',
    marginBottom: theme.spacing(1),
    marginTop: theme.spacing(1),
  },
  myMasonryGridColumn: {
    paddingLeft: theme.spacing(1),
    backgroundClip: 'padding-box',
  },
}));

export type MasonryGridProps = {
  children: ReactNode;
  breakpoints?: MasonryProps['breakpointCols'];
};

export default function MasonryGrid(props: MasonryGridProps) {
  const classes = useStyles();
  const theme = useTheme();
  const breakpointColumnsObj = {
    default: 3,
    [theme.breakpoints.values.lg]: 2,
    [theme.breakpoints.values.md]: 1,
  };
  return (
    <Masonry breakpointCols={props.breakpoints || breakpointColumnsObj} className={classes.myMasonryGrid} columnClassName={classes.myMasonryGridColumn}>
      {props.children}
    </Masonry>
  );
}
