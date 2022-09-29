#include "set.hpp"
#include <vector>
#include <algorithm>
#include <string>
#include <iostream>

using namespace std;

Set::Set()
{
  this->set = vector<int>();
}

Set::Set(vector<int> set)
{
  for (size_t i = 0; i < set.size(); i++)
  {
    this->operator+(set[i]);
  }
}
bool Set::contains(int num)
{
  for (int i = 0; i < this->size(); i++)
  {
    if (this->set.at(i) == num)
    {
      return true;
    }
  }
  return false;
}
size_t Set::size()
{
  return this->set.size();
}

int &Set::operator[](int i)
{
  return this->set[i];
}
int &Set::operator[](size_t i)
{
  return this->set[i];
}
void Set::operator+(int nr)
{
  if (!this->contains(nr))
  {
    this->set.emplace_back(nr);
  }
}

void Set::operator=(Set &other)
{
  this->set.clear();
  for (size_t i = 0; i < other.size(); i++)
  {
    this->operator+(other[i]);
  }
}

Set Set::operator+(Set &other)
{
  Set newSet = *this;
  for (size_t i = 0; i < other.size(); i++)
  {
    if (!newSet.contains(other.set[i])) {
      newSet + other.set[i];
    }
  }
  return newSet;
}

string Set::print()
{
  string output = "{ ";
  for (size_t i = 0; i < this->size(); i++)
  {
    int num = this->operator[](i);
    output.append(to_string(num));
    if (i != this->size() - 1)
    {
      output.append(", ");
    }
  }
  output.append(" }");
  return output;
}

int main()
{
  // Ny, tom mengde
  Set set1;
  // Legge et nytt tall inn i en mengde
  set1 + 1;
  set1 + 2;
  set1 + 3;
  // Dersom tallet fins fra før, skal det ikke skje noe
  set1 + 3;

  // Skrive ut mengde
  cout << set1.print() << endl;

  // Mengde lik en annen mengde
  Set set2 = set1;

  // // Union av to mengder
  Set union_of_equal = set1 + set2;

  cout << union_of_equal.print() << endl;

  Set set3({2, 3, 4, 5});
  Set union_of_diff = set1 + set3;

  cout << union_of_diff.print() << endl;
}
