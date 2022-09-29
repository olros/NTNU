import { createMuiTheme } from '@material-ui/core/styles';

export default createMuiTheme({
  breakpoints: {
    values: {
      xs: 0,
      sm: 400,
      md: 600,
      lg: 900,
      xl: 1200,
    },
  },
  typography: {
    fontFamily: '"Krub", "Roboto", "Helvetica", "Arial", sans-serif',
  },
  shape: {
    borderRadius: 16,
  },
  palette: {
    type: 'dark',
    background: {
      default: 'var(--primary-background)',
      paper: 'var(--secondary-background)',
    },
    text: {
      primary: '#ffffff',
      secondary: '#ffffff',
    },
    primary: {
      main: '#cfded5',
      contrastText: '#454a47',
    },
    secondary: {
      main: '#fafaf8',
      contrastText: '#454a47',
    },
    error: {
      main: '#B71C1C',
      contrastText: '#ffffff',
    },
  },
});
