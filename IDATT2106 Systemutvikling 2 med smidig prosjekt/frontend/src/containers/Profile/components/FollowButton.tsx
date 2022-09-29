import { useFollowUser, useUnfollowUser, useRemoveFollower } from 'hooks/Followers';
import { useUser, useIsAuthenticated } from 'hooks/User';
import { useSnackbar } from 'hooks/Snackbar';

// Material UI
import { Button, ButtonProps } from '@material-ui/core';

// Project components
import VerifyDialog from 'components/layout/VerifyDialog';

export type FollowButtonProps = ButtonProps & {
  userId: string;
};

const FollowButton = ({ userId, ...props }: FollowButtonProps) => {
  const { data: user } = useUser(userId);
  const isAuthenticated = useIsAuthenticated();
  const follow = useFollowUser();
  const unfollow = useUnfollowUser(userId);
  const showSnackbar = useSnackbar();

  if (!user || !isAuthenticated) {
    return null;
  }

  const startFollow = async () => {
    follow.mutate(userId, {
      onSuccess: () => {
        showSnackbar(`Du følger nå ${user.firstName} ${user.surname}`, 'success');
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  const stopFollow = async () => {
    unfollow.mutate(null, {
      onSuccess: () => {
        showSnackbar(`Du har sluttet å følge ${user.firstName} ${user.surname}`, 'info');
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  if (user.currentUserIsFollowing) {
    return (
      <VerifyDialog
        closeText='Nei'
        confirmText='Ja'
        contentText={`Slutt å følge ${user.firstName} ${user.surname}?`}
        onConfirm={stopFollow}
        titleText='Avfølg'
        variant='contained'
        {...props}>
        Følger
      </VerifyDialog>
    );
  }
  return (
    <Button fullWidth onClick={startFollow} variant='outlined' {...props}>
      Følg
    </Button>
  );
};

export default FollowButton;

export type RemoveFollowButtonProps = ButtonProps & {
  userId: string;
};

export const RemoveFollowButton = ({ userId, ...props }: RemoveFollowButtonProps) => {
  const { data: user } = useUser(userId);
  const isAuthenticated = useIsAuthenticated();
  const removeFollower = useRemoveFollower(userId);
  const showSnackbar = useSnackbar();

  if (!user || !isAuthenticated) {
    return null;
  }

  const deleteFollower = async () => {
    removeFollower.mutate(null, {
      onSuccess: () => {
        showSnackbar(`Du har sluttet å følge ${user.firstName} ${user.surname}`, 'info');
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  return (
    <VerifyDialog
      closeText='Avbryt'
      confirmText='Fjern'
      contentText={`${user.firstName} ${user.surname} blir ikke informert om at vedkommende er fjernet fra følgerne dine.`}
      onConfirm={deleteFollower}
      titleText='Vil du fjerne følger?'
      variant='outlined'
      {...props}>
      Fjern følger
    </VerifyDialog>
  );
};
