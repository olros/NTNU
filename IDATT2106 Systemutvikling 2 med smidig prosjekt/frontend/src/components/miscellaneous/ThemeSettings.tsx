import { MouseEvent as ReactMouseEvent, useState } from 'react';
import { useThemeSettings } from 'hooks/ThemeContext';
import { ThemeTypes, themesDetails } from 'theme';

// Material-ui
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import ToggleButton from '@material-ui/core/ToggleButton';
import ToggleButtonGroup from '@material-ui/core/ToggleButtonGroup';
import IconButton from '@material-ui/core/IconButton';

// Icons
import LightIcon from '@material-ui/icons/WbSunnyRounded';

// Project components
import Dialog from 'components/layout/Dialog';

const useStyles = makeStyles((theme) => ({
  group: {
    background: theme.palette.background.default,
    margin: theme.spacing(0, 2),
  },
  groupButton: {
    margin: theme.spacing(0, 1),
    color: theme.palette.text.secondary,
  },
}));

type ThemeSettingsProps = {
  className?: string;
  classNameIcon?: string;
};

function ThemeSettings({ className, classNameIcon }: ThemeSettingsProps) {
  const themeSettings = useThemeSettings();
  const [open, setOpen] = useState(false);
  const [themeName, setThemeName] = useState(themeSettings.getThemeFromStorage());
  const classes = useStyles();

  const changeTheme = (e: ReactMouseEvent<HTMLElement, MouseEvent>, newThemeName: ThemeTypes) => {
    if (newThemeName) {
      setThemeName(newThemeName);
      themeSettings.set(newThemeName);
      window.gtag('event', 'theme-switch', {
        event_category: 'theme',
        event_label: newThemeName,
      });
    }
  };

  return (
    <>
      <IconButton aria-label='Endre fargetema' className={className} onClick={() => setOpen(true)}>
        <LightIcon className={classNameIcon} />
      </IconButton>
      <Dialog fullWidth={false} maxWidth={false} onClose={() => setOpen(false)} open={open} titleText='Tema'>
        <ToggleButtonGroup aria-label='Tema' className={classes.group} exclusive onChange={changeTheme} orientation='vertical' value={themeName}>
          {themesDetails.map((theme) => (
            <ToggleButton aria-label={theme.name} key={theme.key} value={theme.key}>
              <theme.icon />
              <Typography className={classes.groupButton} variant='subtitle2'>
                {theme.name}
              </Typography>
            </ToggleButton>
          ))}
        </ToggleButtonGroup>
      </Dialog>
    </>
  );
}

export default ThemeSettings;
