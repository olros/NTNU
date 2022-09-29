import { makeStyles, TextField as MuiTextField, TextFieldProps } from '@material-ui/core';
import MuiDatePicker, { DatePickerProps as MuiDatePickerProps } from '@material-ui/lab/DatePicker';
import MuiDateTimePicker, { DateTimePickerProps as MuiDateTimePickerProps } from '@material-ui/lab/DateTimePicker';
import { Control, Controller, RegisterOptions, UseFormReturn } from 'react-hook-form';

const useStyles = makeStyles((theme) => ({
  noOutline: {
    ...theme.palette.extra.noOutlinedTextField,
  },
}));

export type DatePickerProps = TextFieldProps &
  Pick<UseFormReturn, 'formState'> & {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    control: Control<any>;
    name: string;
    rules?: RegisterOptions;
    label: string;
    defaultValue?: string;
    dateProps?: Partial<MuiDatePickerProps> & Partial<MuiDateTimePickerProps>;
    type: 'date' | 'date-time';
    noOutline?: boolean;
  };

const DatePicker = ({ type, name, label, control, formState, noOutline, rules = {}, defaultValue = '', dateProps, ...props }: DatePickerProps) => {
  const classes = useStyles();
  const Picker = type === 'date' ? MuiDatePicker : MuiDateTimePicker;
  return (
    <Controller
      control={control}
      defaultValue={defaultValue}
      name={name}
      render={({ field }) => (
        <Picker
          {...field}
          {...dateProps}
          ampm={false}
          inputFormat={type === 'date' ? 'dd/MM/yyyy' : 'dd/MM/yyyy HH:mm'}
          label={label}
          renderInput={(params) => (
            <MuiTextField
              className={noOutline ? classes.noOutline : ''}
              margin='normal'
              variant='outlined'
              {...params}
              error={Boolean(formState.errors[name])}
              helperText={formState.errors[name]?.message}
              {...props}
            />
          )}
        />
      )}
      rules={rules}
    />
  );
};
export default DatePicker;
