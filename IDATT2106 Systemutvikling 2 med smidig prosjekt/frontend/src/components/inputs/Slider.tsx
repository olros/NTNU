import { withStyles, makeStyles } from '@material-ui/core/styles';
import { Input, InputAdornment, Slider } from '@material-ui/core';
import { Control, Controller, RegisterOptions } from 'react-hook-form';

const useStyles = makeStyles((theme) => ({
  root: {
    width: '100%',
  },
  margin: {
    height: theme.spacing(3),
  },
  input: {
    width: 56,
  },
  sliderGrid: {
    display: 'grid',
    gridTemplateColumns: '1fr auto',
    gap: theme.spacing(3),
    marginBottom: theme.spacing(1),
  },
}));

const PrettoSlider = withStyles({
  root: {
    height: 8,
  },
  thumb: {
    height: 24,
    width: 24,
    backgroundColor: '#fff',
    border: '2px solid currentColor',
    marginTop: -8,
    marginLeft: -12,
    '&:focus, &:hover, &$active': {
      boxShadow: 'inherit',
    },
  },
  active: {},
  valueLabel: {
    left: 'calc(-50% + 4px)',
  },
  track: {
    height: 8,
    borderRadius: 4,
  },
  rail: {
    height: 8,
    borderRadius: 4,
  },
})(Slider);

export type IProps = {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  control: Control<any>;
  name: string;
  rules?: RegisterOptions;
  defaultValue?: number;
};

export default function CustomizedSlider({ name, control, rules = {}, defaultValue = 3 }: IProps) {
  const classes = useStyles();
  return (
    <div className={classes.root}>
      <div className={classes.sliderGrid}>
        <Controller
          control={control}
          defaultValue={defaultValue}
          name={name}
          render={({ field }) => (
            <>
              <PrettoSlider {...field} aria-labelledby='input-slider' max={15} min={0} valueLabelDisplay='auto' />
              <Input
                {...field}
                className={classes.input}
                endAdornment={<InputAdornment>km</InputAdornment>}
                inputProps={{
                  step: 1,
                  min: 0,
                  max: 15,
                  type: 'number',
                  'aria-labelledby': 'input-slider',
                }}
                margin='dense'
                onChange={(e) => {
                  const val = Number(e.target.value);
                  field.onChange(val < 0 ? 0 : val > 15 ? 15 : val);
                }}
              />
            </>
          )}
          rules={rules}
        />
      </div>
    </div>
  );
}
