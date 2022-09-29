import { forwardRef } from 'react';
import classnames from 'classnames';
import { UseFormReturn } from 'react-hook-form';

// Material UI Components
import { TextField as MuiTextField, TextFieldProps as MuiTextFieldProps, makeStyles } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
  noOutline: {
    ...theme.palette.extra.noOutlinedTextField,
  },
}));

export type TextFieldProps = MuiTextFieldProps &
  Pick<UseFormReturn, 'formState'> & {
    name: string;
    noDefaultShrink?: boolean;
    noOutline?: boolean;
  };

const TextField = forwardRef(({ name, className, noOutline = false, noDefaultShrink = false, formState, ...props }: TextFieldProps, ref) => {
  const classes = useStyles();
  return (
    <MuiTextField
      className={classnames(className, noOutline && classes.noOutline)}
      error={Boolean(formState.errors[name])}
      fullWidth
      helperText={formState.errors[name]?.message}
      InputLabelProps={noDefaultShrink ? undefined : { shrink: true }}
      inputRef={ref}
      margin='normal'
      name={name}
      placeholder={props.placeholder || 'Skriv her'}
      variant='outlined'
      {...props}
    />
  );
});

TextField.displayName = 'TextField';
export default TextField;
