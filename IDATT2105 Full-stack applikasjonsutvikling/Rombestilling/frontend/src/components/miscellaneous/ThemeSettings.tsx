import { MouseEvent as ReactMouseEvent, useState } from 'react';
import { useThemeSettings } from 'hooks/ThemeContext';
import { ThemeTypes, themesDetails } from 'theme';

// Material-ui
import { makeStyles, Typography, ToggleButton, ToggleButtonGroup } from '@material-ui/core';

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

export type ThemeSettingsProps = {
  open: boolean;
  onClose: () => void;
};

const ThemeSettings = ({ open, onClose }: ThemeSettingsProps) => {
  const themeSettings = useThemeSettings();
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
    <Dialog fullWidth={false} maxWidth={false} onClose={onClose} open={open} titleText='Tema'>
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
  );
};

export default ThemeSettings;
