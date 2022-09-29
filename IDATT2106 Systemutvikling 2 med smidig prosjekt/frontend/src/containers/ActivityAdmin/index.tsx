import { useEffect, useState } from 'react';
import URLS from 'URLS';
import { useNavigate, useParams, Navigate } from 'react-router-dom';
import { useUser } from 'hooks/User';
import { useMyHostActivities, useActivityById } from 'hooks/Activities';
import { formatDate } from 'utils';
import { parseISO } from 'date-fns';

// Material-UI
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Collapse from '@material-ui/core/Collapse';

// Icons
import EditIcon from '@material-ui/icons/EditRounded';
import ParticipantsIcon from '@material-ui/icons/PeopleRounded';
import HostsIcon from '@material-ui/icons/AdminPanelSettingsRounded';
import OpenIcon from '@material-ui/icons/OpenInBrowserRounded';
import InviteIcon from '@material-ui/icons/GroupAddRounded';

// Project components
import Paper from 'components/layout/Paper';
import Tabs from 'components/layout/Tabs';
import Navigation from 'components/navigation/Navigation';
import SidebarList from 'components/layout/SidebarList';
import ActivityEditor from 'containers/ActivityAdmin/components/ActivityEditor';
import ActivityInvites from 'containers/ActivityAdmin/components/ActivityInvites';
import ActivityHosts from 'containers/ActivityAdmin/components/ActivityHosts';
import ActivityRegistrations from 'containers/ActivityAdmin/components/ActivityRegistrations';

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    marginLeft: theme.spacing(35),
    [theme.breakpoints.down('lg')]: {
      padding: theme.spacing(4, 1, 6),
      marginLeft: 0,
    },
  },
  content: {
    maxWidth: 900,
    margin: '0 auto',
  },
  header: {
    color: theme.palette.text.primary,
    paddingLeft: theme.spacing(2),
  },
}));

const ActivityAdmin = () => {
  const classes = useStyles();
  const navigate = useNavigate();
  const { activityId } = useParams();
  const { data: user, isLoading: isUserLoading } = useUser();
  const { data, isError } = useActivityById(activityId || '');
  const editTab = { value: 'edit', label: activityId ? 'Endre' : 'Opprett', icon: EditIcon };
  const participantsTab = { value: 'participants', label: 'Påmeldte', icon: ParticipantsIcon };
  const invitedTab = { value: 'invited', label: 'Inviterte', icon: InviteIcon };
  const hostsTab = { value: 'hosts', label: 'Arrangører', icon: HostsIcon };
  const navigateTab = { value: 'navigate', label: 'Se arrangement', icon: OpenIcon };
  const tabs = [editTab, ...(activityId ? [participantsTab, invitedTab, hostsTab, navigateTab] : [])];
  const [tab, setTab] = useState(editTab.value);

  const goToActivity = (newActivity: string | null) => {
    if (newActivity) {
      navigate(`${URLS.ADMIN_ACTIVITIES}${newActivity}/`);
    } else {
      navigate(URLS.ADMIN_ACTIVITIES);
    }
  };

  useEffect(() => {
    if (isError || !user || (activityId && data && !isUserLoading && !data.hosts.some((host) => host.id === user.id) && data.creator?.id !== user.id)) {
      goToActivity(null);
    }
  }, [isError, data, user, isUserLoading, activityId]);

  useEffect(() => setTab(editTab.value), [activityId]);

  return (
    <Navigation maxWidth={false} noFooter topbarVariant='filled'>
      <SidebarList
        descKey='startDate'
        formatDesc={(date: string) => formatDate(parseISO(date))}
        idKey='id'
        onItemClick={(id: string | null) => goToActivity(id || null)}
        selectedItemId={activityId}
        title='Aktiviteter'
        titleKey='title'
        useHook={useMyHostActivities}
      />
      <div className={classes.root}>
        <div className={classes.content}>
          <Typography className={classes.header} variant='h2'>
            {activityId ? 'Rediger aktivitet' : 'Opprett aktivitet'}
          </Typography>
          <Tabs selected={tab} setSelected={setTab} tabs={tabs} />
          <Paper>
            <Collapse in={tab === editTab.value} mountOnEnter>
              <ActivityEditor activityId={activityId} goToActivity={goToActivity} />
            </Collapse>
            <Collapse in={tab === participantsTab.value} mountOnEnter>
              <ActivityRegistrations activityId={activityId} />
            </Collapse>
            <Collapse in={tab === invitedTab.value} mountOnEnter>
              <ActivityInvites activityId={activityId} />
            </Collapse>
            <Collapse in={tab === hostsTab.value} mountOnEnter>
              <ActivityHosts activityId={activityId} />
            </Collapse>
            {tab === navigateTab.value && <Navigate to={`${URLS.ACTIVITIES}${activityId}/`} />}
          </Paper>
        </div>
      </div>
    </Navigation>
  );
};

export default ActivityAdmin;
