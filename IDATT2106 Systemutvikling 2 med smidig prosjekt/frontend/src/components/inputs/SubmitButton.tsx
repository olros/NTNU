import Button, { ButtonProps } from '@material-ui/core/Button';
import FormHelperText from '@material-ui/core/FormHelperText';
import { UseFormReturn } from 'react-hook-form';

export type SubmitButtonProps = ButtonProps & Pick<UseFormReturn, 'formState'>;

const SubmitButton = ({ formState, children, disabled, ...props }: SubmitButtonProps) => {
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  const errorList = Array.isArray(Object.keys(formState.errors)) ? Object.keys(formState.errors).map((error) => formState.errors[error].message) : [];
  return (
    <>
      <Button color='primary' disabled={disabled} fullWidth type='submit' {...props}>
        {children}
      </Button>
      {errorList.map((error, i) => (
        <FormHelperText error key={i} style={{ textAlign: 'center' }}>
          {error}
        </FormHelperText>
      ))}
    </>
  );
};
export default SubmitButton;
