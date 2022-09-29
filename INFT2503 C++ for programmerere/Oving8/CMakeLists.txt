cmake_minimum_required(VERSION 3.0.0)
project(Task_1 VERSION 0.1.0)

include(CTest)
enable_testing()

add_executable(Task_1 task_1.cpp)
add_executable(Task_2 task_2.cpp)

set(CPACK_PROJECT_NAME ${PROJECT_NAME})
set(CPACK_PROJECT_VERSION ${PROJECT_VERSION})
include(CPack)
