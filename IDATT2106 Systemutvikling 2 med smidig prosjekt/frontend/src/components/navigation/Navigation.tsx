import { ReactNode, ReactElement } from 'react';
import Helmet from 'react-helmet';
import classnames from 'classnames';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import LinearProgress from '@material-ui/core/LinearProgress';

// Project Components
import Footer from 'components/navigation/Footer';
import Topbar, { TopbarProps } from 'components/navigation/Topbar';
import Container from 'components/layout/Container';

const useStyles = makeStyles((theme) => ({
  main: {
    minHeight: '101vh',
  },
  normalMain: {
    paddingTop: 64,
    [theme.breakpoints.down('sm')]: {
      paddingTop: 56,
    },
  },
}));

export type NavigationProps = {
  children?: ReactNode;
  banner?: ReactElement;
  maxWidth?: false | 'xs' | 'sm' | 'md' | 'lg' | 'xl';
  isLoading?: boolean;
  noFooter?: boolean;
  topbarVariant?: TopbarProps['variant'];
};

const Navigation = ({ isLoading = false, noFooter = false, maxWidth, banner, children, topbarVariant = 'transparent' }: NavigationProps) => {
  const classes = useStyles();

  return (
    <>
      <Helmet>
        <title>GIDD - Det er bare å gidde</title>
      </Helmet>
      <Topbar variant={topbarVariant} />
      <main className={classnames(classes.main, topbarVariant !== 'transparent' && classes.normalMain)}>
        {isLoading && <LinearProgress />}
        {banner}
        {maxWidth === false ? <>{children}</> : <Container maxWidth={maxWidth || 'xl'}>{children || <></>}</Container>}
      </main>
      {!noFooter && !isLoading && <Footer />}
    </>
  );
};

export default Navigation;
