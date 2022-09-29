#include "WorkersTests.hpp"
#include "SuccessResponseTests.hpp"
#include "ErrorResponseTests.hpp"
#include "ResponseBuilderTests.hpp"

int main(){
    WorkersTests().test() ;
    SuccessResponseTests().test();
    ErrorResponseTests().test();
    ResponseBuilderTests().test();
}

