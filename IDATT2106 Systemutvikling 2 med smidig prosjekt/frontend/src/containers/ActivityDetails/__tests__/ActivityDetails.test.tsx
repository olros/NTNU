/* eslint-disable @typescript-eslint/no-non-null-assertion */
import '@testing-library/jest-dom/extend-expect';
import { render, mockActivity, mockUser, mockGet } from 'test-utils';
import ActivityRenderer from 'containers/ActivityDetails/components/ActivityRenderer';
import { Activity, User } from 'types/Types';
import { ACTIVITIES, USERS, ME } from 'api/api';
import { formatDate } from 'utils';
import { parseISO } from 'date-fns';

test('ActivityRenderer displays details', async () => {
  const activity = mockActivity('activityId');
  const user = mockUser();
  mockGet<Activity>(`${ACTIVITIES}/${activity.id}`, activity);
  mockGet<User>(`${USERS}/${ME}`, user);
  const { getByText } = render(<ActivityRenderer data={activity} />);

  expect(getByText(activity.title)).toBeInTheDocument();
  expect(getByText(activity.description)).toBeInTheDocument();
  expect(getByText(`Til: ${formatDate(parseISO(activity.endDate))}`)).toBeInTheDocument();
  expect(getByText(`Fra: ${formatDate(parseISO(activity.startDate))}`)).toBeInTheDocument();
});

test('Closed activity displays closed text', async () => {
  const mock = mockActivity('activityId');
  const activity = { ...mock, closed: true };
  const user = mockUser();
  mockGet<Activity>(`${ACTIVITIES}/${activity.id}`, activity);
  mockGet<User>(`${USERS}/${ME}`, user);
  const { getByText } = render(<ActivityRenderer data={activity} />);

  expect(getByText('Denne aktiviteten er avlyst. Det er derfor ikke mulig å melde seg på.')).toBeInTheDocument();
});
