/* eslint-disable @typescript-eslint/no-non-null-assertion */
import '@testing-library/jest-dom/extend-expect';
import { render, mockActivity } from 'test-utils';
import ActivityCard from 'components/layout/ActivityCard';

test('ActivityCard displays title and description', async () => {
  const activity = mockActivity('activityId');
  const { getByText } = render(<ActivityCard activity={activity} />);

  expect(getByText(activity.title)).toBeInTheDocument();
  expect(getByText(activity.description)).toBeInTheDocument();
});
