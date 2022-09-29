import { useMemo, useState } from 'react';
import Helmet from 'react-helmet';
import { useGroups } from 'hooks/Group';
import classnames from 'classnames';

// Material UI Components
import { makeStyles, Typography } from '@material-ui/core';

// Project Components
import Container from 'components/layout/Container';
import GroupListItem from 'containers/Groups/components/GroupListItem';
import Pagination from 'components/layout/Pagination';
import CreateGroup from 'components/miscellaneous/CreateGroup';
import NotFoundIndicator from 'components/miscellaneous/NotFoundIndicator';
import FilterBox from 'components/miscellaneous/FilterBox';

const useStyles = makeStyles((theme) => ({
  list: {
    display: 'grid',
    gap: theme.spacing(1),
  },
  top: {
    gridTemplateColumns: '1fr auto',
  },
}));

export type GroupsFilters = {
  name?: string;
};

const Groups = () => {
  const classes = useStyles();
  const [filters, setFilters] = useState<GroupsFilters>({});
  const { data, error, hasNextPage, fetchNextPage, isFetching } = useGroups(filters);
  const results = useMemo(() => (data !== undefined ? data.pages.map((page) => page.content).flat(1) : []), [data]);
  const isEmpty = useMemo(() => !results.length && !isFetching, [results, isFetching]);
  return (
    <Container>
      <Helmet>
        <title>Grupper - Rombestilling</title>
      </Helmet>
      <div className={classes.list}>
        <div className={classnames(classes.list, classes.top)}>
          <Typography variant='h1'>Grupper</Typography>
          <CreateGroup>Opprett gruppe</CreateGroup>
        </div>
        <FilterBox field='name' filters={filters} label='Søk etter grupper' updateFilters={setFilters} />
        <Pagination fullWidth hasNextPage={hasNextPage} isLoading={isFetching} nextPage={() => fetchNextPage()}>
          <div className={classes.list}>
            {isEmpty && <NotFoundIndicator header={error?.message || 'Fant ingen grupper'} />}
            {results.map((group) => (
              <GroupListItem group={group} key={group.id} />
            ))}
          </div>
        </Pagination>
      </div>
    </Container>
  );
};

export default Groups;
