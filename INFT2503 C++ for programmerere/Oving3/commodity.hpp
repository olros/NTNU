#pragma once

#include <string>

using namespace std;

class Commodity
{
public:
  Commodity(string name_, int id_, double price_);
  string get_name() const;
  int get_id() const;
  double get_price(double = 1.0) const;
  void set_price(double price_);
  double get_price_with_sales_tax(double = 1.0) const;

private:
  string name;
  int id;
  double price;
};