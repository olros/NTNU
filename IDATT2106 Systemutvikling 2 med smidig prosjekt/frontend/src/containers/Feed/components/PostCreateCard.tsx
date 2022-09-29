import { useForm } from 'react-hook-form';
import { ActivityList, Post, PostCreate } from 'types/Types';
import { useSnackbar } from 'hooks/Snackbar';
import { useUser } from 'hooks/User';
import { useCreatePost, useUpdatePost, useDeletePost } from 'hooks/Feed';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { Accordion, AccordionSummary, AccordionDetails, Typography } from '@material-ui/core';

// Icons
import ExpandMoreIcon from '@material-ui/icons/ExpandMoreRounded';

// Project Components
import Dialog, { DialogProps } from 'components/layout/Dialog';
import VerifyDialog from 'components/layout/VerifyDialog';
import TextField from 'components/inputs/TextField';
import SubmitButton from 'components/inputs/SubmitButton';
import PostCard from 'containers/Feed/components/PostCard';
import { SingleImageUpload } from 'components/inputs/Upload';

const useStyles = makeStyles((theme) => ({
  grid: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  accordion: {
    boxShadow: 'none',
    border: `${theme.palette.borderWidth} solid ${theme.palette.divider}`,
    background: theme.palette.background.paper,
  },
  icon: {
    margin: 'auto',
  },
}));

export type PostCreateProps = DialogProps & {
  activity?: ActivityList;
  post?: Post;
};

type FormData = Pick<PostCreate, 'content' | 'image' | 'activityId'>;

const PostCreateCard = ({ activity, post, ...props }: PostCreateProps) => {
  const classes = useStyles();
  const showSnackbar = useSnackbar();
  const { data: user } = useUser();
  const createPost = useCreatePost();
  const updatePost = useUpdatePost(post?.id || '');
  const deletePost = useDeletePost(post?.id || '');
  const { register, handleSubmit, formState, setValue, getValues, watch } = useForm({ defaultValues: { ...post } });

  const submit = async (data: FormData) => {
    if (post) {
      updatePost.mutate(
        { ...data },
        {
          onSuccess: () => {
            showSnackbar('Innlegget ble oppdatert', 'success');
            props.onClose();
          },
          onError: (e) => {
            showSnackbar(e.message, 'error');
          },
        },
      );
    } else {
      createPost.mutate(
        { ...data, activityId: activity?.id || '' },
        {
          onSuccess: () => {
            showSnackbar('Innlegget ble publisert', 'success');
            props.onClose();
          },
          onError: (e) => {
            showSnackbar(e.message, 'error');
          },
        },
      );
    }
  };

  const remove = async () => {
    deletePost.mutate(null, {
      onSuccess: () => {
        showSnackbar('Innlegget ble slettet', 'success');
        props.onClose();
      },
      onError: (e) => {
        showSnackbar(e.message, 'error');
      },
    });
  };

  const getPreview = () =>
    ({
      ...getValues(),
      creator: { ...user },
      id: '123',
      createdAt: new Date().toISOString(),
      likesCount: 0,
      hasLiked: false,
      commentsCount: 0,
      activity: activity,
      ...(post || {}),
    } as Post);

  return (
    <Dialog titleText={post ? 'Oppdater innlegg' : 'Opprett innlegg'} {...props}>
      <form className={classes.grid} onSubmit={handleSubmit(submit)}>
        <TextField
          formState={formState}
          label='Beskrivelse'
          maxRows={10}
          minRows={3}
          multiline
          {...register('content', { required: 'Gi arrengementet en beskrivelse' })}
          required
        />
        <SingleImageUpload
          formState={formState}
          label='Legg til bilde'
          name='image'
          register={register('image')}
          setValue={setValue}
          variant='outlined'
          watch={watch}
        />
        <SubmitButton formState={formState}>{post ? 'Oppdater innlegg' : 'Publiser'}</SubmitButton>
        {Boolean(post) && <VerifyDialog onConfirm={remove}>Slett innlegg</VerifyDialog>}
        <div>
          <Accordion className={classes.accordion}>
            <AccordionSummary expandIcon={<ExpandMoreIcon />}>
              <Typography>Forhåndsvisning</Typography>
            </AccordionSummary>
            <AccordionDetails>
              <PostCard post={getPreview()} preview />
            </AccordionDetails>
          </Accordion>
        </div>
      </form>
    </Dialog>
  );
};

export default PostCreateCard;
