import { Button, ButtonProps, FormHelperText } from '@material-ui/core';
import { UseFormReturn } from 'react-hook-form';

export type SubmitButtonProps = ButtonProps &
  Pick<UseFormReturn, 'formState'> & {
    noFeedback?: boolean;
  };

const SubmitButton = ({ formState, children, disabled, noFeedback = false, ...props }: SubmitButtonProps) => {
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  const errorList = Array.isArray(Object.keys(formState.errors)) ? Object.keys(formState.errors).map((error) => formState.errors[error].message) : [];
  return (
    <>
      <Button color='primary' disabled={disabled} fullWidth type='submit' {...props}>
        {children}
      </Button>
      {!noFeedback &&
        errorList.map((error, i) => (
          <FormHelperText error key={i} style={{ textAlign: 'center' }}>
            {error}
          </FormHelperText>
        ))}
    </>
  );
};
export default SubmitButton;
