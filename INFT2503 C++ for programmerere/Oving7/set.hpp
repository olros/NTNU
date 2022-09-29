#include <vector>
#include <string>
#pragma once


class Set{
    private:
        std::vector<int> set;
    public:
        Set();

        Set(std::vector<int> set);
        bool contains(int i);
        void operator=(Set &other) ;
        Set operator+(Set &other) ;
        void operator+(int nr);
        int& operator[](int);
        int& operator[](size_t);
        size_t size() ;
        std::string print() ; 
};