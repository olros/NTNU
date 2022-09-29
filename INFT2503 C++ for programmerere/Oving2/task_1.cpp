#include <iostream>

using namespace std;

int i = 3;
int j = 5;
int *p = &i;
int *q = &j;

void a()
{
  cout << "Adress of i: " << &i << ", value of i: " << i << endl;
  cout << "Adresse til j: " << &j << ", value of j: " << j << endl;
  cout << "Adresse til p: " << p << ", value of p: " << *p << endl;
  cout << "Adresse til q: " << q << ", value of q: " << *q << endl;
}

void b()
{
  *p = 7;
  *q += 4;
  *q = *p + 1;
  p = q;
  cout << *p << " " << *q << endl;
  a();
}

int main()
{
  b();
}
