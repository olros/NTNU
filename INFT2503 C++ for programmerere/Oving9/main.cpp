#include <algorithm>
#include <iostream>
#include <string>
#include <vector>

using namespace std;

ostream &operator<<(ostream &out, const vector<int> &table)
{
  for (auto &e : table)
    out << e << " ";
  return out;
}

int main(int, char **)
{
  vector<int> v1 = {3, 3, 12, 14, 17, 25, 30};
  cout << "v1: " << v1 << endl;

  vector<int> v2 = {2, 3, 12, 14, 24};
  cout << "v2: " << v2 << endl;

  // a)
  auto element = find_if(begin(v1), end(v1), [](int i)
                         { return i > 15; });
  cout << "Første element i v1 større enn 15: " << *element << endl;

  // b)
  auto b1 = equal(v1.begin(), v1.begin() + 5, v2.data(), [](int a, int b)
                  { return abs(a - b) <= 2; });
  cout << "Intervallet [v1.begin(), v1.begin() + 5> og v2 er omtrent like: " << b1 << endl;
  auto b2 = equal(v1.begin(), v1.begin() + 4, v2.data(), [](int a, int b)
                  { return abs(a - b) <= 2; });
  cout << "Intervallet [v1.begin(), v1.begin() + 4> og v2 er omtrent like: " << b2 << endl;

  // c)
  replace_copy_if(
      v1.begin(), v1.end(), v1.begin(), [](int i)
      { return (i % 2) == 1; },
      100);
  cout << "v1 med 100 istedenfor oddetall: " << v1 << endl;
}
