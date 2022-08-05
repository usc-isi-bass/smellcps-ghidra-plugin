#include <stdio.h>
#include <stdlib.h>

int f(int a_0, int a_1)
{
    return a_0 + a_1;
}

int main(int argc, char *argv[])
{
    int a, b, ans;
    if (argc < 3) {
        fprintf(stderr, "usage: %s a b\n", argv[0]);
        return EXIT_FAILURE;
    }
    a = atoi(argv[1]);
    b = atoi(argv[2]);
    ans = f(a, b);
    printf("f(%d, %d) = %d\n", a, b, ans);
    
    return EXIT_SUCCESS;
}
