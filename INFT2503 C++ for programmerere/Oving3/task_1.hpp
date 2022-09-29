#pragma once

class Circle
{
public:
  Circle(double radius_);
  int get_area() const;
  double get_circumference() const;

private:
  double radius;
};
