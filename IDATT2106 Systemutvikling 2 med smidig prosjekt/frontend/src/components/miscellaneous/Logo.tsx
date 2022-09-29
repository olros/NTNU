import classnames from 'classnames';

// Material UI Components
import { makeStyles, Theme, useTheme } from '@material-ui/core/styles';
import { useMemo } from 'react';

const useStyles = makeStyles<Theme, { fill: string }>((theme) => ({
  logo: {
    margin: 'auto',
    marginTop: theme.spacing(0.5),
    display: 'flex',
    height: '66%',
    width: '66%',
    enableBackground: 'new 0 0 143.93 50.69',
    fill: (props) => props.fill,
  },
}));

type LogoProps = {
  className?: string;
  darkColor: 'white' | 'black';
  lightColor: 'white' | 'black';
};

const Logo = ({ className, darkColor, lightColor }: LogoProps) => {
  const theme = useTheme();
  const color = useMemo(() => {
    const isDark = theme.palette.mode === 'dark';
    const prop = isDark ? darkColor : lightColor;
    if (prop === 'black') {
      return theme.palette.common.black;
    } else {
      return theme.palette.common.white;
    }
  }, [theme.palette, darkColor, lightColor]);
  const classes = useStyles({ fill: color });

  return (
    <svg
      aria-label='GIDD Logo'
      className={classnames(classes.logo, className)}
      id='Logo'
      version='1.1'
      viewBox='0 0 143.93 50.69'
      x='0px'
      xmlns='http://www.w3.org/2000/svg'
      xmlSpace='preserve'
      y='0px'>
      <path
        d='M21.82,22.61h14.47l-6.12,26.21c-2.09,0.79-7.34,1.87-13.97,1.87C5.61,50.69,0,45.58,0,35.28C0,16.99,8.14,0,26.14,0
	c5.76,0,11.95,1.66,14.98,2.88l-2.3,7.2c-3.1-0.65-8.86-1.44-13.46-1.44c-10.94,0-15.12,15.63-15.12,26.71c0,4.82,2.88,6.7,7.2,6.7
	c1.8,0,3.67-0.22,4.54-0.36l2.52-10.87h-4.61L21.82,22.61z M49.53,0.86L38.23,49.83h9.94L59.47,0.86H49.53z M100.37,15.77
	c0,16.27-7.71,34.06-25.27,34.06H57.52L68.83,0.86h15.41C92.74,0.86,100.37,4.03,100.37,15.77z M90.14,15.77
	c0-4.9-3.03-6.26-7.92-6.26h-5.47l-7.27,31.68h6.48C86.33,41.18,90.14,24.69,90.14,15.77z M143.93,15.77
	c0,16.27-7.7,34.06-25.27,34.06h-17.57l11.31-48.96h15.41C136.3,0.86,143.93,4.03,143.93,15.77z M133.7,15.77
	c0-4.9-3.02-6.26-7.92-6.26h-5.47l-7.27,31.68h6.48C129.89,41.18,133.7,24.69,133.7,15.77z'
      />
    </svg>
  );
};
export default Logo;
