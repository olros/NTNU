#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
// Complexity O(n)
double pow1(double x, int n) {
    if (n == 0) return 1;
    return x * pow1(x, n - 1);
}
// Complexity O(log n)
double pow2(double x, int n) {
    if (n == 0) return 1.0;
    else if (n % 2 == 1) return x * pow2(x * x, (n - 1) / 2);
    else return pow2(x * x, n / 2);
}
// Complexity O(1)
double pow3(double x, int n) {
    return pow(x, n);
}
void checkCorrect(double x, int n, double answer) {
    printf(pow1(x, n) == answer ? "true\n" : "false\n");
    printf(pow2(x, n) == answer ? "true\n" : "false\n");
    printf(pow3(x, n) == answer ? "true\n" : "false\n");
}
int main() {
    checkCorrect(3, 17, pow3(3, 17));
    checkCorrect(7, 4, pow3(7, 4));
    double x = 2;
    int n = 10000;
    int seconds = 1;
    int pow1Count = 0;
    time_t start, end;
    start = time(NULL);
    while(difftime(time(NULL), start) < seconds) {
        pow1(x, n);
        pow1Count++;
    }
    printf("Task 1: %d times per second\n", pow1Count / seconds);
    int pow2Count = 0;
    start = time(NULL);
    while(difftime(time(NULL), start) < seconds) {
        pow2(x, n);
        pow2Count++;
    }
    printf("Task 2: %d times per second\n", pow2Count / seconds);
    int pow3Count = 0;
    start = time(NULL);
    while(difftime(time(NULL), start) < seconds) {
        pow3(x, n);
        pow3Count++;
    }
    printf("Task 3: %d times per second\n", pow3Count / seconds);
}