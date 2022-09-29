#include <iostream>
#include <iomanip>

using namespace std;

template <typename Type>
bool isEqual(const Type &first, const Type &second) {
  cout << "Template check if " << first << " and " << second << " is equal" << endl;
  return first == second;
}

bool isEqual(const double &first, const double &second) {
  cout << "Double-spesific check if " << setprecision(8) << first << " and " << second << " is equal" << endl;
  return abs(first - second) < 0.00001;
}

int main(int, char**) {
    cout << "Test int" << endl;
    cout << isEqual(1,2) << endl;
    cout << isEqual(1,1) << endl;

    cout << "Test string" << endl;
    cout << isEqual("Yes","Noo") << endl;
    cout << isEqual("Yes","Yes") << endl;

    cout << "Test double" << endl;
    cout << isEqual(1.000001,2.000005) << endl;
    cout << isEqual(1.000001,1.000005) << endl;
}
