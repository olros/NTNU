#include <stdio.h>
#include <unistd.h>
#define NPROCESSES 5
void main() {
  long i;
  for (i = 0; i < NPROCESSES; i++) {
    int child_pid = fork();
    sleep(1);
    if (child_pid == 0) {
      printf("Hi, I'm a child process with PID #%d\n", getpid());
      return;
    }
  }
}