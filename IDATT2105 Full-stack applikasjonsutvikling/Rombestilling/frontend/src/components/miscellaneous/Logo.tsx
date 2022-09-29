import classnames from 'classnames';
import { makeStyles } from '@material-ui/core/styles';
import LOGO from 'assets/img/logo.png';

const useStyles = makeStyles(() => ({
  logo: {
    margin: 'auto',
    height: '100%',
    width: '100%',
    objectFit: 'contain',
  },
}));

type LogoProps = {
  className?: string;
};

const Logo = ({ className }: LogoProps) => {
  const classes = useStyles();

  return <img alt='Logo' className={classnames(classes.logo, className)} src={LOGO} />;
};
export default Logo;
