import { forwardRef, useState } from 'react';
import classnames from 'classnames';
import { UseFormReturn, Control, UseFormRegister, UseFormTrigger, useFieldArray } from 'react-hook-form';
import { Activity } from 'types/Types';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { IconButton, Button, List, ListItem } from '@material-ui/core';

// Icons
import DeleteIcon from '@material-ui/icons/DeleteOutlineRounded';
import AddIcon from '@material-ui/icons/AddRounded';

// Project components
import Dialog from 'components/layout/Dialog';
import TextField from 'components/inputs/TextField';

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'grid',
    gridGap: theme.spacing(1),
  },
  button: {
    height: 50,
  },
  remove: {
    color: theme.palette.error.main,
  },
  listitem: {
    gridTemplateColumns: '65px 1fr auto',
    padding: theme.spacing(1, 0),
  },
}));

export type EquipmentEditorProps = Pick<UseFormReturn, 'formState'> & {
  name: keyof Activity;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  register: UseFormRegister<any>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  control: Control<any>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  trigger: UseFormTrigger<any>;
};

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export const EquipmentEditor = forwardRef(({ control, trigger, register, name, formState }: EquipmentEditorProps, ref) => {
  const classes = useStyles();
  const [openEquipment, setOpenEquipment] = useState(false);
  const { fields, append, remove } = useFieldArray({
    control,
    name: name,
  });

  const close = async () => {
    await trigger();
    const valid = !formState.errors[name];
    if (valid) {
      setOpenEquipment(false);
    }
  };

  return (
    <>
      <Button onClick={() => setOpenEquipment(true)} variant='outlined'>
        Endre utstyr
      </Button>
      <Dialog onClose={close} open={openEquipment} titleText='Endre utstyr'>
        <div className={classes.root}>
          <List className={classes.root} disablePadding>
            {fields?.map((field, index) => (
              <ListItem className={classnames(classes.root, classes.listitem)} divider key={field.id}>
                <TextField
                  defaultValue={0}
                  formState={formState}
                  label='Antall'
                  {...register(`${name}.${index}.amount` as const, {
                    required: true,
                  })}
                  error={Boolean(formState.errors[name]?.[index]?.amount)}
                  helperText={formState.errors[name]?.[index]?.amount?.message}
                  required
                  type='number'
                />
                <TextField
                  formState={formState}
                  label='Navn'
                  {...register(`${name}.${index}.name` as const, {
                    required: 'Gi utstyret et navn',
                  })}
                  error={Boolean(formState.errors[name]?.[index]?.name)}
                  helperText={formState.errors[name]?.[index]?.name?.message}
                  required
                />
                <IconButton aria-label='Slett' onClick={() => remove(index)}>
                  <DeleteIcon className={classes.remove} />
                </IconButton>
              </ListItem>
            ))}
            <Button endIcon={<AddIcon />} fullWidth onClick={() => append({ amount: 1, name: '' })}>
              Legg til utstyr
            </Button>
          </List>
        </div>
      </Dialog>
    </>
  );
});

EquipmentEditor.displayName = 'EquipmentEditor';
export default EquipmentEditor;
