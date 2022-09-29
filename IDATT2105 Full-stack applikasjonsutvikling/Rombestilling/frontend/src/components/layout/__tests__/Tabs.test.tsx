/* eslint-disable @typescript-eslint/no-non-null-assertion */
import '@testing-library/jest-dom/extend-expect';
import { render } from 'test-utils';
import Tabs from 'components/layout/Tabs';

test('tabs displays all tabs', async () => {
  const firstTab = { value: 'first', label: 'First' };
  const secondTab = { value: 'second', label: 'Second' };
  const tabs = [firstTab, secondTab];
  const { getByText } = render(<Tabs selected={firstTab.value} setSelected={() => null} tabs={tabs} />);

  expect(getByText(firstTab.label)).toBeInTheDocument();
  expect(getByText(secondTab.label)).toBeInTheDocument();
});
