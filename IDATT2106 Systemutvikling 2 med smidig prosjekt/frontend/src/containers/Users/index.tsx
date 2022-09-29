import { useState, useMemo } from 'react';
import Helmet from 'react-helmet';
import classnames from 'classnames';
import { useUsers } from 'hooks/User';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { List, Typography } from '@material-ui/core';

// Project Components
import Navigation from 'components/navigation/Navigation';
import Pagination from 'components/layout/Pagination';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';
import UserCard from 'containers/Users/components/UserCard';
import UsersSearch from 'containers/Users/components/UsersSearch';

const useStyles = makeStyles((theme) => ({
  top: {
    width: '100%',
    padding: theme.spacing(4, 2),
  },
  grid: {
    display: 'grid',
    gap: theme.spacing(2),
    alignItems: 'self-start',
    [theme.breakpoints.down('md')]: {
      gap: theme.spacing(1),
    },
  },
  root: {
    gridTemplateColumns: '300px 1fr',
    [theme.breakpoints.down('xl')]: {
      gridTemplateColumns: '1fr',
    },
  },
  searchWrapper: {
    margin: 'auto',
  },
  title: {
    color: theme.palette.getContrastText(theme.palette.colors.topbar),
    paddingBottom: theme.spacing(2),
  },
  subtitle: {
    margin: 'auto 0',
  },
}));

export type UsersFilters = {
  search?: string;
  ['trainingLevel.level']?: string;
  sort?: string;
};

const Users = () => {
  const classes = useStyles();
  const [filters, setFilters] = useState<UsersFilters>({});
  const { data, error, hasNextPage, fetchNextPage, isFetching } = useUsers(filters);
  const users = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !users.length && !isFetching, [users, isFetching]);

  return (
    <Navigation topbarVariant='dynamic'>
      <Helmet>
        <title>Brukere</title>
      </Helmet>
      <div className={classes.top}>
        <div className={classes.searchWrapper}>
          <Typography align='center' className={classes.title} variant='h1'>
            Brukere
          </Typography>
        </div>
      </div>
      <div className={classnames(classes.grid, classes.root)}>
        <div className={classes.grid}>
          <Typography align='left' className={classes.subtitle} variant='h2'>
            Filtrer
          </Typography>
          <UsersSearch updateFilters={setFilters} />
        </div>
        {filters.search ? (
          <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
            {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen brukere'} />}
            <List className={classes.grid}>
              {users.map((user) => (
                <UserCard key={user.id} user={user} />
              ))}
            </List>
          </Pagination>
        ) : (
          <Typography align='center' variant='h3'>
            Søk for å se brukere
          </Typography>
        )}
      </div>
    </Navigation>
  );
};

export default Users;
