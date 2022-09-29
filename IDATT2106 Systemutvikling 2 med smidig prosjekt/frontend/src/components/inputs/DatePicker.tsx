import MuiTextField, { TextFieldProps } from '@material-ui/core/TextField';
import MuiDatePicker, { DatePickerProps as MuiDatePickerProps } from '@material-ui/lab/DatePicker';
import MuiDateTimePicker, { DateTimePickerProps as MuiDateTimePickerProps } from '@material-ui/lab/DateTimePicker';
import { Control, Controller, RegisterOptions, UseFormReturn } from 'react-hook-form';

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
  };

const DatePicker = ({ type, name, label, control, formState, rules = {}, defaultValue = '', dateProps, ...props }: DatePickerProps) => {
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
