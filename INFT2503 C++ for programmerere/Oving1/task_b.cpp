#include <iostream>
#include <cstdlib>
#include <fstream>
#include "task_b.hpp"

const int length = 5;

int main(int, char**) {
    int low = 0;
    int medium = 0;
    int high = 0;

    double temperatures[length];

    read_temperatures(temperatures, length);

    for (int i = 0; i < length; i++) {
      double temperature = temperatures[i];
      if (temperature < 10) {
        low++;
      } else if (temperature > 20) {
        high++;
      } else {
        medium++;
      }
    }

    std::cout << "Antall under 10 er " << low << "\n";
    std::cout << "Antall mellom 10 og 20 er " << medium << "\n";
    std::cout << "Antall over 20 er " << high << "\n";
}

void read_temperatures(double temperatures[], int length) {
    const char filename[] = "tallfil.txt";
    std::ifstream file;
    file.open(filename);
    if (!file) {
      std::cout << "Feil ved åpning av innfil." << std::endl;
      exit(EXIT_FAILURE);
    }
    int tempsFound = 0;
    int number;
    while (file >> number && tempsFound < length) {
      temperatures[tempsFound] = number;
      tempsFound++;
    }
    file.close();
}

