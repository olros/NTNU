import { createMuiTheme } from '@material-ui/core/styles';
import darkScrollbar from '@material-ui/core/darkScrollbar';

// Icons
import DarkIcon from '@material-ui/icons/Brightness2Outlined';
import AutomaticIcon from '@material-ui/icons/DevicesOutlined';
import LightIcon from '@material-ui/icons/WbSunnyOutlined';

declare module '@material-ui/core/styles/createPalette' {
  interface Palette {
    borderWidth: string;
    get: <T>({ light, dark }: { light: T; dark: T }) => T;
    blurred: {
      backdropFilter: string;
      '-webkit-backdrop-filter': string;
    };
    transparent: {
      boxShadow: string;
      border: string;
      background: string;
    };
    colors: {
      topbar: string;
      gradient: string;
    };
  }

  interface PaletteOptions {
    borderWidth: string;
    get: <T>({ light, dark }: { light: T; dark: T }) => T;
    blurred: {
      backdropFilter: string;
      '-webkit-backdrop-filter': string;
    };
    transparent: {
      boxShadow: string;
      border: string;
      background: string;
    };
    colors: {
      topbar: string;
      gradient: string;
    };
  }
}

export const themesDetails = [
  { key: 'light', name: 'Lyst', icon: LightIcon },
  { key: 'automatic', name: 'Automatisk', icon: AutomaticIcon },
  { key: 'dark', name: 'Mørkt', icon: DarkIcon },
] as const;
export const themes = themesDetails.map((theme) => theme.key);
export type ThemeTypes = typeof themes[number];

export const getTheme = (theme: ThemeTypes, prefersDarkMode: boolean) => {
  // eslint-disable-next-line comma-spacing
  const get = <T,>({ light, dark }: { light: T; dark: T }): T => {
    switch (theme) {
      case 'automatic':
        return prefersDarkMode ? dark : light;
      case 'dark':
        return dark;
      default:
        return light;
    }
  };

  return createMuiTheme({
    breakpoints: {
      values: {
        xs: 0,
        sm: 400,
        md: 650,
        lg: 900,
        xl: 1200,
      },
    },
    components: {
      MuiTypography: {
        styleOverrides: {
          root: {
            wordBreak: 'break-word',
          },
        },
      },
      MuiAvatar: {
        styleOverrides: {
          root: {
            background: get<string>({ light: '#475960', dark: '#bddde5' }),
            color: get<string>({ light: '#ffffff', dark: '#000000' }),
            fontWeight: 'bold',
          },
        },
      },
      MuiCssBaseline: {
        styleOverrides: {
          body: {
            background: get<string>({ light: 'linear-gradient(45deg, #3e843142, #0000ff3d)', dark: 'linear-gradient(45deg, #160202d9, #070727ed)' }),
            // eslint-disable-next-line @typescript-eslint/ban-types
            ...get<object>({ light: {}, dark: darkScrollbar() }),
          },
          '@global': {
            html: {
              WebkitFontSmoothing: 'auto',
            },
          },
          a: {
            color: get<string>({ light: '#1D448C', dark: '#9ec0ff' }),
          },
        },
      },
      MuiButton: {
        defaultProps: {
          variant: 'contained',
        },
        styleOverrides: {
          root: {
            height: 50,
            fontWeight: get<number | undefined>({ light: 500, dark: 400 }),
          },
          contained: {
            boxShadow: 'none',
          },
        },
      },
    },
    palette: {
      get,
      mode: get<'light' | 'dark'>({ light: 'light', dark: 'dark' }),
      primary: {
        main: get<string>({ light: '#475960', dark: '#bddde5' }),
        contrastText: get<string>({ light: '#ffffff', dark: '#000000' }),
      },
      secondary: {
        main: get<string>({ light: '#4f5643', dark: '#ffa2cb' }),
      },
      error: {
        main: get<string>({ light: '#F71735', dark: '#ff6060' }),
      },
      text: {
        primary: get<string>({ light: '#000000', dark: '#ffffff' }),
        secondary: get<string>({ light: '#333333', dark: '#cccccc' }),
      },
      blurred: {
        backdropFilter: `blur(5px)`,
        '-webkit-backdrop-filter': `blur(5px)`,
      },
      transparent: {
        background: get<string>({ light: '#f6f5f340', dark: '#61616160' }),
        border: get<string>({ light: '1px solid #d7d7d75c', dark: '1px solid #4545453b' }),
        boxShadow: `0 8px 32px 0 ${get<string>({ light: '#cab2e7', dark: '#26292d' })}52`,
      },
      borderWidth: '1px',
      background: {
        default: get<string>({ light: '#FDFFFC', dark: '#121212' }),
        paper: get<string>({ light: '#f6f5f3', dark: '#232323' }),
      },
      colors: {
        topbar: get<string>({ light: '#cecef5', dark: '#26292d' }),
        gradient: get<string>({ light: 'linear-gradient(to bottom, #adbcdf82, #abc8c073)', dark: 'linear-gradient(to bottom, #160202ab, #07072769)' }),
      },
    },
    shape: {
      borderRadius: 10,
    },
    typography: {
      h1: {
        fontSize: '3.1rem',
        fontFamily: 'Playfair Display, Roboto, sans-serif',
        fontWeight: 800,
      },
      h2: {
        fontFamily: 'Cabin, Roboto, sans-serif',
        fontSize: '2.2rem',
        fontWeight: 700,
      },
      h3: {
        fontSize: '1.5rem',
      },
    },
  });
};
