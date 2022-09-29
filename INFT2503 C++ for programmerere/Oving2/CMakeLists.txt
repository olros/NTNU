cmake_minimum_required(VERSION 3.0.0)
project(Oving2 VERSION 0.1.0)

include(CTest)
enable_testing()

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++1y -Wall -Wextra")

add_executable(task1 task_1.cpp)
add_executable(task4 task_4.cpp)
add_executable(task5 task_5.cpp)
add_executable(task6 task_6.cpp)

set(CPACK_PROJECT_NAME ${PROJECT_NAME})
set(CPACK_PROJECT_VERSION ${PROJECT_VERSION})
include(CPack)
