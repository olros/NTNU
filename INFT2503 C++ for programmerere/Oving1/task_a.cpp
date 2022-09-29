#include <iostream>

const int length = 5;

int main(int, char**) {
    int low = 0;
    int medium = 0;
    int high = 0;  

    std::cout << "Du skal skrive inn " << length << " temperaturer.\n";
    for (int i = 0; i < length; i++) {
      int input;
      std::cout << "Temperatur nr " << (i + 1) << ": ";
      std::cin >> input;
      if (input < 10) {
        low++;
      } else if (input > 20) {
        high++;
      } else {
        medium++;
      }
    }

    std::cout << "Antall under 10 er " << low << "\n";
    std::cout << "Antall mellom 10 og 20 er " << medium << "\n";
    std::cout << "Antall over 20 er " << high << "\n";
}
