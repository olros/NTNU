#include <iostream>

using namespace std;

int main()
{
  double number;
  double *pointer = &number;
  double &reference = number;

  // Option 1:
  number = 1;
  cout << "Number: " << number << endl;
  // Option 2:
  *pointer = 2;
  cout << "Number: " << number << endl;
  // Option 3:
  reference = 3;
  cout << "Number: " << number << endl;
}
