#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

int main(int, char**) {
    vector<double> numbers;

    for (double i = 1; i <= 5; i++) {
      numbers.emplace_back(i);
    }
    cout << "First number: " << numbers.front() << endl;
    cout << "Last number: " << numbers.back() << endl;

    numbers.emplace(numbers.begin() + 1, 10.0);

    cout << "First number after emplace: " << numbers.front() << endl;

    auto found = find(numbers.begin(), numbers.end(), 3.0);
    if (found != numbers.end()) {
      cout << "Found number: " << 3.0 << ", at position: " << found - numbers.begin() << " from the start" << endl;
    }
}
