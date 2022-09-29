#include "fraction.hpp"
#include <cmath>
#include <iostream>
#include <sstream>
#include <string>

using namespace std;

Fraction::Fraction() : numerator(0), denominator(1) {}

Fraction::Fraction(int numberator, int denominator)
{
  set(numberator, denominator);
}

void Fraction::set(int numberator_, int denominator_)
{
  if (denominator_ != 0)
  {
    numerator = numberator_;
    denominator = denominator_;
    reduce();
  }
  else
  {
    throw "nevneren ble null";
  }
}

Fraction Fraction::operator+(const Fraction &other) const
{
  Fraction fraction = *this;
  fraction += other;
  return fraction;
}

Fraction &Fraction::operator+=(const Fraction &other)
{
  set(numerator * other.denominator + denominator * other.numerator, denominator * other.denominator);
  return *this;
}

Fraction &Fraction::operator++()
{
  numerator += denominator;
  return *this;
}

Fraction Fraction::operator-(const Fraction &other) const
{
  Fraction fraction = *this;
  fraction -= other;
  return fraction;
}

Fraction &Fraction::operator-=(const Fraction &other)
{
  set(numerator * other.denominator - denominator * other.numerator, denominator * other.denominator);
  return *this;
}

Fraction &Fraction::operator--()
{
  numerator -= denominator;
  return *this;
}

Fraction Fraction::operator-() const
{
  Fraction fraction;
  fraction.numerator = -numerator;
  fraction.denominator = denominator;
  return fraction;
}

Fraction Fraction::operator*(const Fraction &other) const
{
  Fraction fraction = *this;
  fraction *= other;
  return fraction;
}

Fraction &Fraction::operator*=(const Fraction &other)
{
  set(numerator * other.numerator, denominator * other.denominator);
  return *this;
}

Fraction Fraction::operator/(const Fraction &other) const
{
  Fraction fraction = *this;
  fraction /= other;
  return fraction;
}

Fraction &Fraction::operator/=(const Fraction &other)
{
  set(numerator * other.denominator, denominator * other.numerator);
  return *this;
}

Fraction &Fraction::operator=(const Fraction &other)
{
  numerator = other.numerator;
  denominator = other.denominator;
  return *this;
}

bool Fraction::operator==(const Fraction &other) const
{
  return (compare(other) == 0) ? true : false;
}

bool Fraction::operator!=(const Fraction &other) const
{
  return (compare(other) != 0) ? true : false;
}

bool Fraction::operator<=(const Fraction &other) const
{
  return (compare(other) <= 0) ? true : false;
}

bool Fraction::operator>=(const Fraction &other) const
{
  return (compare(other) >= 0) ? true : false;
}

bool Fraction::operator<(const Fraction &other) const
{
  return (compare(other) < 0) ? true : false;
}

bool Fraction::operator>(const Fraction &other) const
{
  return (compare(other) > 0) ? true : false;
}

//-------------------------------------------------------------------
//
// Sørg for at nevneren alltid er positiv.
// Bruker Euclids algoritme for å finne fellesnevneren.
//
void Fraction::reduce()
{
  if (denominator < 0)
  {
    numerator = -numerator;
    denominator = -denominator;
  }
  int a = numerator;
  int b = denominator;
  int c;
  if (a < 0)
    a = -a;

  while (b > 0)
  {
    c = a % b;
    a = b;
    b = c;
  }
  numerator /= a;
  denominator /= a;
}

//-------------------------------------------------------------------
//
// Returnerer +1 hvis *this > other, 0 hvis de er like, -1 ellers
//
int Fraction::compare(const Fraction &other) const
{
  Fraction fraction = *this - other;
  if (fraction.numerator > 0)
    return 1;
  else if (fraction.numerator == 0)
    return 0;
  else
    return -1;
}

Fraction Fraction::operator-(int number) const
{
  Fraction fraction;
  int newNr = number * this->denominator;
  fraction.numerator = this->numerator - newNr;
  fraction.denominator = this->denominator;
  return fraction;
}

Fraction operator-(int integer, const Fraction &other)
{
  Fraction fraction;
  int newNr = integer * other.denominator;
  fraction.numerator = newNr - other.numerator;
  fraction.denominator = other.denominator;
  return fraction;
}

int main()
{
  Fraction fraction1(9, 3);
  Fraction fraction2(9, 3);

  // a)
  // i
  Fraction result1 = fraction1 - 2;
  cout << "fraction1 – 2: " << result1.numerator << "/" << result1.denominator << endl;

  // ii
  Fraction result2 = 10 - fraction2;
  cout << "10 - fraction2: " << result2.numerator << "/" << result2.denominator << endl;

  // b)
  // Tolkning av "5 - 3 - fraction1 - 7 - fraction2"
  // (5 - 3) - fraction1 - 7 - fraction2
  // (2 - fraction1) - 7 - fraction2 -> Fraction operator-(int integer, const Fraction &other)
  // (fraction - 7) - fraction2 -> Fraction Fraction::operator-(int number) const
  // (fraction - fraction2) -> Fraction Fraction::operator-(const Fraction &other)
}