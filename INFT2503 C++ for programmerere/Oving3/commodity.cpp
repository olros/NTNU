#include <iostream>
#include <string>
#include "commodity.hpp"

using namespace std;

const double sales_tax = 0.25;

Commodity::Commodity(string name_, int id_, double price_) : name(name_), id(id_), price(price_) {}

string Commodity::get_name() const
{
  return name;
}

int Commodity::get_id() const
{
  return id;
}

double Commodity::get_price(double amount) const
{
  return price * amount;
}

void Commodity::set_price(double price_)
{
  price = price_;
}

double Commodity::get_price_with_sales_tax(double amount) const
{
  return get_price(amount) * (sales_tax + 1);
}
