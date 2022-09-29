#include <stdio.h>
#include <stdlib.h>
int var1 = 0;
void main()
{
    int var2 = 1;
    int *var3 = (int *)malloc(sizeof(int)); // Note, since we are using malloc(), var3 will be a
// pointer into the heap!
// So the question is, where is the pointer stored?
    *var3 = 2;
    printf("Address: %p; Value: %d\n", &var1, var1);
    printf("Address: %p; Value: %d\n", &var2, var2);
    printf("Address: %p; Address: %p; Value: %d\n", &var3, var3, *var3);
}