#include <iostream>

using namespace std;

int main()
{
  int a = 5;
  int &b = a; // En referanse må refererer til noe
  int *c;
  c = &b;
  a = b + *c; // `a` og `b` er ikke pekere
  b = 2;      // Adressen til b kan ikke gis verdien 2
}
